const express       = require('express');
const socket_io     = require("socket.io");
const cl_sessions   = require('express-session');
const debug         = require('debug')('holborn-car-park-service-app: env');
const G             = require('./server/javascripts/global_variables');
const path          = require('path');
const pgSession     = require('connect-pg-simple')(cl_sessions);

const db            = require('./server/databases/auth_db_conn');


debug(G.env);

const app = express();

// Socket.io
const io = socket_io();
app.io = io;
require('./server/sockets/socket')(io);

//main routes declaration
const noApiRoutes = require('./server/routes/noapi');

//API routes declaration
const carParksRoute = require('./server/routes/api/carparks')(io);
const ticketsRoute = require('./server/routes/api/tickets')(io);

app.use(express.json());
app.use(express.urlencoded({extended: true}));

const cl_sessions_opt = {
    name: 'session',
    secret: G.cookie_secret,
    resave: false,
    rolling: true,
    saveUninitialized: false,
    cookie: {
        httpOnly: true,                 //this actually sets if cookie accessible through JS
        maxAge: 5 * 60 * 1000
    },
    store: new pgSession({
        pool : db,                      // Connection pool
        tableName : 'user_sessions'     // Use another table-name than the default "session" one
    }),
};

if(G.env === 'dev') cl_sessions_opt.cookie.secure = false;
else cl_sessions_opt.cookie.secure = true;

app.use(cl_sessions(cl_sessions_opt));

//main routes
app.use(express.static(path.join(__dirname, 'public', 'js')));
app.use(express.static(path.join(__dirname, 'public', 'resources')));
app.use(express.static(path.join(__dirname, 'public', 'stylesheets')));
app.use(noApiRoutes);

//API routes
const api_resource = '/api';
app.use(api_resource + '/carparks', carParksRoute);
app.use(api_resource + '/tickets', ticketsRoute);

//handle 404 res codes
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
