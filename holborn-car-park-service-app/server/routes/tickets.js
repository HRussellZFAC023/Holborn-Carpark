

var express = require('express');
var router = express.Router();
var mongoose = require('mongoose');
var Tickets = require('../data_model/Tickets');


router.get('/', function(req, res) {
    // Tickets.findOne({}, function(err, tickets){
    //     if (err) return res.status(500).send('Error on the server.');
    //     res.status(200).send(tickets)
    // });
    Tickets.create({
        _id_carPark: mongoose.Types.ObjectId('5c4c5d14ada592395a3151a2')
    },(function(err, ticket){
        if(err) {
            return res.status(500).send(err);
        }
        // var token = jwt.sign({ id: user._id }, global_var.secret_key, {
        //     expiresIn: '24h' // expires in 24 hours
        // });
        // res.status(200).send({ auth: true, token: token });
        res.status(200).send("User registered successfully");
    }));
});


router.post('/login', function(req, res) {
    User.findOne({username: req.body.username}, function (err, user) {
       if (err) return res.status(500).send('Error on the server.');
       if (!user) return res.status(404).send('No user found.');
            user.comparePassword(req.body.password, function (err, isMatch) {
                if (err) throw err;
                if (!isMatch) return res.status(401).send({auth: false, token: null});
                var token = jwt.sign({id: user._id}, global_var.secret_key, {
                    expiresIn: "24h"
                });
                res.status(200).send({auth: true, token: token})
            });

   });
});
router.post('/register', function(req, res, next) {
     User.create({
        username: req.body.username,
        password: req.body.password
    },(function(err, user){
        if(err) {
            return res.status(500).send("There was a problem registering the user.");
        }
        // var token = jwt.sign({ id: user._id }, global_var.secret_key, {
        //     expiresIn: '24h' // expires in 24 hours
        // });
        // res.status(200).send({ auth: true, token: token });
         res.status(200).send("User registered successfully");
    }));

});
module.exports = router;
