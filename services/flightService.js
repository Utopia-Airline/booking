const { Op } = require("sequelize");
const { Flight } = require("@utopia-airlines-wss/common/models");
const { BadRequestError   } = require("@utopia-airlines-wss/common/errors");


const flightService = {
  async validateFlights({ flights, passengerCount }) {
    const fullFlightCount = await Flight.count({
      where: {
        id: { [Op.or]: flights },
        availableSeats: { [Op.lt]: passengerCount },
      },
    });
    if (fullFlightCount)
      throw new BadRequestError("not enough seats");
  },
};

module.exports = { flightService };
