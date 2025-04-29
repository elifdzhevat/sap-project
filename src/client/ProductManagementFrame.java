package client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;

public class ProductManagementFrame extends JFrame {
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton filterButton;
    private JButton backButton;
    private JList<String> productList;
    private DefaultListModel<String> productModel;
    private String role;

    public ProductManagementFrame(String role) {
        this.role = role;

        setTitle("Product Management - " + role);
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        productModel = new DefaultListModel<>();
        productList = new JList<>(productModel);
        add(new JScrollPane(productList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        addButton = new JButton("Add Product");
        editButton = new JButton("Edit Product");
        deleteButton = new JButton("Delete Product");
        filterButton = new JButton("Filter Products by Category");
        backButton = new JButton("Back");

        if (role.equalsIgnoreCase("employee")) {
            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
        } else if (role.equalsIgnoreCase("client")) {
            buttonPanel.add(filterButton);
        }

        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.EAST);

        // Action Listeners
        addButton.addActionListener(e -> openAddProductDialog());
        editButton.addActionListener(e -> openEditProductDialog());
        deleteButton.addActionListener(e -> deleteSelectedProduct());
        filterButton.addActionListener(e -> {
            dispose();
            new OrderManagementFrame(role);
        });
        backButton.addActionListener(e -> {
            dispose();
            new MainMenuFrame(role);
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

            productModel.clear();
            if (response.get("status").getAsString().equals("success")) {
                for (var elem : response.getAsJsonArray("products")) {
                    productModel.addElement(elem.getAsString());
                }
            } else {
                JOptionPane.showMessageDialog(this, response.get("message").getAsString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading products.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddProductDialog() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField restaurantField = new JTextField();

        Object[] fields = {
                "Product Name:", nameField,
                "Price:", priceField,
                "Restaurant:", restaurantField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add New Product", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                String restaurant = restaurantField.getText();

                JsonObject request = new JsonObject();
                request.addProperty("action", "add_product");
                request.addProperty("name", name);
                request.addProperty("price", price);
                request.addProperty("restaurant", restaurant);

                ClientMain.sendMessage(request.toString());
                String responseJson = ClientMain.readMessage();
                JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                if (response.get("status").getAsString().equals("success")) {
                    JOptionPane.showMessageDialog(this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(this, response.get("message").getAsString(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openEditProductDialog() {
        String selected = productList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a product to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField restaurantField = new JTextField();

        Object[] fields = {
                "New Product Name:", nameField,
                "New Price:", priceField,
                "New Restaurant:", restaurantField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Edit Product", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String newName = nameField.getText();
                double newPrice = Double.parseDouble(priceField.getText());
                String newRestaurant = restaurantField.getText();

                JsonObject request = new JsonObject();
                request.addProperty("action", "edit_product");
                request.addProperty("originalName", selected);
                request.addProperty("newName", newName);
                request.addProperty("newPrice", newPrice);
                request.addProperty("newRestaurant", newRestaurant);

                ClientMain.sendMessage(request.toString());
                String responseJson = ClientMain.readMessage();
                JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                if (response.get("status").getAsString().equals("success")) {
                    JOptionPane.showMessageDialog(this, "Product edited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(this, response.get("message").getAsString(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedProduct() {
        String selected = productList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + selected + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                JsonObject request = new JsonObject();
                request.addProperty("action", "delete_product");
                request.addProperty("name", selected);

                ClientMain.sendMessage(request.toString());
                String responseJson = ClientMain.readMessage();
                JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                if (response.get("status").getAsString().equals("success")) {
                    JOptionPane.showMessageDialog(this, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(this, response.get("message").getAsString(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting product.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

