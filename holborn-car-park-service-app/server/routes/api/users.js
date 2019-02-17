const Router  = require('express-promise-router');
const router  = new Router();
const debug   = require('debug')('holborn-car-park-service-app: DB');
const UUID    = require('uuid/v4');
const crypto  = require('crypto');


const G        = require('../../javascripts/global');
const user_db  = require('../../databases/auth_db_conn');
const query    = require('../../databases/queries');
const verify   = require('../../javascripts/verify');
const util     = require('../../javascripts/utils');
const validate = require('../../javascripts/validate');


//Get all users
router.get('/', verify.UserAuth, async function (req, res) {
    let db_res;
    try {
        db_res = await user_db.query(query.api.users.get_all);
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).send('Error on the server:' + db_err);
    }

    return res.status(200).send(db_res.rows);
});

//Get a specific user
router.get('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
    let u_id = req.path.replace(/\//g, '');
    let db_res;
    try {
        db_res = await user_db.query(query.api.users.get_one, [u_id]);
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).send('Error on the server:' + db_err);
    }

    return res.status(200).send(db_res.rows[0]);
});

//Create a user, that conforms to the enforced validations
router.post('/', verify.UserAuth, async function (req, res) {
    if(await validate.usernameR(res, req.body.username) !== true) return;
    if(validate.emailR(res, req.body.email) !== true) return;
    if(validate.managerLevelR(res, req.body.manager_level) !== true) return;

    let u_id = UUID();
    let salt = util.genRandomString();
    let hash = crypto.pbkdf2Sync(G.default_pwd, salt, G.hash_iterations, 64, 'sha512');

    let params = [u_id, req.body.username, req.body.email, hash.toString('hex'), salt, req.body.manager_level, req.body._carpark_id, true];

    try {
        await user_db.query(query.api.users.create, params);
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).send('Error on the server:' + db_err);
    }

    res.status(200).send('Success! User with id  ' + u_id + '  created');
});

//Update user
router.put('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
    let u_id = req.path.replace(/\//g, '');

    if (typeof req.body.username !== 'undefined') {
        if(await validate.usernameR(res, req.body.username) !== true) return;
        try{
            await user_db.query(query.api.users.update.username, [u_id, req.body.username]);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        return res.status(200).send('Updated! User with id  ' + u_id + '  updated');
    }

    if (typeof req.body.email !== 'undefined') {
        if(validate.emailR(res, req.body.email) !== true) return;
        try{
            await user_db.query(query.api.users.update.email, [u_id, req.body.email]);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        return res.status(200).send('Updated! User with id  ' + u_id + '  updated');
    }

    if (typeof req.body.reset_password !== 'undefined') {
        let salt = util.genRandomString();
        let hash = crypto.pbkdf2Sync(G.default_pwd, salt, G.hash_iterations, 64, 'sha512');

        try{
            await user_db.query(query.api.users.update.password, [u_id, hash.toString('hex')]);
            await user_db.query(query.api.users.update.salt,     [u_id, salt]);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        return res.status(200).send('Updated! User with id  ' + u_id + '  updated');
    }

    if (typeof req.body.manager_level !== 'undefined') {
        if(validate.managerLevelR(res, req.body.manager_level) !== true) return;

        try{
            await user_db.query(query.api.users.update.manager_level, [u_id, req.body.manager_level]);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        return res.status(200).send('Updated! User with id  ' + u_id + '  updated');
    }

    if (typeof req.body._carpark_id !== 'undefined') {
        if(await validate.carparkID(res, req.body._carpark_id) !== true) return;

        try{
            await user_db.query(query.api.users.update.carparks, [u_id, req.body._carpark_id]);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        return res.status(200).send('Updated! User with id  ' + u_id + '  updated');
    }

    if (typeof req.body.active !== 'undefined') {
        if(await validate.activeStatus(res, req.body.active) !== true) return;

        try{
            await user_db.query(query.api.users.update.active, [u_id, req.body.active === 'true']);
        }
        catch (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        return res.status(200).send('Updated! User with id  ' + u_id + '  updated');
    }

    return res.status(500).send('Possible body params are: \nusername, \nemail, \nmanager_level, \n_carpark_id[i], \nactive, \nreset_password');
});

//Delete
router.delete('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
    let u_id = req.path.replace(/\//g, '');

    try{
        await user_db.query(query.api.users.delete_one, [u_id]);
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).send('Error on the server:' + db_err);
    }

    res.status(200).json({type: 'delete', message: 'User with id ' + u_id + ' deleted.'})
});

module.exports = router;