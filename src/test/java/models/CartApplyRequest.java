package models;

public class CartApplyRequest {
    public int cart_value;
    public int user_id;
    public int restaurant_id;

    public CartApplyRequest(int cartValue, int userId, int restaurantId) {
        this.cart_value = cartValue;
        this.user_id = userId;
        this.restaurant_id = restaurantId;
    }
}
