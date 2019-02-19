const pg    = require('pg');
const debug = require('debug')('holborn-car-park-service-app: carpark_db');

const G     = require('../javascripts/global');


const db_config = {
    user: 'postgres',
    database: 'holborn_car_parks',
    password: 'postgres',
    port: 5432
};

if (G.env === 'dev') db_config.host = '18.130.76.77';
else db_config.host = '127.0.0.1';

/**
 * Instantiate a new pool for connections to the database using the config object literal
 * @type {Pool}
 */
const pool = new pg.Pool(db_config);

/**
 * Test db connection
 */
pool.query('SELECT NOW()', function(err, res) {
    if (err) debug(err);
    else debug('Connected to the database successfully!');
});

module.exports = pool;