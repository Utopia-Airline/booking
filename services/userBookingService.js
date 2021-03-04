const { Op } = require("sequelize");
const { sequelize } = require("@utopia-airlines-wss/common/db");
const { Booking, UserBooking } = require("@utopia-airlines-wss/common/models");
const { removeUndefined } = require("@utopia-airlines-wss/common/util");
const {
  StandardizedError,
  NotFoundError,
  handleMutationError,
  AuthorizationError
} = require("@utopia-airlines-wss/common/errors");

const { flightService } = require("./flightService");

const getDateRange = date => {
  const startTime = new Date(date.getTime());
  startTime.setHours(0, 0, 0, 0);
  const endTime = new Date(startTime.getTime());
  endTime.setDate(endTime.getDate() + 1);
  return [startTime, endTime];
};

const userBookingService = {
  async findAllUserBookings({
    isActive = true, offset = 0, limit = 10,
    gender, origin, userId,
    destination, departureDate, fid, id, sort, order = "ASC"
  }) {
    if (limit > 10000)
      throw new BadRequestError("Limit exceeds maximum of 10000");
    isActive = !(isActive && isActive === "false");
    const where = {
      isActive,
      ...removeUndefined({
        "$flights.departure_time$": departureDate ? { [Op.between]: getDateRange(new Date(departureDate)), } : null,
        "$flights.id$": fid && { [Op.eq]: fid },
      }),
      [Op.and]: (origin || destination) ? [
        [
          origin ?
            {
              [Op.or]: [
                { "$flights.route.origin_id$": { [Op.substring]: origin }, },
                { "$flights.route.origin.name$": { [Op.substring]: origin }, },
                { "$flights.route.origin.city$": { [Op.substring]: origin }, },
                { "$flights.route.origin.country$": { [Op.substring]: origin }, },
              ]
            } : null,
          destination ?
            {
              [Op.or]: [
                { "$flights.route.destination_id$": { [Op.substring]: destination }, },
                { "$flights.route.destination.name$": { [Op.substring]: destination }, },
                { "$flights.route.destination.city$": { [Op.substring]: destination }, },
                { "$flights.route.destination.country$": { [Op.substring]: destination }, }
              ]
            } : null
        ]
      ] : [],
    };
    if (userId != null) where.userId = userId;
    const bookings = await UserBooking.findAndCountAll({
      limit: +limit,
      offset: +offset,
      distinct: true,
      subQuery: false,
      order: (sort) ? [["flights", sort, order]] : null,
      where,
      include: [
        "agent",
        "user",
        {
          association: "flights",
          include: {
            association: "route",
            include: ["origin", "destination"],
          },
          through: { attributes: [] },
        },
        "passengers",
      ],
    });
    return bookings;
  },
  async findUserBookingById({ id, userId }) {
    const booking = await UserBooking.findByPk(
      id,
      {
        include: [
          "agent",
          "user",
          {
            association: "flights",
            include: {
              association: "route",
              include: ["origin", "destination"],
            },
            through: { attributes: [] },
          },
          {
            association: "passengers",
            separate: true
          }
        ],
      }
    );
    if (!booking) throw new NotFoundError("cannot find booking");
    if (userId != null && booking.userId !== userId)
      throw new AuthorizationError();
    return booking;
  },
  async createUserBooking({ flights, passengers, userId, agentId }) {
    const transaction = await sequelize.transaction();
    try {
      await flightService.validateFlights({ flights, passengerCount: passengers.length });
      const data = {
        user: {
          userId,
        },
        passengers: passengers.map(
          ({ name: { given: givenName, family: familyName } = {}, ...rest }) => ({ ...rest, givenName, familyName })
        ),
      };
      if (agentId != null) data.agent = { agentId };
      const booking = await Booking.create(
        data,
        {
          transaction,
          include: [
            "passengers",
            "agent",
            "user",
          ],
        }
      );
      await booking.setFlights(flights, { transaction });
      await transaction.commit();
      return booking;
    } catch (err) {
      await transaction.rollback();
      if (err instanceof StandardizedError) throw err;
      handleMutationError(err);
    }
  },
};

module.exports = { userBookingService };
