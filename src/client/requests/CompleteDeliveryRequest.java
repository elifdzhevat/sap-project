package client.requests;

public class CompleteDeliveryRequest {
    private String action = "complete_delivery";
    private String deliveryId;

    public CompleteDeliveryRequest(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getAction() {
        return action;
    }

    public String getDeliveryId() {
        return deliveryId;
    }
}
