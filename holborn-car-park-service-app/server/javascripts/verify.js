const mongoose = require('mongoose');
const CarParks = require('../../server/data model/CarParks');

exports.VerifyCarPark = function (req, resp, next) {
    CarParks.findOne({_id: req.body._id_carPark},function (err, carPark) {
        if(err) return resp.status(500).send("Something happened: " + err);
        if(!carPark) return resp.status(404).send("Oy we can't find what you're looking for. That car park doesn't exist.");
        next();
    });
};
exports.VerifyClientAuth = function(_id_carPark, callback){
    CarParks.findOne({_id: _id_carPark},function (err, carPark) {
        let err_code, err_desc;
        if(err){
            err_code = 505;
            err_desc = err;
            callback(err_code, err_desc);
            return;
        }
        if(!carPark) {
            callback(404, "Unauthorised access!");
            return;
        }
        callback(200, 'Authorised!');
        return;
    });
}
