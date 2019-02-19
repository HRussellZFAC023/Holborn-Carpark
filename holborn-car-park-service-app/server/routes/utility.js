const Router  = require('express-promise-router');
const router  = new Router();

const verify    = require('../javascripts/verify');


/**
* Get logged in user's name
*/
router.get('/name', verify.UserAuth, async function (req, res) {
    return res.status(200).send(req.session.username);
});

module.exports = router;