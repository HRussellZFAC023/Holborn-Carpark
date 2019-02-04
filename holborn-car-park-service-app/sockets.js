const verif = require("./server/javascripts/verify");
const moment = require('moment');
const sockets = {};
const Tickets = require("./server/data model/Tickets");
const CarParks = require("./server/data model/CarParks");
const Global_Variables = require("./server/javascripts/gloval_variables");
const debug = require('debug')('holborn-car-park-service-app: socket');
const db = require ('./server/javascripts/pg_conn');
const queries = require('./server/javascripts/queries');

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
            const params = [_id];
            //db.query('SELECT * FROM tickets INNER JOIN carparks ON tickets._carpark_id=carparks._id WHERE tickets._id = $1')
            db.query(queries.sockets.ticket_details, function(db_err, db_res){
                if(db_err){

                }

            });
            // Tickets.findOne({_id : _id, _id_carPark : carpark_id}).populate('_id_carPark', 'hourly_price').exec(function(err, ticket) {
            //     if (err)
            //         return callback(500, "Error on server");
            //     if (!ticket)
            //         return callback(404, "Ticket not found");
            //     if (!ticket.valid) return callback(406, "Ticket is invalid");
            //     const now = moment();
            //     const duration = moment.duration(now.diff(ticket.date_in));
            //     const minutes = duration.asMinutes();
            //     ticket.duration = minutes.toFixed(2);
            //     const date = new Date();
            //     ticket.date_check_out =  date.toISOString();
            //     ticket.price = (ticket._id_carPark.hourly_price * duration.asHours().toFixed(2)).toFixed(2);
            //     ticket.save(function (err) {
            //         if(err) throw  err;
            //     });
            //     return callback(200, ticket);
            //
            // },);
        });
        socket.on('fetch-carpark-details', function(callback){
            // CarParks.findOne({_id : carpark_id}, function (err, carpark) {
            //     if(err) throw err;
            //     Tickets.countDocuments({valid : true}, function (err, count){
            //         if(err) throw err;
            //       carpark.current_parking_places = carpark.parking_places - count;
            //       carpark.save(function (err) {
            //           if(err) throw  err;
            //       });
            //       return callback(carpark.current_parking_places, carpark.hourly_price);
            //     });
            // })
        })
    });

};

module.exports = sockets;