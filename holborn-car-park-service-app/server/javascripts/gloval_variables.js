// config.js
const dotenv = require('dotenv');
dotenv.config({path: './server/global_var.env'});
module.exports = {
  sslCert: process.env.SSL_CERT,
  sslKey: process.env.SSL_KEY,
  databaseUri: process.env.DB_CONNECTION,
  secret_key : process.env.SECRET
};