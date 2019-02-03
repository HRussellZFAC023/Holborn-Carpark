const express = require('express');
const router = express.Router();
const Tickets = require('../../data model/Tickets');
const verify = require('../../javascripts/verify');

router.get('/get_all', function (req, res) {
    Tickets.findOne({}, function (err, tickets) {
        if (err) return res.status(500).send('Error on the server:' + err);
        res.status(200).send(tickets)
    });
});

//Verifies if the car park exists and then it creates an new ticket
router.post('/generate', verify.verifyCarPark, function (req, res) {
    Tickets.create({
        date_in: Date.now(),
        date_out: null,
        _id_carPark: req.body._id_carPark
    }, function (err, ticket) {
        if (err) return res.status(500).send('Error on the server:' + err);
        res.status(200).send(ticket);
    });
});

//Verifies if the car park exists and marks the ticket as paid
router.post('/get_one', verify.verifyCarPark, function (req, res) {
    Tickets.findOne({_id: req.body._id}, function (err, ticket) {
        if (err) return res.status(500).send('Error on the server:' + err);
        if (!ticket) return res.status(404).send('Ticket not found');
        if (!ticket.valid) return res.status(406).send('Ticket is invalid');
        res.status(200).send(ticket)
    });
});

//Sets whether or not the ticket has been paid
router.post('/set_paid', verify.verifyCarPark, function (req, res) {
    Tickets.findOneAndUpdate(
        {_id: req.body._id},
        {paid: req.body.paid},
        {new: true},
        function (err, ticket) {
            if (err) return res.status(500).send('Error on the server:' + err);
            res.status(200).send(ticket)
        });
});

//Validates the ticket. After validation, the ticket can't be used anymore
router.post('/validate', verify.verifyCarPark, function (req, res) {
    Tickets.findOneAndUpdate({_id: req.body._id}, {valid: false}, function (err, ticket) {
        if (err) return res.status(500).send('Error on the server:' + err);
        if (!ticket) return res.status(404).send('Ticket not found');
        if (req.body._id_carPark_ticket === ticket._id_carPark) return res.status(406).send('Ticket doest belong to the carpark');
        if (!ticket.valid) return res.status(406).send('Ticket is invalid');
        if (!ticket.paid) return res.status(403).send('Ticket is unpaid');
        res.status(200).send("Ticket valid. You can pass!")
    });
});
module.exports = router;
