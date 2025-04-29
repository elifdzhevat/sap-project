package client.requests;

public class AcceptDeliveryRequest {
    private String action = "accept_delivery";
    private String deliveryId;
    private String deliveryUsername;

    public AcceptDeliveryRequest(String deliveryId, String deliveryUsername) {
        this.deliveryId = deliveryId;
        this.deliveryUsername = deliveryUsername;
    }

    public String getAction() {
        return action;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public String getDeliveryUsername() {
        return deliveryUsername;
    }
}
