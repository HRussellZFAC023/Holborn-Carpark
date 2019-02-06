const moment            = require('moment');
const debug             = require('debug')('holborn-car-park-service-app: socket');
const db                = require('../databases/carpark_db_conn');
const queries           = require('../databases/queries');
const socket_functions  = require('./socket_functions');


module.exports = function (io) {
    io.on('connection', function (socket) {
        var carpark_id = null;
        debug('Socket connected: ' + socket.handshake.address);
        socket_functions.authorise(socket, function (carparkid) {
            carpark_id = carparkid;
            socket.join(carpark_id);
            debug("joined in : " + carpark_id);
        });
        socket.on('fetch-ticket', function (_id, callback) {
            const params = [_id, carpark_id];
            db.query(queries.sockets.ticket_details, params, function (db_err, db_res) {
                if (db_err) {
                    console.log(db_err);
                    return callback(500, db_err);
                }
                if (db_res.rowCount === 0) {
                    return callback(404, "Ticket not found");
                }
                if (!db_res.rows[0].valid) {
                    return callback(406, "Ticket is invalid");
                }
                const ticket = {
                    _id: db_res.rows[0]._id,
                    date_in: db_res.rows[0].date_in,
                    date_out: db_res.rows[0].date_out,
                    price: db_res.rows[0].price,
                    duration: db_res.rows[0].duration
                };
                const now = moment();
                const duration = moment.duration(now.diff(ticket.date_in));
                const minutes = duration.asMinutes();
                const date = new Date();
                ticket.duration = minutes.toFixed(2);
                ticket.date_out = date.toISOString();
                ticket.price = (db_res.rows[0].hour_rate * duration.asHours().toFixed(2)).toFixed(2);
                console.log("minutes:" + ticket.duration);
                console.log("minutes:" + ticket.duration);
                const params = [_id, ticket.duration, ticket.date_out];
                db.query(queries.sockets.ticket_details_update, params, function (db_err, db_res) {
                    if (db_err) debug(db_err);
                });
                return callback(200, ticket);

            });
        });
        socket.on('fetch-carpark-details', function (callback) {
            socket_functions.carpark_details_modified(carpark_id, callback)
        });

    });
};