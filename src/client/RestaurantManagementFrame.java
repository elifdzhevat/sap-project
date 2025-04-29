package client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;

public class RestaurantManagementFrame extends JFrame {
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton backButton;
    private JList<String> restaurantList;
    private DefaultListModel<String> restaurantModel;
    private String role;

    public RestaurantManagementFrame(String role) {
        this.role = role;

        setTitle("Restaurant Management - " + role);
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        restaurantModel = new DefaultListModel<>();
        restaurantList = new JList<>(restaurantModel);
        add(new JScrollPane(restaurantList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        addButton = new JButton("Add Restaurant");
        editButton = new JButton("Edit Restaurant");
        deleteButton = new JButton("Delete Restaurant");
        backButton = new JButton("Back");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.EAST);

        addButton.addActionListener(e -> openAddRestaurantDialog());
        editButton.addActionListener(e -> openEditRestaurantDialog());
        deleteButton.addActionListener(e -> deleteSelectedRestaurant());
        backButton.addActionListener(e -> {
            dispose();
            new MainMenuFrame(role);
        });

        loadRestaurants();
        setVisible(true);
    }

    private void loadRestaurants() {
        try {
            JsonObject request = new JsonObject();
            request.addProperty("action", "list_restaurants");

            ClientMain.sendMessage(request.toString());
            String responseJson = ClientMain.readMessage();
            JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

            restaurantModel.clear();
            if (response.get("status").getAsString().equals("success")) {
                for (var elem : response.getAsJsonArray("restaurants")) {
                    restaurantModel.addElement(elem.getAsString());
                }
            } else {
                JOptionPane.showMessageDialog(this, response.get("message").getAsString(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading restaurants.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openAddRestaurantDialog() {
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();

        Object[] fields = {
                "Restaurant Name:", nameField,
                "Address:", addressField,
                "Phone:", phoneField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Add New Restaurant", JOptionPane.OK_CANCEL_OPTION);

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
                    JOptionPane.showMessageDialog(this, "Restaurant added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadRestaurants();
                } else {
                    JOptionPane.showMessageDialog(this, response.get("message").getAsString(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openEditRestaurantDialog() {
        String selected = restaurantList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();

        Object[] fields = {
                "New Restaurant Name:", nameField,
                "New Address:", addressField,
                "New Phone:", phoneField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Edit Restaurant", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                String newName = nameField.getText();
                String newAddress = addressField.getText();
                String newPhone = phoneField.getText();

                JsonObject request = new JsonObject();
                request.addProperty("action", "edit_restaurant");
                request.addProperty("originalName", selected);
                request.addProperty("newName", newName);
                request.addProperty("newAddress", newAddress);
                request.addProperty("newPhone", newPhone);

                ClientMain.sendMessage(request.toString());
                String responseJson = ClientMain.readMessage();
                JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                if (response.get("status").getAsString().equals("success")) {
                    JOptionPane.showMessageDialog(this, "Restaurant edited successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadRestaurants();
                } else {
                    JOptionPane.showMessageDialog(this, response.get("message").getAsString(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedRestaurant() {
        String selected = restaurantList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a restaurant to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + selected + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                JsonObject request = new JsonObject();
                request.addProperty("action", "delete_restaurant");
                request.addProperty("name", selected);

                ClientMain.sendMessage(request.toString());
                String responseJson = ClientMain.readMessage();
                JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                if (response.get("status").getAsString().equals("success")) {
                    JOptionPane.showMessageDialog(this, "Restaurant deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadRestaurants();
                } else {
                    JOptionPane.showMessageDialog(this, response.get("message").getAsString(), "Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting restaurant.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
