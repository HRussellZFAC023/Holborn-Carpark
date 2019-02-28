const debug = require('debug')('holborn-car-park-service-app: socket');

const carpark_db    = require ('../databases/carpark_db_conn');
const user_db       = require ('../databases/auth_db_conn');


/**
 * Function that verifies client software connecting to the server
 * @param _carpark_id
 * @param callback
 * @constructor
 */
exports.ClientAuth = function(_carpark_id, callback){
    const params = [_carpark_id];
    carpark_db.query('SELECT * FROM carparks WHERE _id = $1', params, function(db_err, db_res ){
        if(db_err){
            debug("Failed authorisation for "+ _carpark_id)
            return callback(505, db_err);
        }
        if(db_res.rowCount === 0) {
             return callback(403, "Unauthorised access!");
        }
        return callback(200, 'Authorised!');
    });
};

/**
 * Function that verifies any user action
 * @param req
 * @param res
 * @param next
 * @constructor
 */
exports.UserAuth = function(req, res, next){
    if(req.session && req.session.username) {
        user_db.query('SELECT * FROM users WHERE username = $1', [req.session.username], function (db_err, db_res) {
            if(db_err){
                debug(db_err);
                req.session.destroy(function () {
                    res.redirect('/login');
                });
            }
            if(!db_res.rowCount) {
                req.session.destroy(function () {
                    res.redirect('/login');
                });
            }
            else{
                next();
            }
        })
    }

    else res.redirect('/login');
};