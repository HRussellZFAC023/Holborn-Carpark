const moment = require('moment');
const debug = require('debug')('holborn-car-park-service-app: socket_functions');

const UUID = require('uuid/v4');
const carpark_db = require('../databases/carpark_db_conn');
const verify = require('../javascripts/verify');
const queries = require('../databases/queries');


exports.carpark_details_modified = async function (_id, callback) {
    let db_res;
    try {
        db_res = await carpark_db.query(queries.sockets.ticket_valid_count, [_id]);
    } catch (db_err) {
        debug(db_err);
    }
    let count = db_res.rows[0].count;

    try {
        db_res = await carpark_db.query(queries.sockets.carpark_details, [_id]);
    } catch (db_err) {
        debug(db_err);
    }

    let avail_places = db_res.rows[0].parking_places - count;
    if (avail_places < 0) avail_places = 0;

    return callback(avail_places, db_res.rows[0].hour_rate, db_res.rows[0].happy_hour);
};
exports.fetch_smartcard_details = async function (_id, carpark_id, callback) {
    const params = [_id, carpark_id];
    let db_res;
    try {
        db_res = await carpark_db.query(queries.sockets.smartcard_details, params);
    } catch (db_err) {
        return callback(500, db_err.message, null);
    }
    if (db_res.rows[0]) {
        await this.fetch_ticket_details(db_res.rows[0].ticket_id, carpark_id, function (code, details, ticket) {
                if (code === 200) {
                    if (db_res.rows[0].discount_start !== null && db_res.rows[0].discount_end !== null) {
                        let currentHour = new Date(Date.now()).getHours();
                        debug(currentHour);
                        if (db_res.rows[0].discount_start <= currentHour && db_res.rows[0].discount_end >= currentHour) {
                            ticket.discount = db_res.rows[0].discount;

                        }else{
                            ticket.discount = 0;
                        }
                    } else {
                        ticket.discount = db_res.rows[0].discount;
                    }
                    return callback(code, details, ticket)

                }
            }
        );
    } else {
        return callback(404, "Smart card could not be found: " + _id, null)
    }
}
;
exports.fetch_ticket_details = async function (_id, carpark_id, callback) {
    const params = [_id, carpark_id];
    let db_res;
    try {
        db_res = await carpark_db.query(queries.sockets.ticket_details, params);
    } catch (db_err) {
        return callback(500, db_err.message, null);
    }

    if (db_res.rowCount === 0) {
        return callback(404, "Ticket not found: " + _id, null);
    }
    const params1 = [carpark_id];
    let db_res1;
    try {
        db_res1 = await carpark_db.query(queries.api.carparks.get_one, params1);
    } catch (db_err) {
        return callback(500, db_err.message, null);
    }
    if (db_res1.rowCount === 0) {
        return callback(404, "Carpark does not exist: " + carpark_id, null);
    }

    const ticket = {
        _id: db_res.rows[0]._id,
        date_in: db_res.rows[0].date_in,
        date_out: db_res.rows[0].date_out,
        price: db_res.rows[0].price,
        duration: db_res.rows[0].duration,
        duration_paying_for: 0,
        paid: db_res.rows[0].paid,
        valid: db_res.rows[0].valid
    };

    const now = moment();
    const duration = moment.duration(now.diff(ticket.date_in));
    const minutes = duration.asMinutes();
    const date = new Date();

    ticket.duration = minutes.toFixed(0);
    ticket.date_out = date.toISOString();
    ticket.duration_paying_for = Math.ceil(duration.asHours()).toFixed(0);
    if(db_res1.rows[0].happy_hour!== null){
        let ticketEnterDate = new Date(ticket.date_in);
        let happyHourStartTime = new Date(db_res1.rows[0].happy_hour_start_time);
        // debug(ticketEnterDate);
        // debug(happyHourStartTime);
        debug(ticketEnterDate >= happyHourStartTime)
        if(db_res1.rows[0].happy_hour=== true && ticketEnterDate >= happyHourStartTime) ticket.price = 0;
        else{
            ticket.price = (db_res.rows[0].hour_rate * Math.ceil(duration.asHours())).toFixed(2);
        }
    }
    // console.log("minutes:" + ticket.duration);
    // console.log("rounded hours to half:" + Math.ceil(duration.asHours()));
    // console.log("rounded hours to half:" + Math.ceil(duration.asHours())*60);
    // console.log("hours:" + duration.asHours());
    return callback(200, "Ticket is valid: " + _id, ticket);
};

exports.ticket_paid = async function (paid, duration, date_out, _id, amount_paid, carpark_id) {
    const params = [paid, duration, date_out, _id, carpark_id, amount_paid];
    try {
        await carpark_db.query(queries.sockets.ticket_details_update, params);
    } catch (db_err) {
        debug(db_err);
    }
};

exports.authorise = function (socket, carparkid_cb) {
    socket.on('authorisation', function (_idCarPark, carpark_name, callback) {
        verify.ClientAuth(_idCarPark, carpark_name, function (code, desc) {
            callback(code, desc);
            carparkid_cb(code, _idCarPark);
        });
    });
};
exports.smartcard_exit = async function (io, s_id, c_id, cb) {
    const params = [s_id];
    let db_res;
    try {
        db_res = await carpark_db.query(queries.api.smartcards.get_one, params);
    } catch (db_err) {
        debug(db_err);
        return cb(500, `Error on server when checking for smartcard`);
    }
    if (db_res.rows[0]) {
        if (db_res.rows[0]._carpark_id !== c_id) return cb(406, `Carpark id on smart card doesn't correspond with the carpark`);
        this.ticket_exit(io, db_res.rows[0].ticket_id, c_id, function (code, msg) {
            return cb(code, msg);
        });
    } else {
        return cb(404, `Could not find`);
    }
};

