const Router = require('express-promise-router');
const router = new Router();
const debug = require('debug')('holborn-car-park-service-app: DB');

/**
 * Get all users
 */
router.get('/client/updates', async function (req, res) {
    let n = req.path.lastIndexOf('\\');
    debug(req.path.substring(n+1));
     return res.download('./server/updates/client/updates.txt')
});
router.get('/client/exe', async function (req, res) {
    let n = req.path.lastIndexOf('\\');
    debug(req.path.substring(n+1));
    return res.download('./server/updates/client/Holborn Car Park Client.exe')
});


module.exports = router;