var express = require('express');
var router = express.Router();
var User = require('../public/data_model/User');
var jwt = require('jsonwebtoken');
var global_var = require('../public/javascripts/gloval_variables');
var VerifyToken = require('../public/javascripts/VerifyToken');
var VerifyAdmin = require('../public/javascripts/VerifyAdmin');
/* GET home page. */
router.get('/me', VerifyToken, function(req, res) {
    User.findById(req.userID, { password: 0 }, function (err, user) {
        if (err) return res.status(500).send("There was a problem finding the user.");
        if (!user) return res.status(404).send("No user found.");

        res.status(200).send(user);
    });
});
router.get('/meadmin', VerifyAdmin, function(req, res) {
        res.status(200).send("You have access");
});


router.post('/login', function(req, res) {
    User.findOne({username: req.body.username}, function (err, user) {
       if (err) return res.status(500).send('Error on the server.');
       if (!user) return res.status(404).send('No user found.');
            user.comparePassword(req.body.password, function (err, isMatch) {
                if (err) throw err;
                if (!isMatch) return res.status(401).send({auth: false, token: null});
                var token = jwt.sign({id: user._id}, global_var.secret_key, {
                    expiresIn: "24h"
                });
                res.status(200).send({auth: true, token: token})
            });

   });
});
router.post('/register', function(req, res, next) {
     User.create({
        username: req.body.username,
        password: req.body.password
    },(function(err, user){
        if(err) {
            return res.status(500).send("There was a problem registering the user.");
        }
        // var token = jwt.sign({ id: user._id }, global_var.secret_key, {
        //     expiresIn: '24h' // expires in 24 hours
        // });
        // res.status(200).send({ auth: true, token: token });
         res.status(200).send("User registered successfully");
    }));

});
module.exports = router;
