const express       = require('express');
const socket_io     = require("socket.io");
const cl_sessions   = require('express-session');
const debug         = require('debug')('holborn-car-park-service-app: env');
const path          = require('path');
const pgSession     = require('connect-pg-simple')(cl_sessions);

const user_db       = require('./server/databases/auth_db_conn');
const G             = require('./server/javascripts/global');


debug(G.env);

const app = express();

/**
 * Socket.io
 */
const io = socket_io();
app.io = io;
require('./server/sockets/socket')(io);

/**
 * Main routes declaration
 */
const mainRoutes    = require('./server/routes/main');
const utilityRoutes = require('./server/routes/utility');

/**
 * API routes declaration
 */
const carParksRoute    = require('./server/routes/api/carparks')(io);
const ticketsRoute     = require('./server/routes/api/tickets')(io);
const smartcardsRoute  = require('./server/routes/api/smartcards')(io);
const usersRoute       = require('./server/routes/api/users');
const autoreportsRoute = require('./server/routes/api/autoreports');

/**
 * Setting express middleware to be used
 */
app.use(express.json());
app.use(express.urlencoded({extended: true}));

const cl_sessions_opt = {
    name:               'session',
    secret:             G.cookie_secret,
    resave:             false,
    rolling:            true,
    saveUninitialized:  false,
    cookie: {
        httpOnly:   true, //this actually sets if cookie is accessible with JS
        maxAge:     555 * 60 * 1000
    },
    store: new pgSession({
        pool :      user_db, // Connection pool
        tableName : 'user_sessions'
    }),
};

if(G.env === 'dev') cl_sessions_opt.cookie.secure = false;
else cl_sessions_opt.cookie.secure = true;

app.use(cl_sessions(cl_sessions_opt));

/**
 *  Setting main routes to be used as statically served folders
 *  and serving the main routes
 */
app.use(express.static(path.join(__dirname, 'public', 'js')));
app.use(express.static(path.join(__dirname, 'public', 'resources')));
app.use(express.static(path.join(__dirname, 'public', 'stylesheets')));
app.use(mainRoutes);
app.use('/utility', utilityRoutes);

/**
 * Setting API routes to be used as express middleware
 */
const api_resource = '/api';
app.use(api_resource + '/carparks', carParksRoute);
app.use(api_resource + '/tickets', ticketsRoute);
app.use(api_resource + '/smartcards', smartcardsRoute);
app.use(api_resource + '/users', usersRoute);
app.use(api_resource + '/autoreports', autoreportsRoute);


/**
 * Express middleware to handle 404 pages
 */
app.use(function (req, res) {
    res.status(404);

    res.format({
        'text/plain': function () {
            res.send('404 nothing to show here.');
        },

        'text/html': function () {
            res.sendFile('404.html', {root: path.join('public', 'protected', 'HTML')});
        },

        'application/json': function () {
            res.send({message: '404 nothing to show here.'});
        },

        'default': function () {
            res.status(406).send('Not Acceptable');
        }
    });
});

module.exports = app;
