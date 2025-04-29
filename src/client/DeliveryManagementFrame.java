package client;

import client.requests.AcceptDeliveryRequest;
import client.requests.CompleteDeliveryRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeliveryManagementFrame extends JFrame {
    private JButton acceptDeliveryButton;
    private JButton completeDeliveryButton;
    private JButton backButton;
    private JButton refreshDeliveriesButton;
    private JList<String> deliveryList;


    public DeliveryManagementFrame(String role) {
        setTitle("Delivery Management - " + role);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        acceptDeliveryButton = new JButton("Accept Delivery");
        completeDeliveryButton = new JButton("Complete Delivery");
        backButton = new JButton("Back");
        refreshDeliveriesButton = new JButton("Refresh Deliveries");
        deliveryList = new JList<>();



        add(acceptDeliveryButton);
        add(completeDeliveryButton);
        add(backButton);
        add(new JScrollPane(deliveryList));
        add(refreshDeliveriesButton);


        acceptDeliveryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String deliveryId = deliveryList.getSelectedValue();
                    if (deliveryId == null) {
                        JOptionPane.showMessageDialog(null, "Please select a delivery to accept.", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    String username = ClientMain.getCurrentUsername();

                    AcceptDeliveryRequest acceptRequest = new AcceptDeliveryRequest(deliveryId, username);
                    String requestJson = new Gson().toJson(acceptRequest);

                    ClientMain.sendMessage(requestJson);
                    String responseJson = ClientMain.readMessage();

                    JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                    if (response.get("status").getAsString().equals("success")) {
                        JOptionPane.showMessageDialog(null, "Delivery accepted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Accept Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error during accepting delivery.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        completeDeliveryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String deliveryId = deliveryList.getSelectedValue();
                    if (deliveryId == null) {
                        JOptionPane.showMessageDialog(null, "Please select a delivery to complete.", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    CompleteDeliveryRequest completeRequest = new CompleteDeliveryRequest(deliveryId);
                    String requestJson = new Gson().toJson(completeRequest);

                    ClientMain.sendMessage(requestJson);
                    String responseJson = ClientMain.readMessage();

                    JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                    if (response.get("status").getAsString().equals("success")) {
                        JOptionPane.showMessageDialog(null, "Delivery completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Complete Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error during completing delivery.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new MainMenuFrame(role);
            }
        });

        refreshDeliveriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDeliveries(); 
            }
        });


        setVisible(true);
    }

    private void loadDeliveries() {
        try {
            JsonObject request = new JsonObject();
            request.addProperty("action", "list_deliveries");
            ClientMain.sendMessage(request.toString());
            String responseJson = ClientMain.readMessage();
            JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

            DefaultListModel<String> deliveryModel = new DefaultListModel<>();
            if (response.get("status").getAsString().equals("success")) {
                for (var elem : response.getAsJsonArray("deliveries")) {
                    deliveryModel.addElement(elem.getAsString());
                }
                deliveryList.setModel(deliveryModel);
            } else {
                JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading deliveries.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
