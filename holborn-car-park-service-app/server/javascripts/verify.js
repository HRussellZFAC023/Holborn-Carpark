const mongoose = require('mongoose');
const CarParks = require('../../server/data model/CarParks');

exports.verifyClientAuth = function(_id_carPark, callback){
    CarParks.findOne({_id: _id_carPark},function (err, carPark) {
        let err_code, err_desc;
        if(err){
            err_code = 505;
            err_desc = err;
            callback(err_code, err_desc);
            return;
        }
        if(!carPark) {
            callback(403, "Unauthorised access!");
            return;
        }
        callback(200, 'Authorised!');
        return;
    });
}
