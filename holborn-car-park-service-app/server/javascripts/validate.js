const debug   = require('debug')('holborn-car-park-service-app: DB');

const G          = require('../javascripts/global');
const carpark_db = require('../databases/carpark_db_conn');
const user_db    = require('../databases/auth_db_conn');
const query      = require('../databases/queries');


module.exports.usernameR = async function (res, name) {
    if (!name) {
        return res.status(406).json({type: 'invalid name', message: 'Username is invalid.'});
    }
    else if (name.includesAnyOf(G.ch_special + G.ch_disallowed)) {
        return res.status(406).json({type: 'space in name', message: 'Username cannot contain ' + G.ch_special + G.ch_disallowed + '.'});
    }
    else if (name.length > 8) {
        return res.status(406).json({type: 'long name', message: 'Username is too long.'});
    }
    let db_res;
    try {
        db_res = await user_db.query(query.noapi.check_name, [name]);
    }
    catch(db_err){
        debug(db_err);
        return res.status(500).json({type: 'internal', message: 'Internal Error! Please try again later.'});
    }
    if (db_res.rowCount) {
        return res.status(418).json({type: 'taken name', message: 'Username already taken. Teapot!'});
    }

    return true;
};

module.exports.emailR = function (res, email) {
    if(!email || !validEmail(email)){
        return res.status(406).json({type: 'invalid email', message: 'Email is invalid.'});
    }

    return true;
};

module.exports.passwordR = function (res, password, confirm_pwd) {
    if (!password) {
        return res.status(406).json({type: 'invalid pwd', message: 'Password is invalid.'});
    }
    else if (password.length < 8) {
        return res.status(406).json({type: 'short pwd', message: 'Password needs to be at least 8 characters.'});
    }
    else if (!password.includesOnly(G.ch_lower + G.ch_upper + G.ch_num + G.ch_special)) {
        return res.status(406).json({
            type: 'disallowed pwd',
            message: 'Password not allowed. Allowed symbols are alphanumeric and ' + G.ch_special
        });
    }
    else if (!pwdComplex(password)) {
        return res.status(406).json({
            type: 'weak pwd',
            message: 'Password too weak. Must include at least 1 number, 1 upper case and 1 special symbol.'
        });
    }

    if(password !== confirm_pwd){
        return res.status(406).json({type: 'match pwd', message: 'Passwords must match.'});
    }

    return true;
};

module.exports.managerLevelR = function (res, level){
    if(level !== '0' && level !== '1' && level !== '2'){
        return res.status(406).json({type: 'invalid level', message: 'Level ' + level + ' does not exist. (0, 1, 2 are valid)'});
    }

    return true;
};

function pwdComplex (pwd) {
    return pwd.includesAnyOf(G.ch_num) && pwd.includesAnyOf(G.ch_upper) && pwd.includesAnyOf(G.ch_special);
}

function validEmail (em){
    return em.includes('@') && em.includes('.') && em.lastIndexOf('.') > em.lastIndexOf('@');
}

module.exports.carparkID = async function (res, ids) {
    let db_res;
    for (let i = 0; i < ids.length; ++i) {
        try {
            db_res = await carpark_db.query(query.api.carparks.get_one, [ids[i]]);
        } catch (db_err) {
            debug(db_err);
            return res.status(500).json({type: 'internal', message: 'Internal Error! Please try again later.'});
        }

        if (!db_res.rowCount) return res.status(404).json({type: 'no carpark', message: 'Car park ' + ids[i] + ' doesnt exist.'});
    }

    return true;
};

module.exports.activeStatus = async function (res, status) {
    if(status !== 'true' && status !== 'false'){
        return res.status(406).json({type: 'invalid active status', message: 'Active status is invalid.'});
    }

    return true;
};