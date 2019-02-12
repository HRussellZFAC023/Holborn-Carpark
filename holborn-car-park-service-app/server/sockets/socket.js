const debug             = require('debug')('holborn-car-park-service-app: socket');

const socket_functions  = require('./socket_functions');


module.exports = function (io) {
    io.on('connection', function (socket) {
        let carpark_id = null;
        debug('Socket connected: ' + socket.handshake.address.substring(socket.handshake.address.lastIndexOf(':') + 1));
        socket_functions.authorise(socket, function (carparkid) {
            carpark_id = carparkid;
            socket.join(carpark_id);
            debug("joined in : " + carpark_id);
        });
        socket.on('fetch-ticket', async function (_id, callback) {
            await socket_functions.fetch_ticket_details(_id, carpark_id, callback);
        });
        socket.on('fetch-carpark-details', async function (callback) {
            await socket_functions.carpark_details_modified(carpark_id, callback)
        });
        socket.on('ticket-paid', async function (paid, duration, date_out, _id) {
            await socket_functions.ticket_paid(paid, duration, date_out, _id, carpark_id);
        });

    });
};