package model;

public abstract class Product {
    private int id;
    private String name;
    private ProductCategory category;
    private double purchasePrice;
    private double sellingPrice;
    private int stockQuantity;
    private boolean discountActive;

    public Product() {}

    public Product(String name, ProductCategory category, double purchasePrice,
                   double sellingPrice, int stockQuantity) {
        this.name = name;
        this.category = category;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
        this.discountActive = false;
    }

    // Abstract method for discount calculation
    public abstract double getDiscountRate();

    public double getDiscountedPrice() {
        if (discountActive) {
            return sellingPrice * (1 - getDiscountRate());
        }
        return sellingPrice;
    }

    public double getFinalPrice() {
        return discountActive ? getDiscountedPrice() : sellingPrice;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ProductCategory getCategory() { return category; }
    public void setCategory(ProductCategory category) { this.category = category; }

    public double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }

    public double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public boolean isDiscountActive() { return discountActive; }
    public void setDiscountActive(boolean discountActive) { this.discountActive = discountActive; }
}