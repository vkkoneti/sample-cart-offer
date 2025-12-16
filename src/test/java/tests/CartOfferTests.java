package tests;

import base.BaseTest;
import clients.CartApiClient;
import clients.OfferApiClient;
import models.CartApplyRequest;
import models.OfferRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;

public class CartOfferTests extends BaseTest {

    private final OfferApiClient offerApi = new OfferApiClient();
    private final CartApiClient cartApi = new CartApiClient();

    @Test
    public void flatX_shouldApplyForP1() {
        segmentMock.mockUserSegment(1, "p1");

        offerApi.addOffer(new OfferRequest(1, "FLATX", 10, List.of("p1")))
                .then().statusCode(200);

        cartApi.applyOffer(new CartApplyRequest(200, 1, 1))
                .then()
                .statusCode(200)
                .body("cart_value", equalTo(190));
    }

    @Test
    public void flatXPercent_shouldApplyForP1() {
        segmentMock.mockUserSegment(2, "p1");

        offerApi.addOffer(new OfferRequest(1, "FLATX%", 10, List.of("p1")))
                .then().statusCode(200);

        cartApi.applyOffer(new CartApplyRequest(200, 2, 1))
                .then()
                .statusCode(200)
                .body("cart_value", equalTo(180));
    }

    @Test
    public void offer_shouldNotApply_whenSegmentMismatch() {
        segmentMock.mockUserSegment(3, "p2");

        offerApi.addOffer(new OfferRequest(1, "FLATX", 10, List.of("p1")))
                .then().statusCode(200);

        cartApi.applyOffer(new CartApplyRequest(200, 3, 1))
                .then()
                .statusCode(200)
                .body("cart_value", equalTo(200));
    }
}
