const moment    = require('moment');
const debug     = require('debug')('holborn-car-park-service-app: socket_functions');

const carpark_db = require('../databases/carpark_db_conn');
const verify     = require('../javascripts/verify');
const queries    = require('../databases/queries');


exports.carpark_details_modified = async function (_id, callback) {
    let db_res;
    try {
        db_res = await carpark_db.query(queries.sockets.ticket_valid_count, [_id]);
    }
    catch (db_err) {
        debug(db_err);
    }
    let count =  db_res.rows[0].count;

    try {
        db_res = await carpark_db.query(queries.sockets.carpark_details, [_id]);
    }
    catch (db_err) {
        debug(db_err);
    }

    const avail_places = db_res.rows[0].parking_places - count;

    return callback(avail_places, db_res.rows[0].hour_rate, db_res.rows[0].happy_hour_start, db_res.rows[0].happy_hour_end);
};

exports.fetch_ticket_details = async function (_id, carpark_id, callback) {
    const params = [_id, carpark_id];
    let db_res;
    try {
        db_res = await carpark_db.query(queries.sockets.ticket_details, params);
    }
    catch (db_err) {
        return callback(500, db_err.message, null);
    }

    if (db_res.rowCount === 0) {
        return callback(404, "Ticket not found: " + _id, null);
    }
    if (!db_res.rows[0].valid) {
        return callback(406, "Ticket is invalid: " + _id, null);
    }
    if (db_res.rows[0].paid) {
        return callback(406, "Ticket is already paid! " + _id, null);
    }

    const ticket = {
        _id: db_res.rows[0]._id,
        date_in: db_res.rows[0].date_in,
        date_out: db_res.rows[0].date_out,
        price: db_res.rows[0].price,
        duration: db_res.rows[0].duration,
        duration_paying_for: 0
    };

    const now = moment();
    const duration = moment.duration(now.diff(ticket.date_in));
    const minutes = duration.asMinutes();
    const date = new Date();

    ticket.duration = minutes.toFixed(0);
    ticket.date_out = date.toISOString();
    ticket.duration_paying_for = Math.ceil(duration.asHours()).toFixed(0);
    ticket.price = (db_res.rows[0].hour_rate * Math.ceil(duration.asHours())).toFixed(2);
    // console.log("minutes:" + ticket.duration);
    // console.log("rounded hours to half:" + Math.ceil(duration.asHours()));
    // console.log("rounded hours to half:" + Math.ceil(duration.asHours())*60);
    // console.log("hours:" + duration.asHours());
    return callback(200, "Ticket is valid: " + _id, ticket);
};

exports.ticket_paid = async function (paid, duration, date_out, _id, carpark_id) {
    const params = [paid, duration, date_out, _id, carpark_id];
    try {
        await carpark_db.query(queries.sockets.ticket_details_update, params);
    }
    catch (db_err) {
        debug(db_err);
    }
};

exports.authorise = function (socket, carparkid_cb) {
    socket.on('authorisation', function (_idCarPark, callback) {
        verify.ClientAuth(_idCarPark, function (code, desc) {
            callback(code, desc);
            carparkid_cb(_idCarPark);
        });
    });
};

exports.emit_update = function (io, _id) {
    io.sockets.in(_id).emit('update-carpark-details');
};

exports.emit_update = function (io) {
    io.sockets.emit('update-carpark-details');
};