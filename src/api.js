
export const url =
  process.env.NODE_ENV === "development"
    ? "http://localhost:8383/api/v1/"
    : "/api/v1/";

export const token =
  process.env.NODE_ENV === "development"
    ? "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJndWVzdEBsYW1pc3BsdXMub3JnIiwiYXV0aCI6IlN1cGVyIEFkbWluIiwibmFtZSI6Ikd1ZXN0IEd1ZXN0IiwiZXhwIjoxNzI4NTgxOTg1fQ.Mb_yaMQr7CVnS6yLXVCcNSfEkv5PX12Wuf6MQ109mXEiXdfpGorkAWrWLFLOiPBJ5mrUOZcyqav_MpFNWSA30w"
    : new URLSearchParams(window.location.search).get("jwt");

