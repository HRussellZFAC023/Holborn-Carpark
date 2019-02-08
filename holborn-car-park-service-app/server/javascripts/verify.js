const debug = require('debug')('holborn-car-park-service-app: socket');

const db      = require ('../databases/carpark_db_conn');
const db_auth = require ('../databases/auth_db_conn');


exports.ClientAuth = function(_carpark_id, callback){
    const params = [_carpark_id];
    db.query('SELECT * FROM carparks WHERE _id = $1', params, function(db_err, db_res ){
        if(db_err){
            debug(db_err);
            return callback(505, db_err);
        }
        if(db_res.rowCount === 0) {
             return callback(403, "Unauthorised access!");
        }
        return callback(200, 'Authorised!');
    });
};
