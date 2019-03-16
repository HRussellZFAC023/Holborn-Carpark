const Router = require('express-promise-router');
const router = new Router();
const debug = require('debug')('holborn-car-park-service-app: DB');

const client_updates_file = './server/updates/client/updates.txt';
const barriers_updates_file = './server/updates/barriers/updates.txt';

const cl_rc_1_0_2 ='./server/updates/client/Holborn Car Park Client Setup Update 1.0.2.exe';
const cl_rc_1_0_1 ='./server/updates/client/Holborn Car Park Client Setup Update 1.0.1.exe';
const cl_rc_1_0_0 ='./server/updates/client/Holborn Car Park Client Setup.exe';

const br_rc_1_0_0 ='./server/updates/barriers/Holborn Car Park Barriers Setup.exe';

/**
 * Client update file
 */
router.get('/client/updates', async function (req, res) {
    return res.download(client_updates_file)
});

/**
 * Barriers update file
 */
router.get('/client/barriers', async function (req, res) {
    return res.download(barriers_updates_file)
});

/**
 * Client updates
 */
router.get('/client/updates/rc_1.0.2', async function (req, res) {
    return res.download(cl_rc_1_0_2)
});
router.get('/client/updates/rc_1.0.1', async function (req, res) {
    return res.download(cl_rc_1_0_1)
});
router.get('/client/updates/rc_1.0.0', async function (req, res) {
    return res.download(cl_rc_1_0_0)
});
router.get('/client/download', async function (req, res) {
    return res.download(cl_rc_1_0_0)
});

/**
 * Barrier Updates
 */
router.get('/barriers/updates/rc_1.0.0', async function (req, res) {
    return res.download(br_rc_1_0_0)
});
router.get('/barriers/download', async function (req, res) {
    return res.download(br_rc_1_0_0)
});


module.exports = router;