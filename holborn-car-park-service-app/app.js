const express = require('express');
const cookieParser = require('cookie-parser');

//Database
require('./server/javascripts/db_connection');
const db = require('./server/javascripts/pg_conn');

//main routes declaration
const noApiRoutes = require('./server/routes/noapi');

//API routes declaration
const carParksRoute = require('./server/routes/api/carparks');
const ticketsRoute = require('./server/routes/api/tickets');

const app = express();

app.use(express.json());
app.use(express.urlencoded({extended: false}));
app.use(cookieParser());

//main routes
app.use(express.static(__dirname + '/public/stylesheets/')); //serve stylesheets first
app.use(noApiRoutes);

//API routes
const api_name = '/api';
app.use(api_name + '/carparks', carParksRoute);
app.use(api_name + '/tickets', ticketsRoute);

module.exports = app;
