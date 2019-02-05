const express = require('express');
const router = express.Router();
const crypto = require('crypto');
const UUID = require('uuid/v4');
const query = require('../databases/queries');
const db = require('../databases/auth_db_conn');
const G = require('../javascripts/global_variables');
const debug = require('debug')('holborn-car-park-service-app: auth');




router.get('/', function (req, res) {
    res.sendFile('index.html', {root: 'public/HTML/'});
});

router.get('/login', function (req, res) {
    res.sendFile('Login.html', {root: 'public/HTML/'});
});

router.get('/register', function (req, res) {
    res.sendFile('Register.html', {root: 'public/HTML/'});
});

router.get('/manager', function (req, res) {
    res.sendFile('Manager.html', {root: 'public/HTML/'});
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

        crypto.pbkdf2(passw, db_res.rows[0].salt, G.hash_iterations, 64, 'sha512', function (err, hash) {
            if (err) debug(err);

            if(db_res.rows[0].pwd_hash.toString('hex') !== hash.toString('hex'))
                return res.status(403).send("Wrong password! Try again");

            res.status(200).send('Success! User with id ' + db_res.rows[0]._id + ' logged in');
            //res.redirect('127.0.0.1/manager');
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

const genRandomString = function(length = 16){
    return crypto.randomBytes(128).toString('hex').slice(0, length);
};

module.exports = router;