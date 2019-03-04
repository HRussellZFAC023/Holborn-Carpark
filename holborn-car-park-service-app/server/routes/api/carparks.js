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
module.exports = function(io) {
    /**
     * Get all car parks
     */
    router.get('/', verify.UserAuth, async function (req, res) {
        let db_res;
        try{
            db_res = await carpark_db.query(query.api.carparks.get_all);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }

        res.status(200).send(db_res.rows);
    });

    /**
     * Gets a specific car park
     */
    router.get('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
        let c_id = req.path.replace(/\//g, '');
        const params = [c_id];

        let db_res;
        try{
            db_res = await carpark_db.query(query.api.carparks.get_one, params)
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }

        res.status(200).send(db_res.rows[0]);
    });

    /**
     * Delete all car parks
     */
    router.delete('/', verify.UserAuth, async function (req, res) {
        try{
            await carpark_db.query(query.api.carparks.delete_all);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }

        res.status(200).json(json_resp.success.delete);
    });

    /**
     * Delete a specific car park
     */
    router.delete('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
        let t_id = req.path.replace(/\//g, '');
        const params = [t_id];

        try{
            await carpark_db.query(query.api.carparks.delete_one, params);
        }catch (db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }

        res.status(200).json(json_resp.success.delete);
    });

    /**
     * Create a car park
     */
    router.post('/', verify.UserAuth, async function (req, res) {
        let c_id = UUID();
        const params = [c_id, req.body.name, req.body.hour_rate, req.body.postcode, req.body.parking_places, req.body.happy_hour_start];

        try{
            await carpark_db.query(query.api.carparks.create, params);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }

        res.status(200).json(json_resp.success.create);
    });

    /**
     * Update a ticket, possible parameters are:
     * @param name              string
     * @param hour_rate         double
     * @param postcode          string
     * @param parking_places    int
     * @param happy_hour_start  date
     * @param happy_hour        boolean
     */
    router.put('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
        let c_id = req.path.replace(/\//g, '');

        if(typeof req.body.name             === 'undefined' &&
           typeof req.body.hour_rate        === 'undefined' &&
           typeof req.body.postcode         === 'undefined' &&
           typeof req.body.parking_places   === 'undefined' &&
           typeof req.body.happy_hour_start === 'undefined' &&
           typeof req.body.happy_hour       === 'undefined')
        {
            return res.status(500).json(json_resp.error.invalid_carpark_update);
        }

        if (typeof req.body.name !== 'undefined') {
            try {
                await carpark_db.query(query.api.carparks.update.name, [c_id, req.body.name]);
            }
            catch(db_err) {
                debug(db_err);
                return res.status(500).json(json_resp.error.internal);
            }
            socket_functions.emit_update(io, c_id);
        }

        if (typeof req.body.hour_rate !== 'undefined') {
            try {
                await carpark_db.query(query.api.carparks.update.hour_rate, [c_id, req.body.hour_rate]);
            }
            catch (db_err) {
                debug(db_err);
                return res.status(500).json(json_resp.error.internal);
            }

            socket_functions.emit_update(io, c_id);
        }

        if (typeof req.body.postcode !== 'undefined') {
            try {
                await carpark_db.query(query.api.carparks.update.postcode, [c_id, req.body.postcode]);
            }
            catch (db_err) {
                debug(db_err);
                return res.status(500).json(json_resp.error.internal);
            }
        }

        if (typeof req.body.parking_places !== 'undefined') {
            try{
            await carpark_db.query(query.api.carparks.update.parking_places, [c_id, req.body.parking_places]);
            }
            catch (db_err) {
                debug(db_err);
                return res.status(500).json(json_resp.error.internal);
            }
            socket_functions.emit_update(io, c_id);
        }

        if (typeof req.body.happy_hour_start !== 'undefined') {
            try{
                await carpark_db.query(query.api.carparks.update.happy_hour_start, [c_id, new Date(req.body.happy_hour_start).getTime()]);
            }
            catch (db_err) {
                debug(db_err);
                return res.status(500).json(json_resp.error.internal);
            }
            socket_functions.emit_update(io, c_id);
        }

        if (typeof req.body.happy_hour !== 'undefined') {
            try{
                await carpark_db.query(query.api.carparks.update.happy_hour, [c_id, req.body.happy_hour]);
            }
            catch (db_err) {
                debug(db_err);
                return res.status(500).json(json_resp.error.internal);
            }
            socket_functions.emit_update(io, c_id);
        }

        return res.status(200).json(json_resp.success.update);
    });

    return router;
};

