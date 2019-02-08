const express   = require('express');
const router    = express.Router();
const crypto    = require('crypto');
const UUID      = require('uuid/v4');
const query     = require('../databases/queries');
const db        = require('../databases/auth_db_conn');
const G         = require('../javascripts/global_variables');
const debug     = require('debug')('holborn-car-park-service-app: auth');
const path      = require('path');


router.get('/', function (req, res) {
    res.sendFile('index.html', {root: path.join('public', 'protected', 'HTML')});
});

router.get('/login', function (req, res) {
    res.sendFile('login.html', {root: path.join('public', 'protected', 'HTML')});
});

router.get('/register', function (req, res) {
    res.sendFile('register.html', {root: path.join('public', 'protected', 'HTML')});
});

router.get('/manager', function (req, res) {
    if(req.session && req.session.user) res.sendFile('manager.html', {root: path.join('public', 'protected', 'HTML')});
    else res.redirect('/login');
});

router.post('/login', function (req, res) {
    let uname = req.body.username;
    let passw = req.body.password;

    const params = [uname];

    db.query(query.noapi.login, params, function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        if(!db_res.rowCount) return res.status(403).send('No such user');

        crypto.pbkdf2(passw, db_res.rows[0].salt, G.hash_iterations, 64, 'sha512', function (err, hash) {
            if (err) debug(err);

            if(db_res.rows[0].pwd_hash.toString('hex') !== hash.toString('hex'))
                return res.status(403).send("Wrong password! Try again");

            req.session.user = uname;
            res.redirect('/manager');
        });
    });
});

router.post('/register', function (req, res) {
    let u_id = UUID();
    let uname = req.body.username.toString();
    let email = req.body.email;
    let passw = req.body.password.toString();

    db.query(query.noapi.check_name, [uname], function (db_err, db_res) {
        if (db_err) {
            debug(db_err);
            return res.status(500).send('Error on the server:' + db_err);
        }

        if(db_res.rowCount !== 0) return res.status(418).send("Name taken! Also I'm a teapot");

        let salt = genRandomString();
        crypto.pbkdf2(passw, salt, G.hash_iterations, 64, 'sha512',  function(err, hash) {
            if (err) debug(err);

            const params = [u_id, uname, email, hash.toString('hex'), salt];

            db.query(query.noapi.register, params, function (db_err, db_res) {
                if (db_err) {
                    debug(db_err);
                    return res.status(500).send('Error on the server:' + db_err);
                }

                res.status(200).send('Success! User with id ' + u_id + ' created');
            });
        });
    });
});

router.post('/register', function (req, res) {
    req.session.destroy();
    res.redirect('/login');
});

const genRandomString = function(length = 16){
    return crypto.randomBytes(128).toString('hex').slice(0, length);
};

module.exports = router;
