const mongoose = require('mongoose');
const CarParks = require('../../server/data model/CarParks');
const db = require ('../javascripts/pg_conn');
const debug = require('debug')('holborn-car-park-service-app: socket');

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
