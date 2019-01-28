const mongoose = require('mongoose');
const CarParks = require('../data_model/CarParks');

exports.VerifyCarPark = function (req, resp, next) {
    CarParks.findOne({_id: req.body._id_carPark},function (err, carPark) {
        if(err) return resp.status(500).send("Something happened: " + err);
        if(!carPark) return resp.status(404).send("Oy we can't find what you're looking for. That car park doesn't exist.");
        next();
    });
};
