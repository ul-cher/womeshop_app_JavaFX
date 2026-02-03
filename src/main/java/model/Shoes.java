package model;

public class Shoes extends Product {
    private int shoeSize;
    private String color;
    private String brand;

    public Shoes() {
        super();
    }

    public Shoes(String name, double purchasePrice, double sellingPrice,
                 int stockQuantity, int shoeSize, String color, String brand) {
        super(name, ProductCategory.SHOES, purchasePrice, sellingPrice, stockQuantity);
        this.shoeSize = shoeSize;
        this.color = color;
        this.brand = brand;
    }

    @Override
    public double getDiscountRate() {
        return 0.20; // 20% discount for shoes
    }

    public int getShoeSize() { return shoeSize; }
    public void setShoeSize(int shoeSize) { this.shoeSize = shoeSize; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
}