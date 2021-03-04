require("dotenv").config();
const express = require("express");
const morgan = require("morgan");
const cookieSession = require("cookie-session");

const PORT = process.env.PORT || process.argv[2] || 3000;

const { getCurrentUser, errorHandler } = require("@utopia-airlines-wss/common/middleware");

const app = express();

app.use(
  morgan("tiny"),
  express.json(),
  cookieSession({
    name: "session",
    httpOnly: true,
    signed: false,
  }),
  getCurrentUser,
  require("./routes"),
  errorHandler
);

app.listen(PORT, () => console.log("Booking controller is running on " + PORT ));
