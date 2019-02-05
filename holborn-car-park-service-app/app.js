const express = require('express');
const socket_io = require("socket.io");
const cl_sessions = require('client-sessions');

const G = require('./server/javascripts/global_variables');

const app = express();
// Socket.io
const io = socket_io();
app.io = io;
require('./server/sockets/base')(io);

//main routes declaration
const noApiRoutes = require('./server/routes/noapi');

//API routes declaration
const carParksRoute = require('./server/routes/api/carparks')(io);
const ticketsRoute = require('./server/routes/api/tickets')(io);

app.use(express.json());
app.use(express.urlencoded({extended: true}));
app.use(cl_sessions({
    cookieName: 'session',
    secret: G.cookie_secret,
    duration: 30 * 60 * 1000,
    activeDuration: 5 * 60 * 1000,
    httpOnly: true,
    secure: false, //change when in production
    ephemeral: true
}));

//main routes
app.use(express.static(__dirname + '/public/stylesheets/')); //serve stylesheets first
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
            res.sendFile('404.html', {root: 'public/HTML/'});
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
