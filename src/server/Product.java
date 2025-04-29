package server;

public class Product {
    private String name;
    private String category;
    private double price;

    public Product(String name, double price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public void setName(String newName) {
        this.name = newName;
    }

    public void setPrice(double newPrice) {
        this.price = newPrice;
    }

    public void setCategory(String newCategory) {
        this.category = newCategory;
    }

    public void setRestaurant(String newRestaurant) {
    }
}
