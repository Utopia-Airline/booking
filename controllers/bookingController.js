const { sendJson } = require("@utopia-airlines-wss/common/util");
const { bookingService } = require("../services");

const bookingController = {
  async getAll(req, res, next) {
    try {
      const bookings = await bookingService.findAllBookings({
        ...req.query,
      });
      sendJson({
        req,
        res,
        data: bookings,
        toJSON: {
          type: "full",
        },
      });
    } catch (err) {
      next(err);
    }
  },
  async getById(req, res, next) {
    try {
      const { params:{ id } } = req;
      const booking = await bookingService.findBookingById({
        id,
      });
      sendJson({
        req,
        res,
        data: booking,
        toJSON: {
          type: "full",
        },
      });
    } catch (err) {
      next(err);
    }
  },
  async deleteById(req, res, next) {
    try {
      await bookingService.deleteBookingById(req.params.id);
      res.sendStatus(204);
    } catch (err) {
      next(err);
    }
  },
};


module.exports = { bookingController };