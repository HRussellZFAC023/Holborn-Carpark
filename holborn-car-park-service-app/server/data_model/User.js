const mongoose = require('mongoose');
const Schema = mongoose.Schema;
const bcrypt = require('bcrypt');
const saltRounds = 10;

const userSchema = new Schema({
    username: {type: String, required: true, index: {unique: true}},
    password: {type: String, required: true},
    admin: {
        type: Boolean,
        default: false
    }
});

userSchema.pre('save', function (next) {
    const user = this;
    // only hash the password if it has been modified (or is new)
    if (!user.isModified("password")) return next();
        bcrypt.hash(user.password, saltRounds, function(err, hash) {
            if(err)throw err;
            user.password = hash;
            next();
        });


});
userSchema.methods.comparePassword = function (candidatePassword, cb) {
    bcrypt.compare(candidatePassword, this.password, function (err, isMatch) {
        if (err) return cb(err);
        cb(null, isMatch);
    });
};
const User = mongoose.model('User', userSchema);
module.exports = User;