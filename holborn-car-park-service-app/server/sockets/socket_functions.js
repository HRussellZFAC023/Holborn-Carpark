const db = require ('../databases/carpark_db_conn');
const verif = require('../javascripts/verify');
const queries = require('../databases/queries');
const debug = require('debug')('holborn-car-park-service-app: socket');
exports.carpark_details_modified = function (_id, callback){
    db.query(queries.sockets.ticket_valid_count,[_id],function(db_err, db_res_t){
        if(db_err){
            console.log(db_err);
        }
        db.query(queries.sockets.carpark_details, [_id],function(db_err, db_res_cp) {
            if (db_err) {
                console.log(db_err);
            }
            const current_parking_places = db_res_cp.rows[0].parking_places - db_res_t.rows[0].count;
            return callback(current_parking_places, db_res_cp.rows[0].hour_rate, db_res_cp.rows[0].happy_hour_start,db_res_cp.rows[0].happy_hour_end);
        });
    });
};
exports.authorise1 = function (_idCarPark, callback) {
    verif.verifyClientAuth(_idCarPark, function (code, desc) {
        callback(code, desc);
    });
    return _idCarPark;
};
exports.authorise = function  (socket, carparkid_cb) {
    socket.on('authorisation', function (_idCarPark, callback) {
        verif.verifyClientAuth(_idCarPark, function (code, desc) {
            callback(code, desc);
            carparkid_cb(_idCarPark);
        });
    });
};
exports.emit_update= function(io, _id){
    io.sockets.in(_id).emit('update-carpark-details');
};
exports.emit_update = function(io){
    io.sockets.emit('update-carpark-details');
};