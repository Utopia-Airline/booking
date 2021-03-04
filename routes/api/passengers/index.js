const { Router } = require("express");
const router = Router();

const { passengerController } = require("../../../controllers");

const { requireAuthentication } = require("@utopia-airlines-wss/common/middleware");

router.use(requireAuthentication({
  roles: ["ADMIN"],
}));

router.get("/",passengerController.getAll);

router.route("/:id")
  .get(passengerController.getById)
  .put(passengerController.updateById);

module.exports = router;