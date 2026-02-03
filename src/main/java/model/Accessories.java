package model;

public class Accessories extends Product {
    private String type;
    private String color;
    private String material;

    public Accessories() {
        super();
    }

    public Accessories(String name, double purchasePrice, double sellingPrice,
                       int stockQuantity, String type, String color, String material) {
        super(name, ProductCategory.ACCESSORIES, purchasePrice, sellingPrice, stockQuantity);
        this.type = type;
        this.color = color;
        this.material = material;
    }

    @Override
    public double getDiscountRate() {
        return 0.50;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
}
