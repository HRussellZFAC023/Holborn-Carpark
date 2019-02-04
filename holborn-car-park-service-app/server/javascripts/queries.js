exports.sockets = {
   ticket_details: `SELECT     tickets._id, date_in, paid, valid, duration, date_out, hour_rate 
                    FROM       tickets 
                    INNER JOIN carparks 
                    ON         _carpark_id = carparks._id WHERE tickets._id = $1`
};

exports.api = {
   tickets: {
      get_all:    `SELECT * FROM tickets`,
      delete_all: `DELETE FROM tickets`,
      get_one:    `SELECT * FROM tickets WHERE _id = $1`,
      create:     `INSERT INTO tickets VALUES ($1, to_timestamp($2 / 1000.0), null, $3, $4, $5)`,
      update: {
         date_out:   `UPDATE tickets SET date_out  = to_timestamp($2 / 1000.0)   WHERE _id = $1`,
         paid:       `UPDATE tickets SET paid      = $2                          WHERE _id = $1`,
         valid:      `UPDATE tickets SET valid     = $2                          WHERE _id = $1`
      },
      delete_one: `DELETE FROM tickets WHERE _id = $1`,
      validate:   `SELECT * FROM tickets WHERE _id = $1`
   },
   carparks: {
      get_all:    `SELECT * FROM carparks`,
      delete_all: `DELETE FROM carparks`,
      get_one:    `SELECT * FROM carparks WHERE _id = $1`,
      create:     `INSERT INTO carparks VALUES ($1, $2, $3, $4, $5)`,
      update: {
         name:             `UPDATE carparks SET name           = $2 WHERE _id = $1`,
         hour_rate:        `UPDATE carparks SET hour_rate      = $2 WHERE _id = $1`,
         postcode:         `UPDATE carparks SET postcode       = $2 WHERE _id = $1`,
         parking_places:   `UPDATE carparks SET parking_places = $2 WHERE _id = $1`,
         duration:         `UPDATE carparks SET duration       = $2 WHERE _id = $1`
      },
      delete_one: `DELETE FROM carparks WHERE _id = $1`,
   }
};
