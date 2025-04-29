package client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ManageEntitiesFrame extends JFrame {
    private JButton addProductButton;
    private JButton editProductButton;
    private JButton deleteProductButton;
    private JButton addRestaurantButton;
    private JButton editRestaurantButton;
    private JButton deleteRestaurantButton;
    private JButton backButton;
    private JList<String> productList;
    private JList<String> restaurantList;
    private List<String> allProducts;
    private List<String> allRestaurants;

    public ManageEntitiesFrame(String role) {
        setTitle("Manage Restaurants and Products - " + role);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        allProducts = new ArrayList<>();
        allRestaurants = new ArrayList<>();

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        productList = new JList<>();
        restaurantList = new JList<>();
        centerPanel.add(new JScrollPane(productList));
        centerPanel.add(new JScrollPane(restaurantList));
        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        addProductButton = new JButton("Add Product");
        editProductButton = new JButton("Edit Product");
        deleteProductButton = new JButton("Delete Product");
        addRestaurantButton = new JButton("Add Restaurant");
        editRestaurantButton = new JButton("Edit Restaurant");
        deleteRestaurantButton = new JButton("Delete Restaurant");
        backButton = new JButton("Back");

        buttonPanel.add(addProductButton);
        buttonPanel.add(editProductButton);
        buttonPanel.add(deleteProductButton);
        buttonPanel.add(addRestaurantButton);
        buttonPanel.add(editRestaurantButton);
        buttonPanel.add(deleteRestaurantButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.EAST);

        addProductButton.addActionListener(e -> addProduct());
        editProductButton.addActionListener(e -> editProduct());
        deleteProductButton.addActionListener(e -> deleteProduct());
        addRestaurantButton.addActionListener(e -> addRestaurant());
        editRestaurantButton.addActionListener(e -> editRestaurant());
        deleteRestaurantButton.addActionListener(e -> deleteRestaurant());
        backButton.addActionListener(e -> {
            dispose();
            new MainMenuFrame(role);
        });

        loadProducts();
        loadRestaurants();

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
                productList.setListData(allProducts.toArray(new String[0]));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load products.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRestaurants() {
        try {
            JsonObject request = new JsonObject();
            request.addProperty("action", "list_restaurants");
            ClientMain.sendMessage(request.toString());
            String responseJson = ClientMain.readMessage();
            JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

            allRestaurants.clear();
            if (response.get("status").getAsString().equals("success")) {
                for (var elem : response.getAsJsonArray("restaurants")) {
                    allRestaurants.add(elem.getAsString());
                }
                restaurantList.setListData(allRestaurants.toArray(new String[0]));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load restaurants.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void addProduct() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField restaurantField = new JTextField();

        Object[] message = {
                "Product Name:", nameField,
                "Price:", priceField,
                "Restaurant:", restaurantField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add New Product", JOptionPane.OK_CANCEL_OPTION);
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
                    JOptionPane.showMessageDialog(null, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts(); // презареждаме списъка с продукти
                } else {
                    JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to add product.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void editProduct() {
        String selectedProduct = productList.getSelectedValue();
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(null, "Please select a product to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField restaurantField = new JTextField();

        Object[] message = {
                "New Product Name:", nameField,
                "New Price:", priceField,
                "New Restaurant:", restaurantField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Edit Product", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String newName = nameField.getText();
                double newPrice = Double.parseDouble(priceField.getText());
                String newRestaurant = restaurantField.getText();

                JsonObject request = new JsonObject();
                request.addProperty("action", "edit_product");
                request.addProperty("originalName", selectedProduct);
                request.addProperty("newName", newName);
                request.addProperty("newPrice", newPrice);
                request.addProperty("newRestaurant", newRestaurant);

                ClientMain.sendMessage(request.toString());
                String responseJson = ClientMain.readMessage();
                JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                if (response.get("status").getAsString().equals("success")) {
                    JOptionPane.showMessageDialog(null, "Product edited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts(); // Презареждаме списъка
                } else {
                    JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to edit product.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void deleteProduct() {
        String selectedProduct = productList.getSelectedValue();
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(null, "Please select a product to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + selectedProduct + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                JsonObject request = new JsonObject();
                request.addProperty("action", "delete_product");
                request.addProperty("name", selectedProduct);

                ClientMain.sendMessage(request.toString());
                String responseJson = ClientMain.readMessage();
                JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                if (response.get("status").getAsString().equals("success")) {
                    JOptionPane.showMessageDialog(null, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadProducts(); // Презареждаме списъка
                } else {
                    JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to delete product.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void addRestaurant() {
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();

        Object[] message = {
                "Restaurant Name:", nameField,
                "Address (optional):", addressField,
                "Phone (optional):", phoneField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add New Restaurant", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String address = addressField.getText();
                String phone = phoneField.getText();

                JsonObject request = new JsonObject();
                request.addProperty("action", "add_restaurant");
                request.addProperty("name", name);
                request.addProperty("address", address);
                request.addProperty("phone", phone);

                ClientMain.sendMessage(request.toString());
                String responseJson = ClientMain.readMessage();
                JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                if (response.get("status").getAsString().equals("success")) {
                    JOptionPane.showMessageDialog(null, "Restaurant added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadRestaurants(); // Презареждаме ресторантите
                } else {
                    JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to add restaurant.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void editRestaurant() {
        String selectedRestaurant = restaurantList.getSelectedValue();
        if (selectedRestaurant == null) {
            JOptionPane.showMessageDialog(null, "Please select a restaurant to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();

        Object[] message = {
                "New Restaurant Name:", nameField,
                "New Address:", addressField,
                "New Phone:", phoneField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Edit Restaurant", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String newName = nameField.getText();
                String newAddress = addressField.getText();
                String newPhone = phoneField.getText();

                JsonObject request = new JsonObject();
                request.addProperty("action", "edit_restaurant");
                request.addProperty("originalName", selectedRestaurant);
                request.addProperty("newName", newName);
                request.addProperty("newAddress", newAddress);
                request.addProperty("newPhone", newPhone);

                ClientMain.sendMessage(request.toString());
                String responseJson = ClientMain.readMessage();
                JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                if (response.get("status").getAsString().equals("success")) {
                    JOptionPane.showMessageDialog(null, "Restaurant edited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadRestaurants(); // Презареждаме списъка
                } else {
                    JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to edit restaurant.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void deleteRestaurant() {
        String selectedRestaurant = restaurantList.getSelectedValue();
        if (selectedRestaurant == null) {
            JOptionPane.showMessageDialog(null, "Please select a restaurant to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + selectedRestaurant + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                JsonObject request = new JsonObject();
                request.addProperty("action", "delete_restaurant");
                request.addProperty("name", selectedRestaurant);

                ClientMain.sendMessage(request.toString());
                String responseJson = ClientMain.readMessage();
                JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                if (response.get("status").getAsString().equals("success")) {
                    JOptionPane.showMessageDialog(null, "Restaurant deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadRestaurants(); // Презареждаме списъка
                } else {
                    JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to delete restaurant.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
