const { Passenger } = require("@utopia-airlines-wss/common/models");
const {  NotFoundError, BadRequestError  } = require("@utopia-airlines-wss/common/errors");

const passengerService = {
  async findAllPassengers() {
    return  (await Passenger.findAll({})).map((passenger) => passenger.toJSON("full"));
  },
  async findPassengerById({ id }) {
    const passenger = await Passenger.findByPk(id);
    if (!passenger) throw new NotFoundError("cannot find passenger");
    return passenger.toJSON("full");
  },
  async updatePassenger(id, { name, dob, gender, address } = {}) {
    const passenger = await Passenger.findOne({ where: { id }, limit: 1 });
    if (!passenger) throw new NotFoundError("cannot find passenger");
    try {
      const data = Object.entries({
        givenName: name?.given,
        familyName: name?.family,
        dob,
        gender,
        address,
      }).filter(([_key, value]) => value != null)
        .reduce((data, [key, value]) => (data[key] = value, data), {});
      await passenger.update(data);
      return passenger.toJSON("full");
    } catch(err) {
      throw new BadRequestError();
    }
  },
};

module.exports = { passengerService };
