const VerifyToken = require('./verify_token');
const global_var = require('./gloval_variables');
const User = require('../data model/User');

function verify_admin(req, res, next) {
    VerifyToken(req, res, function(){
        User.findOne({_id: req.userID}, function (err, user) {
            if(err) return res.status(404).send('Unable to find the user');
            if(user) {
                if(user.admin !== true) return res.status(403).send('You don not have permission to access this!');
                next();
            }

        });
    });
}
module.exports = verify_admin;