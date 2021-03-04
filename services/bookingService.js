const { Op } = require("sequelize");
const { sequelize } = require("@utopia-airlines-wss/common/db");
const { Booking } = require("@utopia-airlines-wss/common/models");
const { NotFoundError, handleMutationError, BadRequestError } = require("@utopia-airlines-wss/common/errors");
const { removeUndefined } = require("@utopia-airlines-wss/common/util");

const findBookingById = async (id, options) => {
  const booking = await Booking.findByPk(id, options);
  if (!booking) throw new NotFoundError("cannot find booking");
  return booking;
};

const getDateRange = date => {
  const startTime = new Date(date.getTime());
  startTime.setHours(0, 0, 0, 0);
  const endTime = new Date(startTime.getTime());
  endTime.setDate(endTime.getDate() + 1);
  return [startTime, endTime];
};

const bookingService = {
  async findAllBookings({
    isActive = true, offset = 0, limit = 10,
    gender, origin,
    destination, departureDate, fid, id, sort, order = "DESC"
  }) {
    if (limit > 10000)
      throw new BadRequestError("Limit exceeds maximum of 10000");
    isActive = !(isActive && isActive === "false");
    const bookings = await Booking.findAndCountAll({
      limit: +limit,
      offset: +offset,
      distinct: true,
      subQuery: false,
      where: {
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
      },
      order: (sort) ? [["flights", sort, order]] : null,
      include: [
        {
          association: "agent",
          include: "agent",
        },
        {
          association: "user",
          include: "user",
        },
        "guest",
        {
          association: "flights",
          // attributes: ["departureTime", "seatPrice", "maxCapacity", "reservedSeats", "passengerCount", "availableSeats"],
          // attributes: ["departureTime", "seatPrice", "maxCapacity", "reservedSeats", "passengerCount", "availableSeats", [sequelize.fn("sum", sequelize.col("seat_price")), "total"]],
          // group: ["flights.id"],
          // raw: true,
          // order: sequelize.literal('total DESC'),
          include: {
            association: "route",
            include: [
              "origin",
              "destination"
            ],
          },
          through: { attributes: [] },
        },
        {
          association: "passengers",
          separate: true,
          where: removeUndefined({
            gender: gender && {
              [Op.substring]: gender,
            },
          })
        },
      ],
    });
    return bookings;
  },
  async findBookingById({ id }) {
    const booking = await findBookingById(
      id,
      {
        include: [
          {
            association: "agent",
            include: "agent",
          },
          {
            association: "user",
            include: "user",
          },
          "guest",
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
      }
    );
    return booking;
  },
  async updateBooking(id, { isActive }) {
    const booking = await findBookingById(id);
    const transaction = await sequelize.transaction();
    try {
      await booking.update({
        isActive,
      }, {
        transaction,
      });
      await transaction.commit();
      return booking;
    } catch (err) {
      await transaction.rollback();
      handleMutationError(err);
    }
  },
  async deleteBookingById(id) {
    const booking = await findBookingById(id);
    await booking.destroy();
  },
};

module.exports = { bookingService };
