package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuFrame extends JFrame {
    private JButton productsButton;
    private JButton ordersButton;
    private JButton deliveriesButton;
    private JButton reportsButton;
    private JButton logoutButton;
    private String role;

    public MainMenuFrame(String role) {
        this.role = role;

        setTitle("Main Menu - " + role);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 1, 10, 10));

        productsButton = new JButton("Manage/View Products");
        ordersButton = new JButton("Place/View Orders");
        deliveriesButton = new JButton("Manage Deliveries");
        reportsButton = new JButton("View Reports");
        logoutButton = new JButton("Logout");

        if (role.equalsIgnoreCase("client")) {
            add(productsButton);
            add(ordersButton);
            add(logoutButton);
        } else if (role.equalsIgnoreCase("employee")) {
            add(productsButton);
            add(reportsButton);
            add(logoutButton);
        } else if (role.equalsIgnoreCase("delivery")) {
            add(deliveriesButton);
            add(logoutButton);
        }

        productsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                if (role.equalsIgnoreCase("employee")) {
                    new ManageEntitiesFrame(role);
                } else if (role.equalsIgnoreCase("client")) {
                    new OrderManagementFrame(role);
                }
            }
        });


        ordersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new OrderManagementFrame(role);
            }
        });

        deliveriesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new DeliveryManagementFrame(role);
            }
        });

        reportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ReportFrame(role);
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame();
            }
        });

        setVisible(true);
    }
}
