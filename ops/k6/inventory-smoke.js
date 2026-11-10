import encoding from "k6/encoding";
import http from "k6/http";
import { check, sleep } from "k6";

const baseUrl = __ENV.BASE_URL;
const username = __ENV.APP_ADMIN_USERNAME;
const password = __ENV.APP_ADMIN_PASSWORD;

if (!baseUrl || !username || !password) {
  throw new Error("Set BASE_URL, APP_ADMIN_USERNAME, and APP_ADMIN_PASSWORD.");
}

export const options = {
  scenarios: {
    inventory_browse: {
      executor: "ramping-vus",
      startVUs: 5,
      stages: [
        { duration: "1m", target: 50 },
        { duration: "3m", target: 50 },
        { duration: "1m", target: 0 },
      ],
      gracefulRampDown: "30s",
    },
  },
  thresholds: {
    http_req_failed: ["rate<0.01"],
    http_req_duration: ["p(95)<500"],
  },
};

const authorization = `Basic ${encoding.b64encode(`${username}:${password}`)}`;

export default function () {
  const response = http.get(`${baseUrl}/api/v1/products?page=0`, {
    headers: {
      Authorization: authorization,
      Accept: "application/json",
    },
  });

  check(response, {
    "inventory API returns 200": (result) => result.status === 200,
    "inventory result stays bounded": (result) => {
      try {
        return result.json("content").length <= 25;
      } catch {
        return false;
      }
    },
  });
  sleep(1);
}
