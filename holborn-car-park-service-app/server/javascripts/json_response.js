const G = require('./global');

module.exports = {
    error: {
        internal: {
            type:    'internal',
            message: 'Internal Error! Please try again later.'
        },
        missing_user: {
            type:    'missing user',
            message: 'No such user.'
        },
        invalid_name: {
            type:    'invalid name',
            message: 'Username is invalid.'
        },
        disallowed_name: {
            type:    'disallowed name',
            message: 'Username cannot contain ' + G.ch_special + G.ch_disallowed + '.'
        },
        too_long_name: {
            type:    'long name',
            message: 'Username is too long.'
        },
        taken_name: {
            type:    'taken name',
            message: 'Username already taken. Teapot!'
        },
        invalid_email: {
            type:    'invalid email',
            message: 'Email is invalid.'
        },
        wrong_password: {
            type:    'wrong password',
            message: 'Wrong password.'
        },
        invalid_password: {
            type:    'invalid password',
            message: 'Password is invalid.'
        },
        too_short_password:{
            type:    'short password',
            message: 'Password needs to be at least 8 characters.'
        },
        disallowed_password: {
            type:    'disallowed password',
            message: 'Password not allowed. Allowed symbols are alphanumeric and ' + G.ch_special
        },
        weak_password: {
            type:    'weak password',
            message: 'Password too weak. Must include at least 1 number, 1 upper case and 1 special symbol.'
        },
        match_password: {
            type:    'match password',
            message: 'Passwords must match.'
        },
        invalid_level: {
            type:    'invalid level',
            message: 'Level does not exist. (0, 1, 2 are valid)'
        },
        missing_carpark: {
            type:    'missing carpark',
            message: 'Car park doesn\'t exist.'
        },
        invalid_active_status: {
            type:    'invalid active',
            message: 'Active status is invalid.'
        },
        invalid_user_update: {
            type:    'invalid update',
            message: 'Possible parameters listed in .params',
            params: {
                username:        'string',
                email:           'string',
                manager_level:   'int',
                _carpark_id:     'uuid[]',
                active:          'boolean',
                reset_password:  'boolean',
                change_password: 'string'
            }
        },
        invalid_ticket_update: {
            type:    'invalid update',
            message: 'Possible parameters listed in .params',
            params: {
                date_out:       'Date.now()',
                paid:           'boolean',
                valid:          'boolean',
                duration:       'double'
            }
        },
        invalid_carpark_update: {
            type:    'invalid update',
            message: 'Possible parameters listed in .params',
            params: {
                name:           'string',
                hour_rate:      'double',
                postcode:       'string ("HH000HH")'
            }
        },
        invalid_autoreport_update: {
            type:    'invalid update',
            message: 'Possible parameters listed in .params',
            params: {
                time_period:  'integer',
                last_sent:    'Date.now()',
                user_id:      'uuid',
                carpark_id:   'uuid'
            }
        }
    },
    success: {
        login: {
            type:     'success',
            message:  'Login successful.',
            redirect: '/manager'
        },
        register: {
            type:     'success',
            message:  'Register successful.',
            redirect: '/login'
        },
        create: {
            type:    'create',
            message: 'Create successful.'
        },
        delete: {
            type:    'delete',
            message: 'Delete successful.'
        },
        update: {
            type:    'update',
            message: 'Update successful.'
        }
    }
};