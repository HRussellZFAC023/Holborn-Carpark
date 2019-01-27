const express = require('express');
const router = express.Router();
const mongoose = require('mongoose');
const Tickets = require('../data_model/Tickets');


router.get('/create', function(req, res) {
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

module.exports = router;
