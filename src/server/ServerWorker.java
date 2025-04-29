package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerWorker implements Runnable {
    private Socket clientSocket;
    private static final Gson gson = new Gson();

    public ServerWorker(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String requestJson;
            while ((requestJson = in.readLine()) != null) {
                System.out.println("Received: " + requestJson);

                JsonObject requestObject = JsonParser.parseString(requestJson).getAsJsonObject();
                String action = requestObject.get("action").getAsString();

                switch (action) {
                    case "login":
                        out.println(handleLogin(requestObject));
                        break;
                    case "register":
                        out.println(handleRegister(requestObject));
                        break;
                    case "place_order":
                        out.println(handlePlaceOrder(requestObject));
                        break;
                    case "accept_delivery":
                        out.println(handleAcceptDelivery(requestObject));
                        break;
                    case "complete_delivery":
                        out.println(handleCompleteDelivery(requestObject));
                        break;
                    case "report":
                        out.println(handleReport(requestObject));
                        break;
                    case "list_products":
                        out.println(handleListProducts());
                        break;
                    case "add_product":
                        out.println(handleAddProduct(requestObject));
                        break;
                    case "edit_product":
                        out.println(handleEditProduct(requestObject));
                        break;
                    case "delete_product":
                        out.println(handleDeleteProduct(requestObject));
                        break;
                    case "add_restaurant":
                        out.println(handleAddRestaurant(requestObject));
                        break;
                    case "edit_restaurant":
                        out.println(handleEditRestaurant(requestObject));
                        break;
                    case "delete_restaurant":
                        out.println(handleDeleteRestaurant(requestObject));
                        break;
                    case "list_orders":
                        out.println(handleListOrders(requestObject));
                        break;
                    case "list_deliveries":
                        out.println(handleListDeliveries());
                        break;
                    default:
                        out.println("{\"status\":\"error\", \"message\":\"Unknown action.\"}");
                }
            }
        } catch (IOException e) {
            System.err.println("Worker exception: " + e.getMessage());
        }
    }


    private String handleLogin(JsonObject request) {
        String username = request.get("username").getAsString();
        String password = request.get("password").getAsString();

        try (Reader reader = new FileReader("src/resources/users.json")) {
            User[] users = gson.fromJson(reader, User[].class);

            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    JsonObject response = new JsonObject();
                    response.addProperty("status", "success");
                    response.addProperty("role", user.getRole());
                    return gson.toJson(response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "{\"status\":\"error\", \"message\":\"Invalid username or password.\"}";
    }

    private String handleRegister(JsonObject request) {
        String username = request.get("username").getAsString();
        String password = request.get("password").getAsString();
        String role = request.get("role").getAsString();

        try {
            File file = new File("src/resources/users.json");
            User[] users = new Gson().fromJson(new FileReader(file), User[].class);

            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    return "{\"status\":\"error\", \"message\":\"Username already exists.\"}";
                }
            }

            User[] updatedUsers = new User[users.length + 1];
            System.arraycopy(users, 0, updatedUsers, 0, users.length);
            updatedUsers[users.length] = new User(username, password, role);

            FileWriter writer = new FileWriter(file);
            new Gson().toJson(updatedUsers, writer);
            writer.flush();
            writer.close();

            return "{\"status\":\"success\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Registration failed.\"}";
        }
    }

    private String handlePlaceOrder(JsonObject request) {
        String username = request.get("username").getAsString();
        List<String> products = gson.fromJson(request.get("products"), List.class);

        try {
            File file = new File("src/resources/orders.json");


            Order[] existingOrders;
            if (file.length() == 0) {
                existingOrders = new Order[0];
            } else {
                existingOrders = gson.fromJson(new FileReader(file), Order[].class);
            }


            String currentDate = java.time.LocalDate.now().toString();


            Order newOrder = new Order(generateOrderId(), username, products, "Pending", currentDate);


            Order[] updatedOrders = new Order[existingOrders.length + 1];
            System.arraycopy(existingOrders, 0, updatedOrders, 0, existingOrders.length);
            updatedOrders[existingOrders.length] = newOrder;


            FileWriter writer = new FileWriter(file);
            gson.toJson(updatedOrders, writer);
            writer.flush();
            writer.close();

            return "{\"status\":\"success\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Could not place order.\"}";
        }
    }

    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }

    private String handleAcceptDelivery(JsonObject request) {
        String deliveryId = request.get("deliveryId").getAsString();
        String deliveryUsername = request.get("deliveryUsername").getAsString();

        try {
            File file = new File("src/resources/orders.json");
            Order[] orders = gson.fromJson(new FileReader(file), Order[].class);

            for (Order order : orders) {
                if (order.getId().equals(deliveryId) && order.getStatus().equals("Pending")) {
                    order.setStatus("Accepted");
                    order.setDeliveryUsername(deliveryUsername);

                    FileWriter writer = new FileWriter(file);
                    gson.toJson(orders, writer);
                    writer.flush();
                    writer.close();

                    return "{\"status\":\"success\"}";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "{\"status\":\"error\", \"message\":\"Cannot accept delivery.\"}";
    }

    private String handleCompleteDelivery(JsonObject request) {
        String deliveryId = request.get("deliveryId").getAsString();

        try {
            File file = new File("src/resources/orders.json");
            Order[] orders = gson.fromJson(new FileReader(file), Order[].class);

            for (Order order : orders) {
                if (order.getId().equals(deliveryId) && order.getStatus().equals("Accepted")) {
                    order.setStatus("Delivered");

                    FileWriter writer = new FileWriter(file);
                    gson.toJson(orders, writer);
                    writer.flush();
                    writer.close();

                    return "{\"status\":\"success\"}";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "{\"status\":\"error\", \"message\":\"Cannot complete delivery.\"}";
    }

    private String handleReport(JsonObject request) {
        String reportType = request.get("reportType").getAsString();
        String dateFrom = request.has("dateFrom") ? request.get("dateFrom").getAsString() : "";
        String dateTo = request.has("dateTo") ? request.get("dateTo").getAsString() : "";
        String deliveryUsername = request.has("deliveryUsername") ? request.get("deliveryUsername").getAsString() : "";

        try {
            File productsFile = new File("src/resources/products.json");
            Product[] products = gson.fromJson(new FileReader(productsFile), Product[].class);

            File ordersFile = new File("src/resources/orders.json");
            Order[] orders = gson.fromJson(new FileReader(ordersFile), Order[].class);

            double total = 0.0;

            for (Order order : orders) {
                if (!order.getStatus().equals("Delivered")) {
                    continue;
                }


                if (!dateFrom.isEmpty() && !dateTo.isEmpty()) {
                    if (order.getDate().compareTo(dateFrom) < 0 || order.getDate().compareTo(dateTo) > 0) {
                        continue;
                    }
                }


                if (!deliveryUsername.isEmpty()) {
                    if (!order.getDeliveryUsername().equals(deliveryUsername)) {
                        continue;
                    }
                }


                for (String productName : order.getProducts()) {
                    for (Product product : products) {
                        if (product.getName().equals(productName)) {
                            total += product.getPrice();
                            break;
                        }
                    }
                }
            }

            JsonObject response = new JsonObject();
            response.addProperty("status", "success");
            response.addProperty("total", total);

            if (reportType.equals("delivery_income")) {

                double bonusThreshold = 500.0;
                double bonusAmount = 100.0;

                if (total >= bonusThreshold) {
                    total += bonusAmount;
                }
            }

            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Cannot generate report.\"}";
        }
    }

    private String handleListProducts() {
        try {
            File productsFile = new File("src/resources/products.json");
            if (!productsFile.exists()) {
                JsonObject error = new JsonObject();
                error.addProperty("status", "error");
                error.addProperty("message", "No products found.");
                return gson.toJson(error);
            }

            Product[] products = gson.fromJson(new FileReader(productsFile), Product[].class);
            List<String> productNames = new ArrayList<>();
            for (Product product : products) {
                productNames.add(product.getName());
            }

            JsonObject response = new JsonObject();
            response.addProperty("status", "success");
            response.add("products", gson.toJsonTree(productNames));

            return gson.toJson(response);
        } catch (Exception e) {
            e.printStackTrace();
            JsonObject error = new JsonObject();
            error.addProperty("status", "error");
            error.addProperty("message", "Failed to load products.");
            return gson.toJson(error);
        }
    }

    private String handleAddProduct(JsonObject request) {
        try {
            String name = request.get("name").getAsString();
            double price = request.get("price").getAsDouble();
            String restaurant = request.get("restaurant").getAsString();

            File productsFile = new File("src/resources/products.json");
            Product[] products;

            if (!productsFile.exists() || productsFile.length() == 0) {
                products = new Product[0];
            } else {
                products = gson.fromJson(new FileReader(productsFile), Product[].class);
            }

            for (Product product : products) {
                if (product.getName().equalsIgnoreCase(name)) {
                    return "{\"status\":\"error\", \"message\":\"Product already exists.\"}";
                }
            }

            Product newProduct = new Product(name, price, restaurant);

            Product[] updatedProducts = new Product[products.length + 1];
            System.arraycopy(products, 0, updatedProducts, 0, products.length);
            updatedProducts[products.length] = newProduct;

            FileWriter writer = new FileWriter(productsFile);
            gson.toJson(updatedProducts, writer);
            writer.flush();
            writer.close();

            return "{\"status\":\"success\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Failed to add product.\"}";
        }
    }

    private String handleEditProduct(JsonObject request) {
        try {
            String originalName = request.get("originalName").getAsString();
            String newName = request.get("newName").getAsString();
            double newPrice = request.get("newPrice").getAsDouble();
            String newRestaurant = request.get("newRestaurant").getAsString();

            File productsFile = new File("src/resources/products.json");
            Product[] products = gson.fromJson(new FileReader(productsFile), Product[].class);

            boolean found = false;
            for (Product product : products) {
                if (product.getName().equalsIgnoreCase(originalName)) {
                    product.setName(newName);
                    product.setPrice(newPrice);
                    product.setRestaurant(newRestaurant);
                    found = true;
                    break;
                }
            }

            if (!found) {
                return "{\"status\":\"error\", \"message\":\"Product not found.\"}";
            }

            FileWriter writer = new FileWriter(productsFile);
            gson.toJson(products, writer);
            writer.flush();
            writer.close();

            return "{\"status\":\"success\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Failed to edit product.\"}";
        }
    }

    private String handleDeleteProduct(JsonObject request) {
        try {
            String name = request.get("name").getAsString();

            File productsFile = new File("src/resources/products.json");
            Product[] products = gson.fromJson(new FileReader(productsFile), Product[].class);

            List<Product> updatedList = new ArrayList<>();
            boolean found = false;

            for (Product product : products) {
                if (!product.getName().equalsIgnoreCase(name)) {
                    updatedList.add(product);
                } else {
                    found = true;
                }
            }

            if (!found) {
                return "{\"status\":\"error\", \"message\":\"Product not found.\"}";
            }

            FileWriter writer = new FileWriter(productsFile);
            gson.toJson(updatedList.toArray(new Product[0]), writer);
            writer.flush();
            writer.close();

            return "{\"status\":\"success\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Failed to delete product.\"}";
        }
    }

    private String handleAddRestaurant(JsonObject request) {
        try {
            String name = request.get("name").getAsString();
            String address = request.get("address").getAsString();
            String phone = request.get("phone").getAsString();

            File restaurantsFile = new File("src/resources/restaurants.json");
            Restaurant[] restaurants;

            if (!restaurantsFile.exists() || restaurantsFile.length() == 0) {
                restaurants = new Restaurant[0];
            } else {
                restaurants = gson.fromJson(new FileReader(restaurantsFile), Restaurant[].class);
            }

            for (Restaurant r : restaurants) {
                if (r.getName().equalsIgnoreCase(name)) {
                    return "{\"status\":\"error\", \"message\":\"Restaurant already exists.\"}";
                }
            }

            Restaurant newRestaurant = new Restaurant(name, address, phone);

            Restaurant[] updatedRestaurants = new Restaurant[restaurants.length + 1];
            System.arraycopy(restaurants, 0, updatedRestaurants, 0, restaurants.length);
            updatedRestaurants[restaurants.length] = newRestaurant;

            FileWriter writer = new FileWriter(restaurantsFile);
            gson.toJson(updatedRestaurants, writer);
            writer.flush();
            writer.close();

            return "{\"status\":\"success\"}";

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Failed to add restaurant.\"}";
        }
    }

    private String handleEditRestaurant(JsonObject request) {
        try {
            String originalName = request.get("originalName").getAsString();
            String newName = request.get("newName").getAsString();
            String newAddress = request.get("newAddress").getAsString();
            String newPhone = request.get("newPhone").getAsString();

            File restaurantsFile = new File("src/resources/restaurants.json");
            Restaurant[] restaurants = gson.fromJson(new FileReader(restaurantsFile), Restaurant[].class);

            boolean found = false;
            for (Restaurant restaurant : restaurants) {
                if (restaurant.getName().equalsIgnoreCase(originalName)) {
                    restaurant.setName(newName);
                    restaurant.setAddress(newAddress);
                    restaurant.setPhone(newPhone);
                    found = true;
                    break;
                }
            }

            if (!found) {
                return "{\"status\":\"error\", \"message\":\"Restaurant not found.\"}";
            }

            FileWriter writer = new FileWriter(restaurantsFile);
            gson.toJson(restaurants, writer);
            writer.flush();
            writer.close();

            return "{\"status\":\"success\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Failed to edit restaurant.\"}";
        }
    }

    private String handleDeleteRestaurant(JsonObject request) {
        try {
            String name = request.get("name").getAsString();

            File restaurantsFile = new File("src/resources/restaurants.json");
            Restaurant[] restaurants = gson.fromJson(new FileReader(restaurantsFile), Restaurant[].class);

            List<Restaurant> updatedList = new ArrayList<>();
            boolean found = false;

            for (Restaurant restaurant : restaurants) {
                if (!restaurant.getName().equalsIgnoreCase(name)) {
                    updatedList.add(restaurant);
                } else {
                    found = true;
                }
            }

            if (!found) {
                return "{\"status\":\"error\", \"message\":\"Restaurant not found.\"}";
            }

            FileWriter writer = new FileWriter(restaurantsFile);
            gson.toJson(updatedList.toArray(new Restaurant[0]), writer);
            writer.flush();
            writer.close();

            return "{\"status\":\"success\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Failed to delete restaurant.\"}";
        }
    }

    private String handleListOrders(JsonObject request) {
        String username = request.get("username").getAsString();
        try {
            File ordersFile = new File("src/resources/orders.json");
            if (!ordersFile.exists() || ordersFile.length() == 0) {
                JsonObject response = new JsonObject();
                response.addProperty("status", "success");
                response.add("orders", gson.toJsonTree(new String[0]));
                return gson.toJson(response);
            }

            Order[] orders = gson.fromJson(new FileReader(ordersFile), Order[].class);
            List<String> userOrders = new ArrayList<>();

            for (Order order : orders) {
                if (order.getUsername().equals(username)) {
                    userOrders.add(order.getId() + " - " + order.getStatus() + " - " + order.getProducts());
                }
            }

            JsonObject response = new JsonObject();
            response.addProperty("status", "success");
            response.add("orders", gson.toJsonTree(userOrders));
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Failed to list orders.\"}";
        }
    }

    private String handleListDeliveries() {
        try {
            File ordersFile = new File("src/resources/orders.json");
            if (!ordersFile.exists() || ordersFile.length() == 0) {
                JsonObject response = new JsonObject();
                response.addProperty("status", "success");
                response.add("deliveries", gson.toJsonTree(new String[0]));
                return gson.toJson(response);
            }

            Order[] orders = gson.fromJson(new FileReader(ordersFile), Order[].class);
            List<String> pendingDeliveries = new ArrayList<>();

            for (Order order : orders) {
                if (order.getStatus().equals("Pending")) {
                    pendingDeliveries.add(order.getId() + " - " + order.getProducts());
                }
            }

            JsonObject response = new JsonObject();
            response.addProperty("status", "success");
            response.add("deliveries", gson.toJsonTree(pendingDeliveries));
            return gson.toJson(response);

        } catch (Exception e) {
            e.printStackTrace();
            return "{\"status\":\"error\", \"message\":\"Failed to list deliveries.\"}";
        }
    }

}



