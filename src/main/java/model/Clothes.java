package model;

public class Clothes extends Product {
    private String size;
    private String color;
    private String material;

    public Clothes() {
        super();
    }

    public Clothes(String name, double purchasePrice, double sellingPrice,
                   int stockQuantity, String size, String color, String material) {
        super(name, ProductCategory.CLOTHES, purchasePrice, sellingPrice, stockQuantity);
        this.size = size;
        this.color = color;
        this.material = material;
    }

    @Override
    public double getDiscountRate() {
        return 0.30;
    }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
}