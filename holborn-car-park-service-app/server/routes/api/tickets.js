const express = require('express');
const router = express.Router();
const db = require('../../javascripts/pg_conn');
const debug = require('debug')('holborn-car-park-service-app: DB');
const UUID = require('uuid/v4');
const query = require('../../javascripts/queries');

const uuid_regex = '[0-9A-Za-z]{8}-[0-9A-Za-z]{4}-4[0-9A-Za-z]{3}-[89ABab][0-9A-Za-z]{3}-[0-9A-Za-z]{12}';


//Get all tickets
router.get('/', function (req, res) {
    db.query(query.api.tickets.get_all, function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send(db_res.rows);
    });
});

//Delete all tickets
router.delete('/', function (req, res) {
    db.query(query.api.tickets.delete_all, function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send('All tickets deleted');
    });
});




//Gets a specific ticket
router.get('/' + uuid_regex, function (req, res) {
    let t_id = req.path.replace(/\//g, '');
    const params = [t_id];

    db.query(query.api.tickets.get_one, params, function (db_err, db_res) {
         if (db_err) {
             debug(db_err);
             return res.status(500).send('Error on the server:' + db_err);
         }

         res.status(200).send(db_res.rows[0]);
     });
});

//Create a ticket (attached to a carpark id)
router.post('/' + uuid_regex, function (req, res) {
    let t_id = UUID();
    let c_id = req.path.replace(/\//g, '');
    const params = [t_id, Date.now(), false, true, c_id];

    db.query(query.api.tickets.create, params, function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send('Success! Ticket with id  ' + t_id + '  created at car park ' + c_id);
    });
});

//Update a ticket
router.put('/' + uuid_regex, function (req, res) {
    let t_id = req.path.replace(/\//g, '');

    if (typeof req.body.date_out !== 'undefined') {
        db.query(query.api.tickets.update.date_out, [t_id, Date.now()], function (db_err, db_res) {
            if (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }

            res.status(200).send('Updated! Ticket with id  ' + t_id + '  updated');
        });
    }else if (typeof req.body.paid !== 'undefined') {
        db.query(query.api.tickets.update.paid, [t_id, req.body.paid], function (db_err, db_res) {
            if (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }

            res.status(200).send('Updated! Ticket with id  ' + t_id + '  updated');
        });
    }else if (typeof req.body.valid !== 'undefined') {
        db.query(query.api.tickets.update.paid, [t_id, req.body.valid], function (db_err, db_res) {
            if (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }

            res.status(200).send('Updated! Ticket with id  ' + t_id + '  updated');
        });
    }else{
        res.status(500).send('Possible body params are: \ndate_out (Date.now()),\npaid (true/false),\nvalid (true/false)');
    }
});

//Delete a ticket
router.delete('/' + uuid_regex, function (req, res) {
    let t_id = req.path.replace(/\//g, '');
    const params = [t_id];

    db.query(query.api.tickets.delete_one, params, function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send('Deleted! Ticket with id  ' + t_id + '  deleted');
    });
});


//Validates the ticket. After validation, the ticket can't be used anymore
router.post('/validate', function (req, res) {
    let t_id = req.body._id;
    let c_id = req.body._carpark_id; //provided by the carpark requesting validation
    const params = [t_id];


    db.query(query.api.tickets.validate, params, function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }
        if(db_res.rows[0]._carpark_id !== c_id)return res.status(406).send('Ticket does\'t belong to the carpark');
        if(db_res.rows[0].valid !== true) return res.status(406).send('Ticket is invalid');
        if(db_res.rows[0].paid !== true) return res.status(403).send('Ticket is unpaid');

        res.status(200).send("Ticket valid. You can pass!");
    });
});


module.exports = router;
