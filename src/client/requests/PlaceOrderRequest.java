package client.requests;

import java.util.List;

public class PlaceOrderRequest {
    private String action = "place_order";
    private String username;
    private List<String> products;

    public PlaceOrderRequest(String username, List<String> products) {
        this.username = username;
        this.products = products;
    }

    public String getAction() {
        return action;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getProducts() {
        return products;
    }
}
