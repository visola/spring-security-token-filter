### Simple Authentication Token

A simple example that shows how to use the JWT authentication library with a REST API. This example uses an in memory
user for testing.

To login, you send an HTTP request passing in the username and credentials like te following:

```http
POST http://localhost:8080/api/v1/login
Content-type: application/json

{"username":"john","password":"pass"}
```  

As the response you get the JWT authentication token:

```json
{
  "token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwicm9sZXMiOlsiVVNFUiJdLCJleHAiOjE1MzI4MDYyNjYsImlhdCI6MTUzMjc3NzQ2Nn0.r0ufzALuuvlz6Lwdmnv4NymPNia2FQGv0ClOfA87Xfc"
}
```

This JWT token can be used to make a secure request like the following:
```http
GET http://localhost:8080/api/v1/secure
Authorization:  Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIiwicm9sZXMiOlsiVVNFUiJdLCJleHAiOjE1MzI4MDYyNjYsImlhdCI6MTUzMjc3NzQ2Nn0.r0ufzALuuvlz6Lwdmnv4NymPNia2FQGv0ClOfA87Xfc
```

which gets the following response:
```json
{"message":"Hello John!"}
```