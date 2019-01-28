const express = require('express');
const router = express.Router();
const CarParks = require('../../data_model/CarParks');
const Verify = require('../../javascripts/verify');

//Display all the car parks
router.get('/get_all', function(req, res) {
    CarParks.findOne({}, function(err, carparks){
        if (err) return res.status(500).send('Error on the server.');
        res.status(200).send(carparks)
    });
});

//Create a new car park
router.post('/create', function(req, resp){
    CarParks.create({
        name: req.body.name,
        hourly_price: req.body.hourly_price,
        address: req.body.address,
        postcode: req.body.postcode
    },(function(err, carpark){
        if(err) {
            return resp.status(err.code).send("There was a problem registering the user." + err);
        }
        resp.status(200).send("Car park created successfully: " + carpark);
    }));
});

router.post('/get_one', Verify.VerifyCarPark, function(req, resp){
    CarParks.findOne({_id: req.body._id_carPark},function (err, carPark) {
        return  resp.status(200).send(carPark);
    });
});


module.exports = router;
