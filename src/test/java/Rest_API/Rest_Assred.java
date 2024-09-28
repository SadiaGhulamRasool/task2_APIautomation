package Rest_API;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;

public class Rest_Assred {
	
	private final String BASE_URL = "https://restful-booker.herokuapp.com";

    // Positive Test Case: Create a booking
    @Test
    public void Booking() {
        String requestBody = "{\n" +
                "\"firstname\" : \"testFirstName\",\n" +
                "\"lastname\" : \"lastName\",\n" +
                "\"totalprice\" : 10,\n" +
                "\"depositpaid\" : true,\n" +
                "\"bookingdates\" : {\n" +
                "\"checkin\" : \"2022-01-01\",\n" +
                "\"checkout\" : \"2024-01-01\"\n" +
                "},\n" +
                "\"additionalneeds\" : \"testAdd\"\n" +
                "}";

        // Create booking and capture the response
        Response createResponse = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post(BASE_URL + "/booking")
                .then()
                .statusCode(200) // Check for successful creation
                .extract()
                .response();

        System.out.println("Create Response: " + createResponse.asString());
        Integer bookingId = createResponse.jsonPath().getInt("bookingid");
        Assert.assertNotNull(bookingId, "Booking ID is null!");
        System.out.println("Created Booking ID: " + bookingId);

        // Validate the created booking
        Response validateResponse = given()
                .when()
                .get(BASE_URL + "/booking/" + bookingId)
                .then()
                .statusCode(200) 
                .extract()
                .response();

        System.out.println("Validation Response: " + validateResponse.asString());

        // Assertions to validate the response
        Assert.assertEquals(validateResponse.jsonPath().getString("firstname"), "testFirstName", "Firstname mismatch!");
        Assert.assertEquals(validateResponse.jsonPath().getString("lastname"), "lastName", "Lastname mismatch!");
        Assert.assertEquals(validateResponse.jsonPath().getInt("totalprice"), 10, "Total price mismatch!");
        Assert.assertTrue(validateResponse.jsonPath().getBoolean("depositpaid"), "Deposit paid status mismatch!");
        Assert.assertEquals(validateResponse.jsonPath().getString("bookingdates.checkin"), "2022-01-01", "Checkin date mismatch!");
        Assert.assertEquals(validateResponse.jsonPath().getString("bookingdates.checkout"), "2024-01-01", "Checkout date mismatch!");
        Assert.assertEquals(validateResponse.jsonPath().getString("additionalneeds"), "testAdd", "Additional needs mismatch!");
    }

    // Negative Test Case 1: Missing Required Fields
    @Test
    public void Booking_MissingRequiredFields() {
        String requestBody = "{\n" +
                "\"lastname\" : \"lastName\",\n" +
                "\"totalprice\" : 10,\n" +
                "\"depositpaid\" : true,\n" +
                "\"bookingdates\" : {\n" +
                "\"checkin\" : \"2022-01-01\",\n" +
                "\"checkout\" : \"2024-01-01\"\n" +
                "}\n" +
                "}";

        Response createResponse = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post(BASE_URL + "/booking")
                .then()
                .statusCode(400) // 
                .extract()
                .response();

        System.out.println("Response for missing required fields: " + createResponse.asString());
    }

    //Invalid Data Types
    @Test
    public void Booking_InvalidDataTypes() {
        String requestBody = "{\n" +
                "\"firstname\" : \"testFirstName\",\n" +
                "\"lastname\" : \"lastName\",\n" +
                "\"totalprice\" : \"ten\",  // Invalid type\n" +
                "\"depositpaid\" : true,\n" +
                "\"bookingdates\" : {\n" +
                "\"checkin\" : \"2022-01-01\",\n" +
                "\"checkout\" : \"2024-01-01\"\n" +
                "},\n" +
                "\"additionalneeds\" : \"testAdd\"\n" +
                "}";

        Response createResponse = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post(BASE_URL + "/booking")
                .then()
                .statusCode(400) 
                .extract()
                .response();

        System.out.println("Response for invalid data types: " + createResponse.asString());
    }

    //Invalid Dates
    @Test
    public void Booking_InvalidDates() {
        String requestBody = "{\n" +
                "\"firstname\" : \"testFirstName\",\n" +
                "\"lastname\" : \"lastName\",\n" +
                "\"totalprice\" : 10,\n" +
                "\"depositpaid\" : true,\n" +
                "\"bookingdates\" : {\n" +
                "\"checkin\" : \"2024-01-01\",  // Check-in is after check-out\n" +
                "\"checkout\" : \"2022-01-01\"\n" +
                "},\n" +
                "\"additionalneeds\" : \"testAdd\"\n" +
                "}";

        Response createResponse = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .when()
                .post(BASE_URL + "/booking")
                .then()
                .statusCode(400)
                .extract()
                .response();

        System.out.println("Response for invalid dates: " + createResponse.asString());
    }
}
