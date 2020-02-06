import bodyParser from "body-parser";
import cors from "cors";
import express from "express";
import logger from "morgan";

const API_PORT = 8080;
const app = express();
const router = express.Router();

app.use(cors());

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(logger("dev"));

router.get("/placeHolder", cors(), (request, response) => {
  response.status(200).send({
    success: "true",
    message: "Request successful"
  });
});

app.use("/api", router);
app.disable("etag");

app.listen(API_PORT, () => console.log(`LISTENING ON PORT ${API_PORT}/api`));