/**
 * This is shit code...
 * @param io
 * @param s_id
 * @param c_id
 * @param cb
 * @returns {Promise<*>}
 */
exports.smartcard_enter = async function (io, s_id, c_id, cb) {
    /**
     * Firstly we check if there's a smart card for the provided UI
     * @type {*[]}
     */
    const params = [s_id];
    let db_res;
    try {
        db_res = await carpark_db.query(queries.api.smartcards.get_one, params);
    } catch (db_err) {
        debug(db_err);
        return cb(500, `Error on server when checking for smartcard`);
    }
    // debug(db_res);
    if (db_res.rows[0]) {
        /**
         * If doesnt the smart card doesnt corresponds to this car park error
         */
        if (db_res.rows[0]._carpark_id !== c_id) cb(406, `Carpark id on smart card doesn't correspond with the carpark`);
        /**
         * if no ticket is attached, we attach one
         */
        if (db_res.rows[0].ticket_id === null) {
            this.request_ticket(io, c_id, function (code, ticket) {
                if (code == 200) {
                    const params = [s_id, ticket._id];
                    try {
                        carpark_db.query(queries.api.smartcards.update.ticket_id, params);
                    } catch (db_err) {
                        debug(db_err);
                        return cb(500, `Error on server when attaching ticket`);
                    }
                    return cb(200, `New ticket attached`);
                } else {
                    return cb(code, ticket)
                }
            });

        } else {
            /**
             * If there is a ticket, we get it
             * @type {*[]}
             */
            const params = [db_res.rows[0].ticket_id];
            let local_db_res;
            try {
                local_db_res = await carpark_db.query(queries.api.tickets.get_one, params);
            } catch (db_err) {
                debug(db_err);
                return cb(500, `Error on getting the ticket`);
            }
            if (!local_db_res.rows[0]) {
                /**
                 * If ticket could not be found, we attach a new one
                 */
                this.request_ticket(io, c_id, function (code, ticket) {
                    if (code == 200) {
                        const params = [s_id, ticket._id];
                        try {
                            carpark_db.query(queries.api.smartcards.update.ticket_id, params);
                        } catch (db_err) {
                            debug(db_err);
                            return cb(500, `Error on server when attaching ticket`);
                        }
                        return cb(200, `New ticket attached`);
                    } else {
                        return cb(code, ticket)
                    }
                });

            }
            /**
             * If the ticket is invalid and paid, we generate one and attach it
             */
            if (local_db_res.rows[0].valid !== true && local_db_res.rows[0].paid === true) {
                this.request_ticket(io, c_id, function (code, ticket) {
                    if (code == 200) {
                        const params = [s_id, ticket._id];
                        try {
                            carpark_db.query(queries.api.smartcards.update.ticket_id, params);
                        } catch (db_err) {
                            debug(db_err);
                            cb(500, 'Error on replacing ticket');
                        }
                        return cb(200, "Ticket invalid and paid. Attached a new ticket!");
                    } else {
                        return cb(code, ticket)
                    }
                });
                /**
                 * Else we update it with a new date in
                 */
            } else if (local_db_res.rows[0].valid === true && local_db_res.rows[0].paid === false) {
                const params = [local_db_res.rows[0]._id, Date.now()];
                try {
                    carpark_db.query(queries.api.tickets.update.date_in, params);
                } catch (db_err) {
                    debug(db_err);
                    return cb(500, `Error on server when updating date for the ticket`);
                }
                return cb(200, 'Updated ticket with new date');
            } else {
                return cb(406, `Ticket cannot be valid and paid at the same time when entering the car park. You little snek u cheatin ?`);
            }

        }
    } else {
        return cb(404, `Smart card could not be found`);
    }
};
exports.ticket_exit = async function (io, t_id, c_id, cb) {
    const p1 = [t_id, c_id];
    let db_res1;
    try {
        db_res1 = await carpark_db.query(queries.sockets.ticket_details, p1);
    } catch (db_err) {
        return cb(500, 'Error on the server')
    }
    if (!db_res1.rows[0]) return cb(404, null);
    if (db_res1.rows[0].valid !== true) return cb(406, 'Not valid'); //not valid
    if (db_res1.rows[0].paid !== true) return cb(403, 'Not paid'); // not paid

    const params = [t_id, c_id];
    let db_res;
    try {
        db_res = await carpark_db.query(queries.api.tickets.validate, params);
    } catch (db_err) {
        debug(db_err);
        return cb(500, "Error on validating");
    }
    cb(200, db_res.rows[0]);

};
exports.request_ticket = async function (io, c_id, cb) {
    //generate a ticket
    let t_id = UUID();
    const params = [t_id, Date.now(), false, true, c_id];
    try {
        await carpark_db.query(queries.api.tickets.create, params);
    } catch (db_err) {
        debug(db_err);
        cb(500, "Error");
    }
    this.emit_update(io, c_id);

    //and get the newly generated ticket
    const params1 = [t_id];
    let db_res;
    try {
        db_res = await carpark_db.query(queries.api.tickets.get_one, params1);
    } catch (db_err) {
        debug(db_err);
    }
    cb(200, db_res.rows[0]);


};
exports.emit_update = function (io, _id) {
    io.sockets.in(_id).emit('update-carpark-details');
};

exports.emit_update = function (io) {
    io.sockets.emit('update-carpark-details');
};