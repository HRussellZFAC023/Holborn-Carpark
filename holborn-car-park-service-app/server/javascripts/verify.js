const debug = require('debug')('holborn-car-park-service-app: socket');

const db    = require ('../databases/carpark_db_conn');


exports.verifyClientAuth = function(_carpark_id, callback){
    const params = [_carpark_id];
    db.query('SELECT * FROM carparks WHERE _id = $1', params, function(db_err, db_res ){
        let err_code, err_desc;
        if(db_err){
            debug(db_err);
            err_code = 505;
            err_desc = db_err;
            return callback(err_code, err_desc);
        }
        if(db_res.rowCount === 0) {
             return callback(403, "Unauthorised access!");
        }
        return callback(200, 'Authorised!');
    });
};
