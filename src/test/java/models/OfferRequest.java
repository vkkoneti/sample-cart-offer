package models;

import java.util.List;

public class OfferRequest {
    public int restaurant_id;
    public String offer_type;           // "FLATX" or "FLATX%"
    public int offer_value;
    public List<String> customer_segment;

    public OfferRequest(int restaurantId, String offerType, int offerValue, List<String> segments) {
        this.restaurant_id = restaurantId;
        this.offer_type = offerType;
        this.offer_value = offerValue;
        this.customer_segment = segments;
    }
}
