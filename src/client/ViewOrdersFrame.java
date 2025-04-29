package client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ViewOrdersFrame extends JFrame {
    private JList<String> ordersList;
    private JButton backButton;
    private List<String> myOrders;

    public ViewOrdersFrame(String role) {
        setTitle("My Orders - " + ClientMain.getCurrentUsername());
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        myOrders = new ArrayList<>();

        ordersList = new JList<>();
        JScrollPane scrollPane = new JScrollPane(ordersList);
        add(scrollPane, BorderLayout.CENTER);

        backButton = new JButton("Back");
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            dispose();
            new OrderManagementFrame(role);
        });

        loadMyOrders();
        setVisible(true);
    }

    private void loadMyOrders() {
        try {
            JsonObject request = new JsonObject();
            request.addProperty("action", "list_orders");
            request.addProperty("username", ClientMain.getCurrentUsername());

            ClientMain.sendMessage(request.toString());
            String responseJson = ClientMain.readMessage();
            JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

            myOrders.clear();
            if (response.get("status").getAsString().equals("success")) {
                for (var elem : response.getAsJsonArray("orders")) {
                    myOrders.add(elem.getAsString());
                }

                if (myOrders.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Нямате направени поръчки.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }

                ordersList.setListData(myOrders.toArray(new String[0]));
            } else {
                JOptionPane.showMessageDialog(this, response.get("message").getAsString(), "Failed to Load Orders", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading orders.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
