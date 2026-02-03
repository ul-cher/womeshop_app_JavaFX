package model;

public enum ProductCategory {
    CLOTHES("Clothes"),
    SHOES("Shoes"),
    ACCESSORIES("Accessories");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
