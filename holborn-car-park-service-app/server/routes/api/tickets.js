const Router            = require('express-promise-router');
const router            = new Router();
const debug             = require('debug')('holborn-car-park-service-app: DB');
const UUID              = require('uuid/v4');

const G                 = require('../../javascripts/global');
const carpark_db        = require('../../databases/carpark_db_conn');
const query             = require('../../databases/queries');
const socket_functions  = require('../../sockets/socket_functions');
const verify            = require('../../javascripts/verify');
const json_resp         = require('../../javascripts/json_response');


/**
 * Function that gets exported as a module so that it can take the socket as an argument
 * and provide standard routing functionality at the same time by returning the router
 * @param io
 * @returns {module:express-promise-router}
 */
module.exports = function (io) {
    /**
     * Get all tickets (or all tickets based on some parameter/s)
     */
    router.get('/', verify.UserAuth, async function (req, res) {
        let db_res;

        if(typeof req.query._carpark_id  !== 'undefined'  &&
           typeof req.query.startDate    !== 'undefined'  &&
           typeof req.query.endDate      !== 'undefined'
        ){
            let params = [req.query._carpark_id, new Date(req.query.startDate).valueOf(), new Date(req.query.endDate).valueOf()];
            try{
                db_res = await carpark_db.query(query.api.tickets.get_all_specific, params);
            }
            catch (db_err) {
                debug(db_err);
                return res.status(500).json(json_resp.error.internal);
            }

            return res.status(200).send(db_res.rows);
        }

        try{
            db_res = await carpark_db.query(query.api.tickets.get_all);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }

        res.status(200).send(db_res.rows);
    });

    /**
     * Get a specific ticket
     */
    router.get('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
        let t_id = req.path.replace(/\//g, '');
        const params = [t_id];

        let db_res;
        try{
            db_res = await carpark_db.query(query.api.tickets.get_one, params);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }

        res.status(200).send(db_res.rows[0]);
    });

    /**
     * Delete all tickets
     */
    router.delete('/', verify.UserAuth, async function (req, res) {
        try{
            await carpark_db.query(query.api.tickets.delete_all);
        }
        catch(db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }

        socket_functions.emit_update(io);
        res.status(200).json(json_resp.success.delete);
    });

    /**
     * Delete a specific ticket
     */
    router.delete('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
        let t_id = req.path.replace(/\//g, '');
        const params = [t_id];

        try{
            await carpark_db.query(query.api.tickets.delete_one, params);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }

        socket_functions.emit_update(io);
        res.status(200).json(json_resp.success.delete);
    });

    /**
     * Create a ticket, car park id needs to be provided as well
     * so that the ticket get created attached to a car park
     */
    router.post('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
        let t_id = UUID();
        let c_id = req.path.replace(/\//g, '');
        const params = [t_id, Date.now(), false, true, c_id];

        try{
            await carpark_db.query(query.api.tickets.create, params);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }

        socket_functions.emit_update(io, c_id);
        res.status(200).send(json_resp.success.create);
    });

    /**
     * Update a ticket, possible parameters are:
     * @param date_out  Date.now() from JS
     * @param paid      true/false
     * @param valid     true/false
     * @param duration  double
     */
    router.put('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
        let t_id = req.path.replace(/\//g, '');

        if(typeof req.body.date_out === 'undefined' &&
           typeof req.body.paid     === 'undefined' &&
           typeof req.body.valid    === 'undefined' &&
           typeof req.body.duration === 'undefined' &&
           typeof req.body.amount_paid === 'undefined')
        {
            return res.status(500).json(json_resp.error.invalid_ticket_update);
        }

        if (typeof req.body.date_out !== 'undefined') {
            try{
                await carpark_db.query(query.api.tickets.update.date_out, [t_id, Date.now()]);
            }
            catch (db_err) {
                debug(db_err);
                return res.status(500).json(json_resp.error.internal);
            }
        }

        if (typeof req.body.paid !== 'undefined') {
            try{
                await carpark_db.query(query.api.tickets.update.paid, [t_id, req.body.paid]);
            }
            catch(db_err) {
                debug(db_err);
                return res.status(500).json(json_resp.error.internal);
            }
        }

        if (typeof req.body.valid !== 'undefined') {
            try{
                await carpark_db.query(query.api.tickets.update.valid, [t_id, req.body.valid]);
            }
            catch (db_err) {
                debug(db_err);
                return res.status(500).json(json_resp.error.internal);
            }
        }

        if (typeof req.body.duration !== 'undefined') {
            try{
                await carpark_db.query(query.api.carparks.update.duration, [t_id, req.body.duration]);
            }
            catch (db_err) {
                debug(db_err);
                return res.status(500).json(json_resp.error.internal);
            }
        }

        if (typeof req.body.amount_paid !== 'undefined') {
            try{
                await carpark_db.query(query.api.carparks.update.amount_paid, [t_id, req.body.amount_paid]);
            }
            catch (db_err) {
                debug(db_err);
                return res.status(500).json(json_resp.error.internal);
            }
        }

        return res.status(200).json(json_resp.success.update);
    });

    /**
     * Validates the ticket. After validation, the ticket can't be used anymore
     */
    router.post('/validate', verify.UserAuth, async function (req, res) {
        let t_id = req.body._id;
        let c_id = req.body._carpark_id; //provided by the carpark requesting validation
        const params = [t_id, c_id];

        let db_res;
        try{
            db_res = await carpark_db.query(query.api.tickets.validate, params);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }
        if (db_res.rows[0]._carpark_id !== c_id) return res.status(406).send('Ticket does\'t belong to the carpark');
        if (db_res.rows[0].valid !== true) return res.status(406).send('Ticket is invalid');
        if (db_res.rows[0].paid !== true) return res.status(403).send('Ticket is not paid');

        socket_functions.emit_update(io, c_id);
        res.status(200).send("Ticket valid. You can pass!");
    });

    return router;
};
