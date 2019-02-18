const crypto = require('crypto');


/**
 * Function that returns a cryptographically proven
 * random string with default length of 16 bytes
 * @param length
 * @returns {string}
 */
module.exports.genRandomString = function (length = 16) {
    return crypto.randomBytes(128).toString('hex').slice(0, length);
};

/**
 * Function augmenting the String prototype to provide more
 * checking functionality
 * @param symbols
 * @returns {boolean}
 */
String.prototype.includesAnyOf = function (symbols) {
    for (let i = 0; i < this.length; ++i) {
        if (symbols.includes(this[i])) return true;
    }

    return false;
};

/**
 * Function augmenting the String prototype to provide more
 * checking functionality
 * @param symbols
 * @returns {boolean}
 */
String.prototype.includesOnly = function (symbols) {
    for (let i = 0; i < this.length; ++i) {
        if (!symbols.includes(this[i])) return false;
    }

    return true;
};

