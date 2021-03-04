const { Router } = require("express");
const router = Router();

const { bookingController } = require("../../../controllers");

const { requireAuthentication } = require("@utopia-airlines-wss/common/middleware");

router.use("/guest", require("./guest"));
router.use("/user", require("./user"));


router.get("/",
  requireAuthentication({ roles: ["ADMIN", "AGENT"], }),
  bookingController.getAll
);

router.route("/:id")
  .all(requireAuthentication())
  .get(bookingController.getById)
  .delete(bookingController.deleteById);

module.exports = router;
