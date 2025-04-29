
# BringIt - Food Delivery Application

## Overview
BringIt is a Java-based client-server food delivery system, developed using **Java Swing** for the UI and **socket programming** for client-server communication.

The system supports three user roles:
- **Client**: Can register, log in, browse products, place orders, and view their orders.
- **Employee**: Can add/edit/delete restaurants and products, and generate reports.
- **Delivery**: Can accept deliveries and mark them as completed.

## Features
- Login and Registration with validation (username non-empty, password minimum 8 characters).
- Role-based access and different UI screens.
- Adding, editing, deleting restaurants and products.
- Placing and viewing orders for clients.
- Accepting and completing deliveries for delivery guys.
- Viewing company turnover and delivery income reports (with bonus calculations).
- Filtering products by category.
- Refresh buttons for real-time updates.
- Full client-server communication over sockets using JSON.

## How to Run
1. Open the project in IntelliJ IDEA.
2. Start the server:
   - Run `ServerMain.java` from the `server` package.
3. Start the client:
   - Run `ClientMain.java` from the `client` package.
4. Use the UI to register/login based on your role.

## Requirements
- Java 17 or later (Language Level 17+)
- No external libraries required except GSON (manually added).

## Important Notes
- JSON files are stored in `src/resources/` folder.
- Server must be running before starting the client.
- All data is persisted inside `.json` files.

---

**BringIt** 🚚🍔 – Fast and easy food ordering experience!
