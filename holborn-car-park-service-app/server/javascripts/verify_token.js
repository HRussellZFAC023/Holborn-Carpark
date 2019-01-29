var jwt = require('jsonwebtoken');
var global_var = require('./gloval_variables');

function verify_token(req, res, next) {
    var token = req.headers["x-access-token"];
    if (!token) return res.status(401).send({auth: false, message: 'No token provided.'});

    jwt.verify(token, global_var.secret_key, function (err, decoded) {
        if (err) return res.status(500).send({auth: false, message: 'Failed to authenticate token.'});
        req.userID = decoded.id;
        next();
    });
}
module.exports = verify_token;