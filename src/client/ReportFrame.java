package client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReportFrame extends JFrame {
    private JButton companyTurnoverButton;
    private JButton deliveryIncomeButton;
    private JButton backButton;

    public ReportFrame(String role) {
        setTitle("Reports - " + role);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        companyTurnoverButton = new JButton("View Company Turnover");
        deliveryIncomeButton = new JButton("View Delivery Income");
        backButton = new JButton("Back");

        add(companyTurnoverButton);
        add(deliveryIncomeButton);
        add(backButton);

        companyTurnoverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String dateFrom = JOptionPane.showInputDialog("Enter start date (DD/MM/YYYY):");
                    String dateTo = JOptionPane.showInputDialog("Enter end date (DD/MM/YYYY):");

                    if (!isValidDateFormat(dateFrom)) {
                        JOptionPane.showMessageDialog(null, "Invalid start date format! Use DD/MM/YYYY.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!isValidDateFormat(dateTo)) {
                        JOptionPane.showMessageDialog(null, "Invalid end date format! Use DD/MM/YYYY.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    JsonObject reportRequest = new JsonObject();
                    reportRequest.addProperty("action", "report");
                    reportRequest.addProperty("reportType", "company_turnover");
                    reportRequest.addProperty("dateFrom", dateFrom);
                    reportRequest.addProperty("dateTo", dateTo);

                    ClientMain.sendMessage(reportRequest.toString());
                    String responseJson = ClientMain.readMessage();

                    JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                    if (response.get("status").getAsString().equals("success")) {
                        double total = response.get("total").getAsDouble();
                        JOptionPane.showMessageDialog(null, "Company Turnover: " + total + " BGN", "Report", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Report Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error during report generation.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deliveryIncomeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String dateFrom = JOptionPane.showInputDialog("Enter start date (DD/MM/YYYY):");
                    String dateTo = JOptionPane.showInputDialog("Enter end date (DD/MM/YYYY):");
                    String deliveryUsername = JOptionPane.showInputDialog("Enter Delivery Username:");

                    if (!isValidDateFormat(dateFrom)) {
                        JOptionPane.showMessageDialog(null, "Invalid start date format! Use DD/MM/YYYY.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!isValidDateFormat(dateTo)) {
                        JOptionPane.showMessageDialog(null, "Invalid end date format! Use DD/MM/YYYY.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    JsonObject reportRequest = new JsonObject();
                    reportRequest.addProperty("action", "report");
                    reportRequest.addProperty("reportType", "delivery_income");
                    reportRequest.addProperty("dateFrom", dateFrom);
                    reportRequest.addProperty("dateTo", dateTo);
                    reportRequest.addProperty("deliveryUsername", deliveryUsername);

                    ClientMain.sendMessage(reportRequest.toString());
                    String responseJson = ClientMain.readMessage();

                    JsonObject response = JsonParser.parseString(responseJson).getAsJsonObject();

                    if (response.get("status").getAsString().equals("success")) {
                        double total = response.get("total").getAsDouble();
                        JOptionPane.showMessageDialog(null, "Delivery Income: " + total + " BGN", "Report", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, response.get("message").getAsString(), "Report Failed", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error during report generation.", "Error", JOptionPane.ERROR_MESSAGE);
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

        setVisible(true);
    }

    private boolean isValidDateFormat(String date) {
        return date != null && date.matches("\\d{2}/\\d{2}/\\d{4}");
    }
}
