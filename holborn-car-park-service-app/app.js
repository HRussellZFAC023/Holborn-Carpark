const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const logger = require('morgan');
const db_connection = require('./server/javascripts/db_connection');
//main routes declaration
const indexRouter = require('./server/routes/index');


//API routes declaration
const carParksRoute = require('./server/routes/api/carparks');
const ticketsRoute = require('./server/routes/api/tickets');

var app = express();

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

//main routes
app.use('/', indexRouter);

//API routes
const api_name = '/api';
app.use(api_name + '/carparks', carParksRoute);
app.use(api_name + '/tickets', ticketsRoute);

module.exports = app;