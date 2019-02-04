const pg = require('pg');
const debug = require('debug')('holborn-car-park-service-app: DB');


const db_config = {
    user: 'postgres',
    host: '18.218.5.6', //change to localhost once in production
    database: 'holborn_car_parks',
    password: 'postgres',
    port: 5432
};

//instantiate a new pool for connections to the database using the config object literal
const pool = new pg.Pool(db_config);

//test connection
pool.query('SELECT NOW()', (err, res) => {
    if (err) debug(err);
    else debug('Connected to the database successfully!');
});


module.exports = pool;