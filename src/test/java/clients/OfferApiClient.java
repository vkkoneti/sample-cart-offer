package clients;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.OfferRequest;
import utils.TestConfig;
public class OfferApiClient {
    public Response addOffer(OfferRequest request) {
        return RestAssured.given()
                .baseUri(TestConfig.BASE_API_URL)
                .contentType("application/json")
                .body(request)
                .when()
                .post(TestConfig.OFFER_ENDPOINT);
    }
}
