module.exports = {
    env:                'dev',
    hash_iterations:    1000,
    cookie_secret:      'CA181ADEB865FE34F29B65EC84133BF2E61640C54537EA281C679064FDACBC75',
    ch_num:             '0123456789',
    ch_lower:           'abcdefghijklmnopqrstuvwxyz',
    ch_upper:           'ABCDEFGHIJKLMNOPQRSTUVWXYZ',
    ch_special:         '~!@#$%^&*()_+[]{}|<>?,.`',
    ch_disallowed:      '\\|/\' ',
    uuid_regex:         '[0-9A-Za-z]{8}-[0-9A-Za-z]{4}-4[0-9A-Za-z]{3}-[89ABab][0-9A-Za-z]{3}-[0-9A-Za-z]{12}',
    default_pwd:        'holborn'
};