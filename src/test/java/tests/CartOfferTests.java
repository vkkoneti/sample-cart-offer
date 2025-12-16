package tests;


import base.BaseTest;
import clients.CartApiClient;
import clients.OfferApiClient;
import lombok.var;
import models.CartApplyRequest;
import models.OfferRequest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CartOfferTests extends BaseTest {

    private final OfferApiClient offerApi = new OfferApiClient();
    private final CartApiClient cartApi = new CartApiClient();

    // -------------------
    // HAPPY PATH
    // -------------------

    @Test
    void flatx_should_apply_for_p1_segment() {
        segmentMock.mockUserSegment(1, "p1");

        offerApi.addOffer(new OfferRequest(
                1, "FLATX", 10, Arrays.asList(new String[]{"p1"})
        )).then().statusCode(anyOf(is(200), is(201)));

        cartApi.applyOffer(new CartApplyRequest(200, 1, 1))
                .then()
                .statusCode(200)
                // Example expected: 190 (as per statement)
                .body("cart_value", is(190));
    }

    @Test
    void flatpercent_should_apply_for_p1_segment() {
        segmentMock.mockUserSegment(1, "p1");

        offerApi.addOffer(new OfferRequest(
                1, "FLAT%", 10, Arrays.asList(new String[]{"p1"})
        )).then().statusCode(anyOf(is(200), is(201)));

        cartApi.applyOffer(new CartApplyRequest(200, 1, 1))
                .then()
                .statusCode(200)
                // Example expected: 180
                .body("cart_value", is(180));
    }

    // -------------------
    // SEGMENT BEHAVIOR
    // -------------------

    @Test
    void offer_should_not_apply_when_segment_mismatch() {
        segmentMock.mockUserSegment(1, "p2");

        offerApi.addOffer(new OfferRequest(
                1, "FLATX", 10, Arrays.asList(new String[]{"p1"})
        )).then().statusCode(anyOf(is(200), is(201)));

        cartApi.applyOffer(new CartApplyRequest(200, 1, 1))
                .then()
                .statusCode(200)
                // no discount, should remain same
                .body("cart_value", is(200));
    }

    @Test
    void should_handle_unknown_segment_gracefully_no_discount() {
        segmentMock.mockUserSegment(1, "p999");

        offerApi.addOffer(new OfferRequest(
                1, "FLATX", 10, Arrays.asList(new String[]{"p1"})
        )).then().statusCode(anyOf(is(200), is(201)));

        cartApi.applyOffer(new CartApplyRequest(200, 1, 1))
                .then()
                .statusCode(200)
                .body("cart_value", is(200));
    }

    // -------------------
    // VALIDATION - APPLY OFFER
    // -------------------

    @Test
    void apply_offer_should_fail_when_cart_value_missing() {
        segmentMock.mockUserSegment(1, "p1");

        cartApi.applyOfferRaw("{\"user_id\":1,\"restaurant_id\":1}")
                .then()
                .statusCode(anyOf(is(400), is(422)));
    }

    @Test
    void apply_offer_should_fail_when_user_id_missing() {
        cartApi.applyOfferRaw("{\"cart_value\":200,\"restaurant_id\":1}")
                .then()
                .statusCode(anyOf(is(400), is(422)));
    }

    @Test
    void apply_offer_should_fail_when_restaurant_id_missing() {
        cartApi.applyOfferRaw("{\"cart_value\":200,\"user_id\":1}")
                .then()
                .statusCode(anyOf(is(400), is(422)));
    }

    @Test
    void apply_offer_should_fail_when_cart_value_negative() {
        segmentMock.mockUserSegment(1, "p1");

        cartApi.applyOffer(new CartApplyRequest(-1, 1, 1))
                .then()
                .statusCode(anyOf(is(400), is(422)));
    }

    @Test
    void apply_offer_should_handle_cart_value_zero() {
        segmentMock.mockUserSegment(1, "p1");

        cartApi.applyOffer(new CartApplyRequest(0, 1, 1))
                .then()
                .statusCode(anyOf(is(200), is(400), is(422)));
        // depending on implementation, 0 may be allowed or rejected
    }

    // -------------------
    // VALIDATION - ADD OFFER
    // -------------------

    @Test
    void add_offer_should_fail_for_invalid_offer_type() {
        offerApi.addOffer(new OfferRequest(
                        1, "INVALID", 10, Arrays.asList(new String[]{"p1"})
                )).then()
                .statusCode(anyOf(is(400), is(422)));
    }

    @Test
    void add_offer_should_fail_when_offer_value_missing() {
        offerApi.addOfferRaw("{\"restaurant_id\":1,\"offer_type\":\"FLATX\",\"customer_segment\":[\"p1\"]}")
                .then()
                .statusCode(anyOf(is(400), is(422)));
    }

    @Test
    void add_offer_should_fail_when_customer_segment_missing() {
        offerApi.addOfferRaw("{\"restaurant_id\":1,\"offer_type\":\"FLATX\",\"offer_value\":10}")
                .then()
                .statusCode(anyOf(is(400), is(422)));
    }

    @Test
    void add_offer_should_fail_when_offer_value_negative() {
        offerApi.addOffer(new OfferRequest(
                        1, "FLATX", -10, Arrays.asList(new String[]{"p1"})
                )).then()
                .statusCode(anyOf(is(400), is(422)));
    }

    @Test
    void flatpercent_should_not_make_cart_value_negative() {
        segmentMock.mockUserSegment(1, "p1");

        offerApi.addOffer(new OfferRequest(
                1, "FLAT%", 99, Arrays.asList(new String[]{"p1"})
        )).then().statusCode(anyOf(is(200), is(201)));

        int cartValue = 10;
        int userId = 1;
        int restaurantId = 1;

        var response = cartApi.applyOffer(new CartApplyRequest(cartValue, userId, restaurantId))
                .then()
                .statusCode(200)
                .extract().jsonPath().getInt("cart_value");

        // ensure it does not go below 0
        assertThat(response, greaterThanOrEqualTo(0));
    }

    @Test
    void flatx_should_not_make_cart_value_negative() {
        segmentMock.mockUserSegment(1, "p1");

        offerApi.addOffer(new OfferRequest(
                1, "FLATX", 9999, Arrays.asList(new String[]{"p1"})
        )).then().statusCode(anyOf(is(200), is(201)));

        int cartValue = 50;

        int response = cartApi.applyOffer(new CartApplyRequest(cartValue, 1, 1))
                .then()
                .statusCode(200)
                .extract().jsonPath().getInt("cart_value");

        assertThat(response, greaterThanOrEqualTo(0));
    }
}



