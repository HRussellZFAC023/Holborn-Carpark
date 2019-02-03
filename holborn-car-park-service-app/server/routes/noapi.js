const express = require('express');
const router = express.Router();

router.get('/', function (req, res) {
    res.sendFile('index.html', {root: 'public/HTML/'});
});

router.get('/Login', function (req, res) {
    res.sendFile('Login.html', {root: 'public/HTML/'});
});

router.get('/Register', function (req, res) {
    res.sendFile('Register.html', {root: 'public/HTML/'});
});

router.get('/Admin', function (req, res) {
    res.sendFile('Admin.html', {root: 'public/HTML/'});
});

//catch 404s
router.get('*', function (req, res) {
    res.sendFile('404.html', {root: 'public/HTML/'});
});

module.exports = router;