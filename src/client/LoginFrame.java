package client;

import client.requests.LoginRequest;
import client.requests.RegisterRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JComboBox<String> roleComboBox;


    public LoginFrame() {
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        String[] roles = {"client", "employee", "delivery"};
        roleComboBox = new JComboBox<>(roles);


        add(new JLabel("Username:"));
        add(usernameField);

        add(new JLabel("Password:"));
        add(passwordField);

        add(loginButton);
        add(registerButton);

        add(new JLabel("Role:"));
        add(roleComboBox);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Username cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Password cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    LoginRequest loginRequest = new LoginRequest(username, password);
                    String requestJson = new Gson().toJson(loginRequest);

                    ClientMain.sendMessage(requestJson);
                    String responseJson = ClientMain.readMessage();

                    JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                    if (response.get("status").getAsString().equals("success")) {
                        String role = response.get("role").getAsString();


                        ClientMain.setCurrentUsername(username);

                        dispose();
                        new MainMenuFrame(role);
                    } else {
                        JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Login Failed", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error during login.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });



        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String role = (String) roleComboBox.getSelectedItem();

                try {
                    RegisterRequest registerRequest = new RegisterRequest(username, password, role);
                    String requestJson = new Gson().toJson(registerRequest);

                    ClientMain.sendMessage(requestJson);
                    String responseJson = ClientMain.readMessage();

                    JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                    if (response.get("status").getAsString().equals("success")) {
                        JOptionPane.showMessageDialog(null, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new LoginFrame();
                    } else {
                        JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Registration Failed", JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error during registration.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        setVisible(true);
    }
}
