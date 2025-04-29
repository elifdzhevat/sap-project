package client;

import client.requests.PlaceOrderRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class OrderManagementFrame extends JFrame {
    private JButton placeOrderButton;
    private JButton viewOrdersButton;
    private JButton backButton;
    private JButton refreshButton;
    private JList<String> productList;
    private JComboBox<String> categoryComboBox;
    private List<String> allProducts;

    public OrderManagementFrame(String role) {
        setTitle("Order Management - " + role);
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        allProducts = new ArrayList<>();

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());

        String[] categories = {"All", "Pizza", "Burgers", "Sushi", "Drinks"};
        categoryComboBox = new JComboBox<>(categories);
        refreshButton = new JButton("Refresh Products");

        northPanel.add(new JLabel("Select Category:"));
        northPanel.add(categoryComboBox);
        northPanel.add(refreshButton);

        add(northPanel, BorderLayout.NORTH);

        productList = new JList<>();
        JScrollPane productScrollPane = new JScrollPane(productList);
        add(productScrollPane, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());

        placeOrderButton = new JButton("Place New Order");
        viewOrdersButton = new JButton("View My Orders");
        backButton = new JButton("Back");

        southPanel.add(placeOrderButton);
        southPanel.add(viewOrdersButton);
        southPanel.add(backButton);

        add(southPanel, BorderLayout.SOUTH);

        categoryComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                filterProducts(selectedCategory);
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadProducts();
                categoryComboBox.setSelectedItem("All");
            }
        });

        placeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String username = ClientMain.getCurrentUsername();
                    List<String> selectedProducts = productList.getSelectedValuesList();
                    if (selectedProducts == null || selectedProducts.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please select at least one product.", "Warning", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    PlaceOrderRequest orderRequest = new PlaceOrderRequest(username, selectedProducts);
                    String requestJson = new Gson().toJson(orderRequest);

                    ClientMain.sendMessage(requestJson);
                    String responseJson = ClientMain.readMessage();

                    JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                    if (response.get("status").getAsString().equals("success")) {
                        JOptionPane.showMessageDialog(null, "Order placed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Order Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error during placing order.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        viewOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ViewOrdersFrame(role);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new MainMenuFrame(role);
            }
        });

        loadProducts();
        setVisible(true);
    }

    private void loadProducts() {
        try {
            JsonObject request = new JsonObject();
            request.addProperty("action", "list_products");

            ClientMain.sendMessage(request.toString());
            String responseJson = ClientMain.readMessage();
            JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

            allProducts.clear();
            if (response.get("status").getAsString().equals("success")) {
                for (var elem : response.getAsJsonArray("products")) {
                    allProducts.add(elem.getAsString());
                }
                filterProducts("All"); // Default filter to All
            } else {
                JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Failed to Load Products", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading products.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterProducts(String category) {
        DefaultListModel<String> filteredModel = new DefaultListModel<>();

        for (String product : allProducts) {
            if (category.equals("All") || product.toLowerCase().contains(category.toLowerCase())) {
                filteredModel.addElement(product);
            }
        }

        productList.setModel(filteredModel);
    }
}
