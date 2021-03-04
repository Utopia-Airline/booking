const { Router } = require("express");
const router = Router();

const { guestBookingController } = require("../../../../controllers");
const { body } = require("express-validator");
const { validateRequest, requireAuthentication } = require("@utopia-airlines-wss/common/middleware");


router.route("/")
  .get(
    requireAuthentication({ roles: ["ADMIN", "AGENT"], }),
    guestBookingController.getAll
  )
  .post(
    [
      body("flights")
        .exists().withMessage("please provide flights")
        .bail()
        .isArray().withMessage("flights must be an array")
        .isLength({ min: 1 }).withMessage("please provide a flight"),
      body("passengers")
        .exists().withMessage("please provide passengers")
        .bail()
        .isArray().withMessage("passengers must be an array")
        .isLength({ min: 1 }).withMessage("please provide a passenger"),
      body("contact.email")
        .exists().withMessage("please provide an email")
        .bail()
        .isEmail().withMessage("please provide a valid email"),
      body("contact.phone")
        .exists().withMessage("please provide a phone")
        .bail()
        .isMobilePhone().withMessage("please provide a valid phone number"),
    ],
    validateRequest,
    guestBookingController.create
  );

router.route("/:id")
  .get(guestBookingController.getById)
  .put(
    [
      body("contact.email")
        .isEmail().withMessage("please provide a valid email"),
      body("contact.phone")
        .isMobilePhone().withMessage("please provide a valid phone number"),
    ],
    validateRequest,
    guestBookingController.updateById
  );

module.exports = router;
