const verif = require("./server/javascripts/verify");
const moment = require('moment');
const sockets = {};
const Tickets = require("./server/data model/Tickets");
const Global_Variables = require("./server/javascripts/gloval_variables");

sockets.init = function (server) {
    // socket.io setup
    const io = require('socket.io').listen(server);
    io.sockets.on('connection', function (socket) {
        console.log('Socket connected: ' + socket.handshake.address);
        var carpark_id;

        socket.on('authorisation', function (_idCarPark, callback) {
            verif.verifyClientAuth(_idCarPark, function (code, desc) {
               callback(code, desc);
               carpark_id = _idCarPark;
            });
        });

        socket.on('fetch-ticket', function (_id, callback) {
            Tickets.findOneAndUpdate({_id : _id, _id_carPark : carpark_id}).populate('_id_carPark', 'hourly_price').exec(function(err, ticket) {
                if (err)
                    return callback(500, "Error on server");
                if (!ticket)
                    return callback(404, "Ticket not found");
                if (!ticket.valid) return callback(406, "Ticket is invalid");
                const now = moment();
                const duration = moment.duration(now.diff(ticket.date_in));
                const hours = duration.asHours();
                ticket.duration = hours.toFixed(2);
                const date = new Date();
                ticket.date_check_out =  date.toISOString();
                ticket.price = (ticket._id_carPark.hourly_price * hours).toFixed(2);
                ticket.save(function (err) {
                    if(err) throw  err;
                });
                return callback(200, ticket);

            },);
        });

    });

};

module.exports = sockets;