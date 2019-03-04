const Router = require('express-promise-router');
const router = new Router();
const debug = require('debug')('holborn-car-park-service-app: DB');

const updates_file = './server/updates/client/updates.txt';
const rc_1_0_1 ='./server/updates/client/Holborn Car Park Client Setup Update 1.0.1.exe';
const rc_1_0_0 ='./server/updates/client/Holborn Car Park Client Setup.exe';
/**
 * Get all users
 */
router.get('/client/updates', async function (req, res) {
    return res.download(updates_file)
});
router.get('/client/updates/rc_1.0.1', async function (req, res) {
     return res.download(rc_1_0_1)
});
router.get('/client/updates/rc_1.0.0', async function (req, res) {
    return res.download(rc_1_0_0)
});
router.get('/client/download', async function (req, res) {
    return res.download(rc_1_0_0)
});


module.exports = router;