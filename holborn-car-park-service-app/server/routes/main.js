const Router    = require('express-promise-router');
const router    = new Router();
const crypto    = require('crypto');
const UUID      = require('uuid/v4');
const debug     = require('debug')('holborn-car-park-service-app: auth');
const path      = require('path');

const query     = require('../databases/queries');
const user_db   = require('../databases/auth_db_conn');
const G         = require('../javascripts/global');
const verify    = require('../javascripts/verify');
const util      = require('../javascripts/utils');
const validate  = require('../javascripts/validate');
const json_resp = require('../javascripts/json_response');


/**
 * Defines what happens when a browser navigates to those routes
 */
router.get('/', function (req, res) {
    res.sendFile('index.html', {root: path.join('public', 'protected', 'HTML')});
});

router.get('/login', function (req, res) {
    if (req.session && req.session.username) return res.redirect('/manager');
    res.sendFile('login.html', {root: path.join('public', 'protected', 'HTML')});
});

router.get('/register', function (req, res) {
    res.sendFile('register.html', {root: path.join('public', 'protected', 'HTML')});
});

router.get('/manager', verify.UserAuth, function (req, res) {
    res.sendFile('manager.html', {root: path.join('public', 'protected', 'HTML')});
});


router.get('/logout', function (req, res) {
    req.session.destroy(function () {
        res.redirect('/login');
    });
});

/**
 * Functions that defines what happens when a post request is sent by the browser
 * usually that is a button click requesting validation of details
 */
router.post('/login', async function loginUser(req, res) {
    let db_res;
    try {
        db_res = await user_db.query(query.noapi.login, [req.body.username.trim()]);
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).json(json_resp.error.internal);
    }

    if (!db_res.rowCount)
        return res.status(403).json(json_resp.error.missing_user);

    let hash = crypto.pbkdf2Sync(req.body.password, db_res.rows[0].salt, G.hash_iterations, 64, 'sha512');
    if (db_res.rows[0].pwd_hash !== hash.toString('hex'))
        return res.status(403).json(json_resp.error.wrong_password);

    req.session.username    = db_res.rows[0].username;
    req.session.level       = db_res.rows[0].manager_level;
    req.session.active      = db_res.rows[0].active;
    await req.session.save();
    return res.status(200).json(json_resp.success.login);
});

router.post('/register', async function registerUser(req, res) {
    if(await validate.usernameR(res, req.body.username) !== true) return;
    if(validate.emailR(res, req.body.email) !== true) return;
    if(validate.passwordR(res, req.body.password, req.body.confirm_password) !== true) return;

    let salt = util.genRandomString();
    let hash = crypto.pbkdf2Sync(req.body.password, salt, G.hash_iterations, 64, 'sha512');

    try{
        await user_db.query(query.noapi.register, [UUID(), req.body.username, req.body.email, hash.toString('hex'), salt])
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).json(json_resp.error.internal);
    }

    return res.status(200).json(json_resp.success.register);
});

module.exports = router;
