package clients;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.CartApplyRequest;
import utils.TestConfig;
public class CartApiClient {
    public Response applyOffer(CartApplyRequest request) {
        return RestAssured.given()
                .baseUri(TestConfig.BASE_API_URL)
                .contentType("application/json")
                .body(request)
                .when()
                .post(TestConfig.APPLY_OFFER_ENDPOINT);
    }

    public Response applyOfferRaw(String rawJson) {
        return RestAssured
                .given()
                .baseUri(TestConfig.BASE_API_URL)
                .contentType("application/json")
                .body(rawJson)
                .when()
                .post(TestConfig.APPLY_OFFER_ENDPOINT);
    }

}
