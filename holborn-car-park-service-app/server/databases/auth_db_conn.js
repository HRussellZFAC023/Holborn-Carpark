const pg    = require('pg');
const debug = require('debug')('holborn-car-park-service-app: auth_db');

const G     = require('../javascripts/global_variables');


const db_config = {
    user: 'postgres',
    host: '18.218.5.6',
    database: 'Security',
    password: 'postgres',
    port: 5432
};

if (G.env === 'dev') db_config.host = '18.218.5.6';
else db_config.host = '127.0.0.1';

//instantiate a new pool for connections to the database using the config object literal
const pool = new pg.Pool(db_config);

//test connection
pool.query('SELECT NOW()', function(err, res) {
    if (err) debug(err);
    else debug('Connected to the database successfully!');
});

module.exports = pool;