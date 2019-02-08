const moment = require('moment');

const db = require('../databases/carpark_db_conn');
const verif = require('../javascripts/verify');
const queries = require('../databases/queries');

exports.carpark_details_modified = function (_id, callback) {
    db.query(queries.sockets.ticket_valid_count, [_id], function (db_err, db_res_t) {
        if (db_err) {
            console.log(db_err);
        }
        db.query(queries.sockets.carpark_details, [_id], function (db_err, db_res_cp) {
            if (db_err) {
                console.log(db_err);
            }
            const current_parking_places = db_res_cp.rows[0].parking_places - db_res_t.rows[0].count;
            return callback(current_parking_places, db_res_cp.rows[0].hour_rate, db_res_cp.rows[0].happy_hour_start, db_res_cp.rows[0].happy_hour_end);
        });
    });
};

exports.fetch_ticket_details = function (_id, carpark_id, callback) {
    const params = [_id, carpark_id];
    db.query(queries.sockets.ticket_details, params, function (db_err, db_res) {
        if (db_err) {
            return callback(500, db_err.message, null);
        }
        if (db_res.rowCount === 0) {
            return callback(404, "Ticket not found: " + _id, null);
        }
        if (!db_res.rows[0].valid) {
            return callback(406, "Ticket is invalid: " + _id, null);
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
        console.log("minutes:" + ticket.duration);
        console.log("rounded hours to half:" + Math.ceil(duration.asHours()));
        console.log("rounded hours to half:" + Math.ceil(duration.asHours())*60);
        console.log("hours:" + duration.asHours());
        const params = [_id, ticket.duration, ticket.date_out];
        // db.query(queries.sockets.ticket_details_update, params, function (db_err) {
        //     if (db_err) debug(db_err);
        // });
        return callback(200, "Ticket is valid: " + _id, ticket);

    });
};

exports.authorise1 = function (_idCarPark, callback) {
    verif.verifyClientAuth(_idCarPark, function (code, desc) {
        callback(code, desc);
    });
    return _idCarPark;
};

exports.authorise = function (socket, carparkid_cb) {
    socket.on('authorisation', function (_idCarPark, callback) {
        verif.verifyClientAuth(_idCarPark, function (code, desc) {
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