const Router  = require('express-promise-router');
const router  = new Router();
const debug   = require('debug')('holborn-car-park-service-app: DB');
const UUID    = require('uuid/v4');
const crypto  = require('crypto');


const G        = require('../../javascripts/global');
const db       = require('../../databases/auth_db_conn');
const query    = require('../../databases/queries');
const verify   = require('../../javascripts/verify');
const util     = require('../../javascripts/utils');
const validate = require('../../javascripts/validate');


router.get('/', verify.UserAuth, async function (req, res) {
    let db_res;
    try {
        db_res = await db.query(query.api.users.get_all);
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).send('Error on the server:' + db_err);
    }

    return res.status(200).send(db_res.rows);
});

router.get('/' + G.uuid_regex, verify.UserAuth, async function (req, res) {
    let u_id = req.path.replace(/\//g, '');
    let db_res;
    try {
        db_res = await db.query(query.api.users.get_one, [u_id]);
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).send('Error on the server:' + db_err);
    }

    return res.status(200).send(db_res.rows[0]);
});

router.post('/', async function (req, res) {

    if(await validate.usernameR(res, req.body.username) !== true) return;
    if(validate.emailR(res, req.body.email) !== true) return;
    if(validate.passwordR(res, req.body.password, req.body.confirm_password) !== true) return;

    let u_id = UUID();
    let salt = util.genRandomString();
    let hash = crypto.pbkdf2Sync(G.default_pwd, salt, G.hash_iterations, 64, 'sha512');

    let params = [u_id, req.body.username, req.body.email, hash.toString('hex'), salt, req.body.level, req.body.carparks, true];

    try {
        await db.query(query.api.users.create, params);
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).send('Error on the server:' + db_err);
    }

    res.status(200).send('Success! User with id  ' + u_id + '  created');
});

module.exports = router;