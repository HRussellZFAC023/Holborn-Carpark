const Router    = require('express-promise-router');
const router    = new Router();
const crypto    = require('crypto');
const UUID      = require('uuid/v4');
const debug     = require('debug')('holborn-car-park-service-app: auth');
const path      = require('path');

const query     = require('../databases/queries');
const db        = require('../databases/auth_db_conn');
const G         = require('../javascripts/global_variables');
const verify    = require('../javascripts/verify');


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

router.post('/login', async function (req, res) {
    let db_res;
    try {
        db_res = await db.query(query.noapi.login, [req.body.username]);
    }
    catch (db_err) {
        debug(db_err);
        return res.status(500).json({type: 'internal', message: 'Internal Error! Please try again later.'});
    }

    if (!db_res.rowCount)
        return res.status(403).json({type: 'user', message: 'No such user.'});

    let hash = crypto.pbkdf2Sync(req.body.password, db_res.rows[0].salt, G.hash_iterations, 64, 'sha512');
    if (db_res.rows[0].pwd_hash !== hash.toString('hex'))
        return res.status(403).json({type: 'pwd', message: 'Wrong password.'});

    req.session.username    = db_res.rows[0].username;
    req.session.level       = db_res.rows[0].level;
    req.session.active      = db_res.rows[0].level;
    await req.session.save();
    return res.status(200).json({type: 'success', message: 'Login successful.', redirect: '/manager'});
});

router.post('/register', function (req, res) {
    let u_id    = UUID();
    let uname   = req.body.username;
    let email   = req.body.email;
    let pwd   = req.body.password;
    let c_passw = req.body.confirm_password;

    if (!uname) {
        return res.status(406).json({type: 'invalid name', message: 'Username is invalid.'});
    } else if (uname.includes(' ')) {
        return res.status(406).json({type: 'space in name', message: 'Username cannot contain a space.'});
    } else if (uname.length > 8) {
        return res.status(406).json({type: 'long name', message: 'Username is too long.'});
    }

    if(!email || !validateEmail(email)){
        return res.status(406).json({type: 'invalid email', message: 'Email is invalid.'});
    }

    if (!pwd) {
        return res.status(406).json({type: 'invalid pwd', message: 'Password is invalid.'});
    } else if (pwd.length < 8) {
        return res.status(406).json({type: 'short pwd', message: 'Password needs to be at least 8 characters.'});
    } else if (!validatePwdAllowed(pwd)) {
        return res.status(406).json({
            type: 'disallowed pwd',
            message: 'Password not allowed. Allowed symbols are alphanumeric and ' + G.ch_special
        });
    } else if (!validatePwdComplex(pwd)) {
        return res.status(406).json({
            type: 'weak pwd',
            message: 'Password too weak. Must include at least 1 number, 1 upper case and 1 special symbol.'
        });
    }

    if(pwd !== c_passw){
        return res.status(406).json({type: 'match pwd', message: 'Passwords must match.'});
    }

    db.query(query.noapi.check_name, [uname], function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).json({type: 'internal', message: 'Internal Error! Please try again later.'});
        }

        if (db_res.rowCount !== 0)
            return res.status(418).json({type: 'taken name', message: 'Username already taken.'});

        let salt = genRandomString();
        crypto.pbkdf2(pwd, salt, G.hash_iterations, 64, 'sha512', function (err, hash) {
            if (err) debug(err);

            const params = [u_id, uname, email, hash.toString('hex'), salt];

            db.query(query.noapi.register, params, function (db_err, db_res) {
                if (db_err) {
                    debug(db_err);
                    return res.status(500).json({type: 'internal', message: 'Internal Error! Please try again later.'});
                }

                res.status(200).json({type: 'success', message: 'Register successful.', redirect: '/manager'});
            });
        });
    });
});

const genRandomString = function (length = 16) {
    return crypto.randomBytes(128).toString('hex').slice(0, length);
};

const validatePwdAllowed = function (pwd) {
    let allowed = G.ch_lower + G.ch_upper + G.ch_num + G.ch_special;

    for (let i = 0; i < pwd.length; ++i) {
        if (!allowed.includes(pwd[i])) return false;
    }

    return true;
};

const validatePwdComplex = function (pwd) {
    let n_flag = false;
    let u_flag = false;
    let s_flag = false;

    for (let i = 0; i < G.ch_num.length; ++i) {
        if (pwd.includes(G.ch_num[i])) {
            n_flag = true;
            break;
        }
    }
    for (let i = 0; i < G.ch_upper.length; ++i) {
        if (pwd.includes(G.ch_upper[i])) {
            u_flag = true;
            break;
        }
    }
    for (let i = 0; i < G.ch_special.length; ++i) {
        if (pwd.includes(G.ch_special[i])) {
            s_flag = true;
            break;
        }
    }

    return n_flag && u_flag && s_flag;
};

const validateEmail = function(email){
    return email.includes('@') && email.includes('.') && email.lastIndexOf('.') > email.lastIndexOf('@');
};

module.exports = router;
