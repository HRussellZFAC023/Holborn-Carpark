exports.sockets = {
    ticket_details: `SELECT     tickets._id, date_in, paid, valid, duration, date_out, hour_rate, valid 
                     FROM       tickets 
                     INNER JOIN carparks 
                     ON         tickets._carpark_id = carparks._id WHERE tickets._id = $1 AND tickets._carpark_id = $2`,
    ticket_details_update:  `UPDATE     tickets SET paid = $1, duration = $2, date_out = $3, amount_paid = $6   
                             WHERE     tickets._id = $4 AND _carpark_id = $5`,
    ticket_valid_count:     `SELECT   COUNT (*) AS count 
                             FROM     tickets          
                             WHERE valid = true AND _carpark_id = $1`,
    carpark_details:        `SELECT * 
                             FROM     carparks
                             WHERE     carparks._id = $1`,
    smartcard_details: `SELECT *
                        FROM       smartcards 
                        INNER JOIN carparks 
                        ON         smartcards._carpark_id = carparks._id WHERE smartcards._id = $1 AND smartcards._carpark_id = $2`,
};

exports.api = {
    smartcards: {
        get_all:    `SELECT * FROM smartcards`,
        delete_all: `DELETE   FROM smartcards`,
        get_one:    `SELECT * FROM smartcards WHERE  _id = $1`,
        create:     `INSERT   INTO smartcards VALUES ($1, $2, $3, $4, $5)`,
        update: {
            paid:       `UPDATE smartcards SET paid      = $2                          WHERE _id = $1`,
            valid:      `UPDATE smartcards SET valid     = $2                          WHERE _id = $1`,
            ticket_id: `UPDATE smartcards SET ticket_id     = $2                          WHERE _id = $1`
        },
        delete_one: `DELETE   FROM tickets WHERE _id = $1`,
        validate:   `SELECT * FROM tickets WHERE _id = $1`
    },
    tickets: {
        get_all:          `SELECT * FROM tickets`,
        get_all_specific: `SELECT * FROM tickets WHERE  _carpark_id = $1 AND date_in > to_timestamp($2 / 1000.0) AND date_in < to_timestamp($3 / 1000.0) ORDER BY date_in`,
        delete_all:       `DELETE   FROM tickets`,
        get_one:          `SELECT * FROM tickets WHERE  _id = $1`,
        create:           `INSERT   INTO tickets VALUES ($1, to_timestamp($2 / 1000.0), null, $3, $4, $5)`,
        update: {
            date_in:  `UPDATE tickets SET date_in  = to_timestamp($2 / 1000.0)   WHERE _id = $1`,
            date_out:   `UPDATE tickets SET date_out  = to_timestamp($2 / 1000.0)   WHERE _id = $1`,
            paid:       `UPDATE tickets SET paid      = $2                          WHERE _id = $1`,
            valid:      `UPDATE tickets SET valid     = $2                          WHERE _id = $1`
        },
        delete_one: `DELETE   FROM tickets WHERE _id = $1`,
        validate:   `SELECT * FROM tickets WHERE _id = $1 AND _carpark_id = $2`
    },
    carparks: {
        get_all:    `SELECT * FROM carparks`,
        delete_all: `DELETE   FROM carparks`,
        get_one:    `SELECT * FROM carparks WHERE  _id = $1`,
        create:     `INSERT   INTO carparks VALUES ($1, $2, $3, $4, $5, to_timestamp($6 / 1000.0), false)`,
        update: {
            name:             `UPDATE carparks SET name             = $2 WHERE _id = $1`,
            hour_rate:        `UPDATE carparks SET hour_rate        = $2 WHERE _id = $1`,
            postcode:         `UPDATE carparks SET postcode         = $2 WHERE _id = $1`,
            parking_places:   `UPDATE carparks SET parking_places   = $2 WHERE _id = $1`,
            duration:         `UPDATE carparks SET duration         = $2 WHERE _id = $1`,
            happy_hour:       `UPDATE carparks SER happy_hour       = $2 WHERE _id = $1`,
            happy_hour_start: `UPDATE carparks SER happy_hour_start = $2 WHERE _id = $1`
        },
        delete_one: `DELETE   FROM carparks WHERE _id = $1`
    },
    users:{
        get_all:    `SELECT * FROM users`,
        get_one:    `SELECT * FROM users WHERE  _id = $1`,
        create:     `INSERT   INTO users VALUES ($1, $2, $3, $4, $5, $6, $7, $8 )`,
        update: {
            username:       `UPDATE users SET username      = $2 WHERE _id = $1`,
            email:          `UPDATE users SET email         = $2 WHERE _id = $1`,
            password:       `UPDATE users SET pwd_hash      = $2 WHERE _id = $1`,
            carparks:       `UPDATE users SET _carpark_id   = $2 WHERE _id = $1`,
            active:         `UPDATE users SET active        = $2 WHERE _id = $1`,
            manager_level:  `UPDATE users SET manager_level = $2 WHERE _id = $1`,
            salt:           `UPDATE users SET salt          = $2 WHERE _id = $1`
        },
        delete_one: `DELETE   FROM users WHERE _id = $1`
    }
};

exports.noapi = {
    register:    `INSERT   INTO users VALUES ($1, $2, $3, $4, $5, 0, null, false)`,
    login:       `SELECT * FROM users WHERE username = $1`,
    check_name:  `SELECT * FROM users WHERE username = $1`
};
