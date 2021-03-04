const { sendJson } = require("@utopia-airlines-wss/common/util");
const { guestBookingService } = require("../services");



const guestBookingController = {
  async getAll(req, res, next) {
    try {
      const bookings = await guestBookingService.findAllGuestBookings({
        ...req.query,
      });
      sendJson({
        req,
        res,
        data: bookings,
      });
    } catch (err) {
      next(err);
    }
  },
  async getById(req, res, next) {
    try {
      const { params:{ id } } = req;
      const booking = await guestBookingService.findGuestBookingById({
        id,
      });
      sendJson({
        req,
        res,
        data: booking,
      });
    } catch (err) {
      next(err);
    }
  },
  async create(req, res, next) {
    try {
      const { user, body } = req;
      const data = ["ADMIN", "AGENT"].includes(user?.role.name)
        ? { ...body, agentId: user.id }
        : { ...body, agentId: null };
      const booking = await guestBookingService.createGuestBooking(data);
      sendJson({
        req,
        res,
        data: booking,
        status: 201,
      });
    } catch (err) {
      next(err);
    }
  },
  async updateById(req, res, next) {
    try {
      const booking = await guestBookingService.updateBooking(req.params.id, req.body);
      sendJson({
        req,
        res,
        data: booking,
      });
    } catch (err) {
      next(err);
    }
  },
};


module.exports = { guestBookingController };