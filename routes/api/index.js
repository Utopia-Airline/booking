const { Router } = require("express");
const router = Router();

router.use("/bookings", require("./bookings"));
router.use("/passengers", require("./passengers"));

module.exports = router;