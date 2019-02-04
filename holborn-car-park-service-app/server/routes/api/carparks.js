const express = require('express');
const router = express.Router();
const db = require('../../javascripts/pg_conn');
const debug = require('debug')('holborn-car-park-service-app: DB');
const UUID = require('uuid/v4');




//Get all tickets
router.get('/', function (req, res) {
    db.query('SELECT * FROM carparks', function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send(db_res.rows);
    });
});

//Delete all tickets
router.delete('/', function (req, res) {
    db.query('DELETE FROM carparks', function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send('All tickets deleted');
    });
});




//Gets a specific ticket
router.get('/[0-9A-Za-z]{8}-[0-9A-Za-z]{4}-4[0-9A-Za-z]{3}-[89ABab][0-9A-Za-z]{3}-[0-9A-Za-z]{12}', function (req, res) {
    let c_id = req.path.replace(/\//g, '');
    const params = [c_id];

    db.query('SELECT * FROM carparks WHERE _id = $1', params, function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send(db_res.rows[0]);
    });
});

//Create a ticket (attached to a carpark id)
router.post('/', function (req, res) {
    let c_id = UUID();
    let name = req.body.name;
    let hour_rate = req.body.hour_rate;
    let postcode = req.body.postcode;
    let total_spaces = req.body.parking_places;
    const params = [c_id, name, hour_rate, postcode, total_spaces];

    db.query('INSERT INTO carparks VALUES ($1, $2, $3, $4, $5)', params, function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send('Success! Car park with id  ' + c_id + '  created');
    });
});

//Update a ticket
router.put('/[0-9A-Za-z]{8}-[0-9A-Za-z]{4}-4[0-9A-Za-z]{3}-[89ABab][0-9A-Za-z]{3}-[0-9A-Za-z]{12}', function (req, res) {
    let c_id = req.path.replace(/\//g, '');

    if (typeof req.body.name !== 'undefined') {
        db.query('UPDATE carparks SET name = $2 WHERE _id = $1', [c_id, req.body.name], function (db_err, db_res) {
            if (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }

            res.status(200).send('Updated! Car park with id  ' + c_id + '  updated');
        });
    }else if (typeof req.body.hour_rate !== 'undefined') {
        db.query('UPDATE carparks SET hour_rate = $2 WHERE _id = $1', [c_id, req.body.hour_rate], function (db_err, db_res) {
            if (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }

            res.status(200).send('Updated! Car park with id  ' + c_id + '  updated');
        });
    }else if (typeof req.body.postcode !== 'undefined') {
        db.query('UPDATE carparks SET postcode = $2) WHERE _id = $1', [c_id, req.body.postcode], function (db_err, db_res) {
            if (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }

            res.status(200).send('Updated! Car park with id  ' + c_id + '  updated');
        });
    }else if (typeof req.body.parking_places !== 'undefined') {
        db.query('UPDATE carparks SET parking_places = $2) WHERE _id = $1', [c_id, req.body.parking_places], function (db_err, db_res) {
            if (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }

            res.status(200).send('Updated! Car park with id  ' + c_id + '  updated');
        });
    }else{
        res.status(500).send('Possible body params are: \nname ("Egham"),\nhour_rate (2.1),\npostcode ("HH000HH")');
    }
});

//Delete a ticket
router.delete('/[0-9A-Za-z]{8}-[0-9A-Za-z]{4}-4[0-9A-Za-z]{3}-[89ABab][0-9A-Za-z]{3}-[0-9A-Za-z]{12}', function (req, res) {
    let t_id = req.path.replace(/\//g, '');
    const params = [t_id];

    db.query('DELETE FROM carparks WHERE _id = $1', params, function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send('Deleted! Ticket with id  ' + t_id + '  deleted');
    });
});


module.exports = router;
