const debug = require('debug')('holborn-car-park-service-app: socket');
const socket_functions = require('./socket_functions');


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
            socket_functions.fetch_ticket_details(_id, carpark_id, callback);
        });
        socket.on('fetch-carpark-details', function (callback) {
            socket_functions.carpark_details_modified(carpark_id, callback)
        });

    });
};