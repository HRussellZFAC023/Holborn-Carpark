module.exports.username = async function (res, name) {
    if (!name) {
        return res.status(406).json({type: 'invalid name', message: 'Username is invalid.'});
    }
    else if (name.includesAnyOf(G.ch_special + G.ch_disallowed)) {
        return res.status(406).json({type: 'space in name', message: 'Username cannot contain ' + G.ch_special + G.ch_disallowed + '.'});
    }
    else if (req.body.username.length > 8) {
        return res.status(406).json({type: 'long name', message: 'Username is too long.'});
    }
    let db_res;
    try {
        db_res = await db.query(query.noapi.check_name, [name]);
    }
    catch(db_err){
        debug(db_err);
        return res.status(500).json({type: 'internal', message: 'Internal Error! Please try again later.'});
    }
    if (db_res.rowCount) {
        return res.status(418).json({type: 'taken name', message: 'Username already taken. Teapot!'});
    }

    return true;
};

module.exports.email = function (res, email) {
    if(!email || !validEmail(email)){
        return res.status(406).json({type: 'invalid email', message: 'Email is invalid.'});
    }

    return true;
};

module.exports.password = function (res, password, confirm_pwd) {
    if (!password) {
        return res.status(406).json({type: 'invalid pwd', message: 'Password is invalid.'});
    }
    else if (password.length < 8) {
        return res.status(406).json({type: 'short pwd', message: 'Password needs to be at least 8 characters.'});
    }
    else if (!password.includesOnly(G.ch_lower + G.ch_upper + G.ch_num + G.ch_special)) {
        return res.status(406).json({
            type: 'disallowed pwd',
            message: 'Password not allowed. Allowed symbols are alphanumeric and ' + G.ch_special
        });
    }
    else if (!pwdComplex(password)) {
        return res.status(406).json({
            type: 'weak pwd',
            message: 'Password too weak. Must include at least 1 number, 1 upper case and 1 special symbol.'
        });
    }

    if(password !== confirm_pwd){
        return res.status(406).json({type: 'match pwd', message: 'Passwords must match.'});
    }

    return true;
};

function pwdComplex (pwd) {
    return pwd.includesAnyOf(G.ch_num) && pwd.includesAnyOf(G.ch_upper) && pwd.includesAnyOf(G.ch_special);
}

function validEmail (em){
    return em.includes('@') && em.includes('.') && em.lastIndexOf('.') > em.lastIndexOf('@');
}