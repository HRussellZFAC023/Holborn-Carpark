const express           = require('express');
const router            = express.Router();
const debug             = require('debug')('holborn-car-park-service-app: DB');
const UUID              = require('uuid/v4');

const G                 = require('../../javascripts/global');
const carpark_db        = require('../../databases/carpark_db_conn');
const query             = require('../../databases/queries');
const socket_functions  = require('../../sockets/socket_functions');
const verify            = require('../../javascripts/verify');


module.exports = function (io) {
    //Get all tickets
    router.get('/', verify.UserAuth, async function (req, res) {
        let db_res;
        try{
            db_res = await carpark_db.query(query.api.tickets.get_all);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send(db_res.rows);
    });

    //Delete all tickets
    router.delete('/', verify.UserAuth, async function (req, res) {
        try{
            await carpark_db.query(query.api.tickets.delete_all);
        }
        catch(db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        socket_functions.emit_update(io);
        res.status(200).send('All tickets deleted');
    });

    //Gets a specific ticket
    router.get('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
        let t_id = req.path.replace(/\//g, '');
        const params = [t_id];

        let db_res;
        try{
            db_res = await carpark_db.query(query.api.tickets.get_one, params);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        res.status(200).send(db_res.rows[0]);
    });

    //Create a ticket (attached to a carpark id)
    router.post('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
        let t_id = UUID();
        let c_id = req.path.replace(/\//g, '');
        const params = [t_id, Date.now(), false, true, c_id];

        try{
            await carpark_db.query(query.api.tickets.create, params);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        socket_functions.emit_update(io, c_id);
        res.status(200).send('Success! Ticket with id  ' + t_id + '  created at car park ' + c_id);
    });

    //Update a ticket
    router.put('/' + G.uuid_regex, async function (req, res) {
        let t_id = req.path.replace(/\//g, '');

        if(typeof req.body.date_out === 'undefined' &&
           typeof req.body.paid     === 'undefined' &&
           typeof req.body.valid    === 'undefined' &&
           typeof req.body.duration === 'undefined')
        {
            return res.status(500).send('Possible body params are: \ndate_out (Date.now()),\npaid (true/false),\nvalid (true/false)');
        }

        if (typeof req.body.date_out !== 'undefined') {
            try{
                await carpark_db.query(query.api.tickets.update.date_out, [t_id, Date.now()]);
            }
            catch (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }
        }

        if (typeof req.body.paid !== 'undefined') {
            try{
                await carpark_db.query(query.api.tickets.update.paid, [t_id, req.body.paid]);
            }
            catch(db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }
        }

        if (typeof req.body.valid !== 'undefined') {
            try{
                await carpark_db.query(query.api.tickets.update.valid, [t_id, req.body.valid]);
            }
            catch (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }
        }

        if (typeof req.body.duration !== 'undefined') {
            try{
                await carpark_db.query(query.api.carparks.update.duration, [t_id, req.body.duration]);
            }
            catch (db_err) {
                debug(db_err);
                return res.status(500).send('Error on the server:' + db_err);
            }
        }

        return res.status(200).send('Updated! Ticket with id  ' + t_id + '  updated');
    });

    //Delete a ticket
    router.delete('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
        let t_id = req.path.replace(/\//g, '');
        const params = [t_id];

        try{
            await carpark_db.query(query.api.tickets.delete_one, params);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        socket_functions.emit_update(io);
        res.status(200).send('Deleted! Ticket with id  ' + t_id + '  deleted');
    });


    //Validates the ticket. After validation, the ticket can't be used anymore
    router.post('/validate', verify.UserAuth, async function (req, res) {
        let t_id = req.body._id;
        let c_id = req.body._carpark_id; //provided by the carpark requesting validation
        const params = [t_id];

        let db_res;
        try{
            db_res = await carpark_db.query(query.api.tickets.validate, params);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }
        if (db_res.rows[0]._carpark_id !== c_id) return res.status(406).send('Ticket does\'t belong to the carpark');
        if (db_res.rows[0].valid !== true) return res.status(406).send('Ticket is invalid');
        if (db_res.rows[0].paid !== true) return res.status(403).send('Ticket is unpaid');

        socket_functions.emit_update(io, c_id);
        res.status(200).send("Ticket valid. You can pass!");
    });

    return router;
};
