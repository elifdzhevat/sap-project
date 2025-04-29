package client;

import client.requests.RegisterRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;

    public RegisterFrame() {
        setTitle("Register");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        roleComboBox = new JComboBox<>(new String[]{"client", "employee", "delivery"});
        registerButton = new JButton("Register");

        add(new JLabel("Username:"));
        add(usernameField);

        add(new JLabel("Password:"));
        add(passwordField);

        add(new JLabel("Role:"));
        add(roleComboBox);

        add(new JLabel(""));
        add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String role = (String) roleComboBox.getSelectedItem();

                if (username.isEmpty() && password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Username and Password cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Username cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Password cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() < 8) {
                    JOptionPane.showMessageDialog(null, "Password must be at least 8 characters long!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

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
