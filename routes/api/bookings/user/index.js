const { Router } = require("express");
const router = Router();

const { userBookingController } = require("../../../../controllers");

const { body, oneOf } = require("express-validator");
const { validateRequest, requireAuthentication } = require("@utopia-airlines-wss/common/middleware");


router.route("/")
  .all(requireAuthentication())
  .get(userBookingController.getAll)
  .post(
    requireAuthentication({
      roles: ["ADMIN", "AGENT"],
      condition: (req) => typeof req.body?.userId === "number", 
    }),
    [
      body("flights")
        .exists().withMessage("please provide flights")
        .bail()
        .isArray().withMessage("flights must be an array")
        .isLength({ min:1 }).withMessage("please provide a flight"),
      body("passengers")
        .exists().withMessage("please provide passengers")
        .bail()
        .isArray().withMessage("passengers must be an array")
        .isLength({ min:1 }).withMessage("please provide a passenger"),
      oneOf([
        body("userId").not().exists(),
        body("userId")
          .exists()
          .isInt().withMessage("please provide a valid user id"),
      ]),
    ],
    validateRequest,
    userBookingController.create
  );

router.route("/:id")
  .get(requireAuthentication())
  .get(userBookingController.getById);

module.exports = router;