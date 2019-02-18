const debug   = require('debug')('holborn-car-park-service-app: DB');

const G          = require('../javascripts/global');
const carpark_db = require('../databases/carpark_db_conn');
const user_db    = require('../databases/auth_db_conn');
const query      = require('../databases/queries');
const json_resp  = require('../javascripts/json_response');


/**
 * Validate registering username for:
 * undefined/null/empty sting
 * containment of special/disallowed characters
 * length (max is 8)
 * already in use
 * @param res
 * @param name
 * @returns {Promise<*>}
 */
module.exports.usernameR = async function (res, name) {
    if (!name) {
        return res.status(406).json(json_resp.error.invalid_name);
    }
    else if (name.includesAnyOf(G.ch_special + G.ch_disallowed)) {
        return res.status(406).json(json_resp.error.disallowed_name);
    }
    else if (name.length > 8) {
        return res.status(406).json(json_resp.error.too_long_name);
    }
    let db_res;
    try {
        db_res = await user_db.query(query.noapi.check_name, [name]);
    }
    catch(db_err){
        debug(db_err);
        return res.status(500).json(json_resp.error.internal);
    }
    if (db_res.rowCount) {
        return res.status(418).json(json_resp.error.taken_name);
    }

    return true;
};

/**
 * Validate registering email
 * @param res
 * @param email
 * @returns {*}
 */
module.exports.emailR = function (res, email) {
    if(!email || !validEmail(email)){
        return res.status(406).json(json_resp.error.invalid_email);
    }

    return true;
};

/**
 * Validate registering password for:
 * undefined/null/empty sting
 * length (min is 8)
 * disallowed (can't contain disallowed symbols)
 * strength
 * @param res
 * @param password
 * @param confirm_pwd
 * @returns {*}
 */
module.exports.passwordR = function (res, password, confirm_pwd) {
    if (!password) {
        return res.status(406).json(json_resp.error.invalid_password);
    }
    else if (password.length < 8) {
        return res.status(406).json(json_resp.error.too_short_password);
    }
    else if (!password.includesOnly(G.ch_lower + G.ch_upper + G.ch_num + G.ch_special)) {
        return res.status(406).json(json_resp.error.disallowed_password);
    }
    else if (!pwdComplex(password)) {
        return res.status(406).json(json_resp.error.weak_password);
    }

    if(password !== confirm_pwd){
        return res.status(406).json(json_resp.error.match_password);
    }

    return true;
};

/**
 * Validate registering manager level
 * @param res
 * @param level
 * @returns {*}
 */
module.exports.managerLevelR = function (res, level){
    if(level !== '0' && level !== '1' && level !== '2'){
        return res.status(406).json(json_resp.error.invalid_level);
    }

    return true;
};

/**
 * Check password complexity
 * @param pwd
 * @returns {boolean}
 */
function pwdComplex (pwd) {
    return pwd.includesAnyOf(G.ch_num) && pwd.includesAnyOf(G.ch_upper) && pwd.includesAnyOf(G.ch_special);
}

/**
 * Most basic validation for emails
 * @param em
 * @returns {*|boolean}
 */
function validEmail (em){
    return em.includes('@') && em.includes('.') && em.lastIndexOf('.') > em.lastIndexOf('@');
}

/**
 * Check if a car park id is valid, exists, etc
 * @param res
 * @param ids
 * @returns {Promise<*>}
 */
module.exports.carparkID = async function (res, ids) {
    let db_res;
    for (let i = 0; i < ids.length; ++i) {
        try {
            db_res = await carpark_db.query(query.api.carparks.get_one, [ids[i]]);
        } catch (db_err) {
            debug(db_err);
            return res.status(500).json(json_resp.error.internal);
        }

        if (!db_res.rowCount) return res.status(404).json(json_resp.error.missing_carpark);
    }

    return true;
};

/**
 * validate active status, should be a boolean
 * @param res
 * @param status
 * @returns {Promise<*>}
 */
module.exports.activeStatus = async function (res, status) {
    if(status !== 'true' && status !== 'false'){
        return res.status(406).json(json_resp.error.invalid_active_status);
    }

    return true;
};