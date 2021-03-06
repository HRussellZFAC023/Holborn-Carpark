const debug = require('debug')('holborn-car-park-service-app: socket');

const socket_functions = require('./socket_functions');


module.exports = function (io) {
    io.on('connection', function (socket) {
        let carpark_id = null;
        debug('Socket connected: ' + socket.handshake.address.substring(socket.handshake.address.lastIndexOf(':') + 1));
        socket_functions.authorise(socket, function (code, carparkid) {
            carpark_id = carparkid;
            if (code == 200) {
                socket.join(carpark_id);
                debug(socket.handshake.address.substring(socket.handshake.address.lastIndexOf(':') + 1) + " joined in : " + carpark_id);
            }
        });
        socket.on('fetch-ticket', async function (_id, callback) {
            await socket_functions.fetch_ticket_details(_id, carpark_id, callback);
        });
        socket.on('fetch-smartcard', async function (_id, callback) {
            await socket_functions.fetch_smartcard_details(_id, carpark_id, callback);
        });
        socket.on('fetch-carpark-details', async function (callback) {
            await socket_functions.carpark_details_modified(carpark_id, callback)
        });
        socket.on('ticket-paid', async function (paid, duration, date_out, _id, amount_paid) {
            await socket_functions.ticket_paid(paid, duration, date_out, _id, amount_paid, carpark_id);
        });
        socket.on("request-ticket", async function (callback) {
            await socket_functions.request_ticket(io, carpark_id, callback);
        });
        socket.on("ticket-exit", async function (t_id, callback) {
            await socket_functions.ticket_exit(io, t_id, carpark_id, callback);

        });
        socket.on("smartcard-enter", async function (s_id, callback) {
            await socket_functions.smartcard_enter(io, s_id,carpark_id, callback);

        });
        socket.on("smartcard-exit", async function (s_id, callback) {
            await socket_functions.smartcard_exit(io, s_id,carpark_id, callback);

        });


    });
};