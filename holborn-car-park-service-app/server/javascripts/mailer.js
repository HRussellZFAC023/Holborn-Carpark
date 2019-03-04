const mailer            = require("nodemailer");
const fs                = require('fs');
const scheduler         = require('node-schedule');
const debug             = require('debug')('holborn-car-park-service-app: DB');
const debug_m             = require('debug')('holborn-car-park-service-app: mailer');


const carpark_db        = require('../databases/carpark_db_conn');
const user_db           = require('../databases/auth_db_conn');
const query             = require('../databases/queries');

/**
 * Set Transport protocol to SMTP
 */
smtpTransport = mailer.createTransport({
    service: "Gmail",
    auth: {
        user: "holbornreporting@gmail.com",
        pass: "&EaY89gWD"
    }
});

/**
 * Create a default empty email object literal that will be used to reset other objects
 */
let mail_empty = {
    from: 'Holborn Auto Reports <holbornreporting@gmail.com>',
    to: '',
    subject: 'Holborn Car Park Report',
    html: '',
    props: {
        car_park_name: '',
        time_period: '',
        number_of_cars: '',
        revenue: ''
    }
};

const day = 24 * 60 * 60 * 1000;

/**
 * This code needs to be wrapped ina function as async/await is not allowed in global scope
 * @returns {Promise<void>}
 */
async function initReporterDaemon(sendnow = false) {
    let all_reports;

    try {
        all_reports = await carpark_db.query(query.api.autoreports.get_all);
    } catch (db_err) {
        debug(db_err);
    }


    for (let i = 0; i < all_reports.rowCount; ++i) {
        let mail = mail_empty;

        let last_sent = new Date(all_reports.rows[i].last_sent);
        last_sent.setHours(0);
        last_sent.setMinutes(0);
        last_sent.setSeconds(0);
        last_sent.setMilliseconds(0);
        let today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        today.setMilliseconds(0);

        if(last_sent.getTime() + all_reports.rows[i].time_period * day !== today.getTime() && !sendnow) continue;

        let username;
        try {
            username = await user_db.query(query.api.users.get_one, [all_reports.rows[i].user_id]);
        } catch (db_err) {
            debug(db_err);
        }

        mail.to = username.rows[0].email;

        switch (all_reports.rows[i].time_period) {
            case 1:
                mail.props.time_period = 'daily';
                break;
            case 7:
                mail.props.time_period = 'weekly';
                break;
            case 30:
                mail.props.time_period = 'monthly';
                break;
            case 365:
                mail.props.time_period = 'yearly';
                break;
        }

        let carpark;
        try {
            carpark = await carpark_db.query(query.api.carparks.get_one, [all_reports.rows[i].carpark_id]);
        } catch (db_err) {
            debug(db_err);
        }

        mail.props.car_park_name = carpark.rows[0].name;


        let tickets;
        try {
            tickets = await carpark_db.query(query.api.tickets.get_all_specific, [all_reports.rows[i].carpark_id, all_reports.rows[i].last_sent.getTime(), Date.now()]);
        } catch (db_err) {
            debug(db_err);
        }

        if(tickets.rows[0]) {
            mail.props.number_of_cars = tickets.rowCount;

            let drevenue = 0;
            for (let i = 0; i < tickets.rowCount; ++i) {
                if (tickets.rows[i].amount_paid) {
                    drevenue += tickets.rows[i].amount_paid;
                }
            }

            mail.props.revenue = drevenue.toFixed(2);
        }
        else{
            mail.props.number_of_cars = 0;
            mail.props.revenue = 0;
        }

        fs.readFile(__dirname + "../../../public/protected/resources/emailTemplate.html", "utf8", (err, data) => {
            data = data.replace('[CAR_PARK_NAME]', mail.props.car_park_name);
            data = data.replace('[TIME_PERIOD]', mail.props.time_period);
            data = data.replace('[NUMBER_OF_CARS]', mail.props.number_of_cars);
            data = data.replace('[REVENUE]', mail.props.revenue);


            mail.html = data;

            smtpTransport.sendMail(mail, async function(error, response){
                if(error){
                    debug_m(error);
                }

                smtpTransport.close();

                try {
                    await carpark_db.query(query.api.autoreports.update.last_sent, [all_reports.rows[i]._id, Date.now()]);
                } catch (db_err) {
                    debug(db_err);
                }
            });
        });

    }
}

/**
 * Set a scheduler to check if there should be emails sent out every day at 9:00 AM
 */
const rule = new scheduler.RecurrenceRule();
rule.hour = 9;
rule.minute = 0;

scheduler.scheduleJob(rule, initReporterDaemon);

/**
 * Testing route to immediately send emails
 * @type {module:express-promise-router}
*/
const Router = require('express-promise-router');
const router = new Router();
const verify = require('../javascripts/verify');

router.get('/autoreporter', verify.UserAuth, async function (req, res) {
    await initReporterDaemon(true);

    res.send("")
});

module.exports = router;