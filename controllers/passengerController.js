const { passengerService } = require("../services");

const passengerController = {
  async getAll(req, res, next) {
    try {
      const passengers = await passengerService.findAllPassengers();
      res.json(passengers);
    } catch (err) {
      next(err);
    }
  },
  async getById(req, res, next) {
    try {
      const { params:{ id } } = req;
      const passenger = await passengerService.findPassengerById({ id });
      res.json(passenger);
    } catch (err) {
      next(err);
    }
  },
  async updateById(req, res, next) {
    try {
      const { params:{ id }, body } = req;
      const passenger = await passengerService.updatePassenger(id, body);
      res.json(passenger);
    } catch (err) {
      next(err);
    }
  },
};


module.exports = { passengerController };