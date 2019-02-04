const express = require('express');
const router = express.Router();
const db = require('../../javascripts/pg_conn');
const debug = require('debug')('holborn-car-park-service-app: DB');
const UUID = require('uuid/v4');
const query = require('../../javascripts/queries');

const uuid_regex = '[0-9A-Za-z]{8}-[0-9A-Za-z]{4}-4[0-9A-Za-z]{3}-[89ABab][0-9A-Za-z]{3}-[0-9A-Za-z]{12}';





//Get all tickets
router.get('/', function (req, res) {
    db.query(query.api.carparks.get_all, function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send(db_res.rows);
    });
});

//Delete all tickets
router.delete('/', function (req, res) {
    db.query(query.api.carparks.delete_all, function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send('All tickets deleted');
    });
});




//Gets a specific ticket
router.get('/' + uuid_regex, function (req, res) {
    let c_id = req.path.replace(/\//g, '');
    const params = [c_id];

    db.query(query.api.carparks.get_one, params, function (db_err, db_res) {
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

    db.query(query.api.carparks.create, params, function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send('Success! Car park with id  ' + c_id + '  created');
    });
});

//Update a ticket
router.put('/' + uuid_regex, function (req, res) {
    let c_id = req.path.replace(/\//g, '');

    if (typeof req.body.name !== 'undefined') {
        db.query(query.api.carparks.update.name, [c_id, req.body.name], function (db_err, db_res) {
            if (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }

            res.status(200).send('Updated! Car park with id  ' + c_id + '  updated');
        });
    }else if (typeof req.body.hour_rate !== 'undefined') {
        db.query(query.api.carparks.update.hour_rate, [c_id, req.body.hour_rate], function (db_err, db_res) {
            if (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }

            res.status(200).send('Updated! Car park with id  ' + c_id + '  updated');
        });
    }else if (typeof req.body.postcode !== 'undefined') {
        db.query(query.api.carparks.update.postcode, [c_id, req.body.postcode], function (db_err, db_res) {
            if (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }

            res.status(200).send('Updated! Car park with id  ' + c_id + '  updated');
        });
    }else if (typeof req.body.parking_places !== 'undefined') {
        db.query(query.api.carparks.update.parking_places, [c_id, req.body.parking_places], function (db_err, db_res) {
            if (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }

            res.status(200).send('Updated! Car park with id  ' + c_id + '  updated');
        });
    }else if (typeof req.body.duration !== 'undefined') {
        db.query(query.api.carparks.update.duration, [c_id, req.body.duration], function (db_err, db_res) {
            if (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }

            res.status(200).send('Updated! Car park with id  ' + c_id + '  updated');
        });
    }else{
        res.status(500).send(`Possible body params are: \n
                                            name ("Egham"),\n
                                            hour_rate (2.1),\n
                                            postcode ("HH000HH")`
        );
    }
});

//Delete a ticket
router.delete('/' + uuid_regex, function (req, res) {
    let t_id = req.path.replace(/\//g, '');
    const params = [t_id];

    db.query(query.api.carparks.delete_one, params, function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send('Deleted! Ticket with id  ' + t_id + '  deleted');
    });
});


module.exports = router;
