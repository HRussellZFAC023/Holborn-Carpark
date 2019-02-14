exports.sockets = {
    ticket_details: `SELECT     tickets._id, date_in, paid, valid, duration, date_out, hour_rate 
                     FROM       tickets 
                     INNER JOIN carparks 
                     ON         tickets._carpark_id = carparks._id WHERE tickets._id = $1 AND tickets._carpark_id = $2`,
    ticket_details_update:  `UPDATE tickets SET paid = $1, duration = $2, date_out = $3   WHERE tickets._id = $4 AND _carpark_id = $5`,
    ticket_valid_count:     `SELECT   COUNT (*) AS count FROM   tickets          WHERE valid = true AND _carpark_id = $1`,
    carpark_details:        `SELECT * FROM                      carparks         WHERE carparks._id = $1`,


};

exports.api = {
    tickets: {
        get_all:    `SELECT * FROM tickets`,
        delete_all: `DELETE   FROM tickets`,
        get_one:    `SELECT * FROM tickets WHERE  _id = $1`,
        create:     `INSERT   INTO tickets VALUES ($1, to_timestamp($2 / 1000.0), null, $3, $4, $5)`,
        update: {
            date_out:   `UPDATE tickets SET date_out  = to_timestamp($2 / 1000.0)   WHERE _id = $1`,
            paid:       `UPDATE tickets SET paid      = $2                          WHERE _id = $1`,
            valid:      `UPDATE tickets SET valid     = $2                          WHERE _id = $1`
        },
        delete_one: `DELETE   FROM tickets WHERE _id = $1`,
        validate:   `SELECT * FROM tickets WHERE _id = $1`
    },
    carparks: {
        get_all:    `SELECT * FROM carparks`,
        delete_all: `DELETE   FROM carparks`,
        get_one:    `SELECT * FROM carparks WHERE  _id = $1`,
        create:     `INSERT   INTO carparks VALUES ($1, $2, $3, $4, $5)`,
        update: {
            name:             `UPDATE carparks SET name           = $2 WHERE _id = $1`,
            hour_rate:        `UPDATE carparks SET hour_rate      = $2 WHERE _id = $1`,
            postcode:         `UPDATE carparks SET postcode       = $2 WHERE _id = $1`,
            parking_places:   `UPDATE carparks SET parking_places = $2 WHERE _id = $1`,
            duration:         `UPDATE carparks SET duration       = $2 WHERE _id = $1`
        },
        delete_one: `DELETE   FROM carparks WHERE _id = $1`
    },
    users:{
        get_all:    `SELECT * FROM users`,
        get_one:    `SELECT * FROM users WHERE  _id = $1`,
        create:     `INSERT   INTO users VALUES ($1, $2, $3, $4, $5, $6, $7, $8 )`,
        update: {
            username:   `UPDATE users SET name           = $2 WHERE _id = $1`,
            email:      `UPDATE users SET hour_rate      = $2 WHERE _id = $1`,
            password:   `UPDATE users SET postcode       = $2 WHERE _id = $1`,
            carparks:   `UPDATE users SET parking_places = $2 WHERE _id = $1`,
            active:     `UPDATE users SET duration       = $2 WHERE _id = $1`
        },
        delete_one: `DELETE   FROM users WHERE _id = $1`
    }
};

exports.noapi = {
    register:    `INSERT   INTO users VALUES ($1, $2, $3, $4, $5, 0, null, false)`,
    login:       `SELECT * FROM users WHERE username = $1`,
    check_name:  `SELECT * FROM users WHERE username = $1`
};
