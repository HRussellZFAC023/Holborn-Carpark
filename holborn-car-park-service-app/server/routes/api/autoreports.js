const Router            = require('express-promise-router');
const router            = new Router();
const debug             = require('debug')('holborn-car-park-service-app: DB');
const UUID              = require('uuid/v4');

const G                 = require('../../javascripts/global');
const carpark_db        = require('../../databases/carpark_db_conn');
const query             = require('../../databases/queries');
const verify            = require('../../javascripts/verify');
const json_resp         = require('../../javascripts/json_response');

/**
 * Get all autoreports
 */
router.get('/', verify.UserAuth, async function (req, res) {
    let db_res;

    try{
        db_res = await carpark_db.query(query.api.autoreports.get_all);
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).json(json_resp.error.internal);
    }

    res.status(200).send(db_res.rows);
});

/**
 * Get a specific autoreport
 */
router.get('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
    let ar_id = req.path.replace(/\//g, '');
    const params = [ar_id];

    let db_res;
    try{
        db_res = await carpark_db.query(query.api.autoreports.get_one, params);
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).json(json_resp.error.internal);
    }

    res.status(200).send(db_res.rows[0]);
});

/**
 * Delete all autoreports
 */
router.delete('/', verify.UserAuth, async function (req, res) {
    try{
        await carpark_db.query(query.api.autoreports.delete_all);
    }
    catch(db_err) {
        debug(db_err);
        return res.status(500).json(json_resp.error.internal);
    }

    res.status(200).json(json_resp.success.delete);
});

/**
 * Delete a specific autoreport
 */
router.delete('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
    let ar_id = req.path.replace(/\//g, '');
    const params = [ar_id];

    try{
        await carpark_db.query(query.api.autoreports.delete_one, params);
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).json(json_resp.error.internal);
    }

    res.status(200).json(json_resp.success.delete);
});

/**
 * Create an autoreport
 */
router.post('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
    let ar_id = UUID();
    let c_id = req.path.replace(/\//g, '');

    const params = [ar_id, req.body.time_period, Date.now(), req.session._id, c_id];

    try{
        await carpark_db.query(query.api.autoreports.create, params);
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).json(json_resp.error.internal);
    }

    res.status(200).send(json_resp.success.create);
});

/**
 * Update a autoreport, possible parameters are:
 * @param time_period  integer
 * @param user_id      uuid
 */
router.put('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
    let ar_id = req.path.replace(/\//g, '');

    console.log(req.body)

    if(typeof req.body.time_period === 'undefined' &&
       typeof req.body.user_id     === 'undefined' &&
       typeof req.body.last_sent   === 'undefined' &&
       typeof req.body.carpark_id  === 'undefined')
    {
        return res.status(500).json(json_resp.error.invalid_autoreport_update);
    }

    if (typeof req.body.time_period !== 'undefined') {
        try{
            await carpark_db.query(query.api.autoreports.update.time_period, [ar_id, req.body.time_period]);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }
    }

    if (typeof req.body.user_id !== 'undefined') {
        try{
            await carpark_db.query(query.api.autoreports.update.user_id, [ar_id, req.body.user_id]);
        }
        catch(db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }
    }

    if (typeof req.body.last_sent !== 'undefined') {
        try{
            await carpark_db.query(query.api.autoreports.update.last_sent, [ar_id, req.body.last_sent]);
        }
        catch(db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }
    }

    if (typeof req.body.carpark_id !== 'undefined') {
        try{
            await carpark_db.query(query.api.autoreports.update.carpark_id, [ar_id, req.body.carpark_id]);
        }
        catch(db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }
    }

    return res.status(200).json(json_resp.success.update);
});

module.exports = router;

