const db = require ('../databases/carpark_db_conn');
const queries = require('../databases/queries');
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
            return callback(current_parking_places, db_res_cp.rows[0].hour_rate);
        });
    });
};