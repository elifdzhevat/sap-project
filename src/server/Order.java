package server;

import java.util.List;

public class Order {
    private String id;
    private String username;
    private List<String> products;
    private String status;
    private String date;
    private String deliveryUsername;

    public Order() {
    }

    public Order(String id, String username, List<String> products, String status, String date) {
        this.id = id;
        this.username = username;
        this.products = products;
        this.status = status;
        this.date = date;
        this.deliveryUsername = "";
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getProducts() {
        return products;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public String getDeliveryUsername() {
        return deliveryUsername;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDeliveryUsername(String deliveryUsername) {
        this.deliveryUsername = deliveryUsername;
    }
}

