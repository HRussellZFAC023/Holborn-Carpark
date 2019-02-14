const crypto = require('crypto');


module.exports.genRandomString = function (length = 16) {
    return crypto.randomBytes(128).toString('hex').slice(0, length);
};

String.prototype.includesAnyOf = function (symbols) {
    for (let i = 0; i < this.length; ++i) {
        if (symbols.includes(this[i])) return true;
    }

    return false;
};

String.prototype.includesOnly = function (symbols) {
    for (let i = 0; i < this.length; ++i) {
        if (!symbols.includes(this[i])) return false;
    }

    return true;
};

