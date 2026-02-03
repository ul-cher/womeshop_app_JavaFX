package dao;


import model.*;
import db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Create a new product
    public boolean createProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, category, purchase_price, selling_price, " +
                "stock_quantity, discount_active) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getCategory().name());
            stmt.setDouble(3, product.getPurchasePrice());
            stmt.setDouble(4, product.getSellingPrice());
            stmt.setInt(5, product.getStockQuantity());
            stmt.setBoolean(6, product.isDiscountActive());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setId(generatedKeys.getInt(1));
                        return insertCategorySpecificData(product);
                    }
                }
            }
        }
        return false;
    }

    private boolean insertCategorySpecificData(Product product) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        if (product instanceof Clothes) {
            Clothes clothes = (Clothes) product;
            String sql = "INSERT INTO clothes (product_id, size, color, material) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, product.getId());
                stmt.setString(2, clothes.getSize());
                stmt.setString(3, clothes.getColor());
                stmt.setString(4, clothes.getMaterial());
                return stmt.executeUpdate() > 0;
            }
        } else if (product instanceof Shoes) {
            Shoes shoes = (Shoes) product;
            String sql = "INSERT INTO shoes (product_id, shoe_size, color, brand) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, product.getId());
                stmt.setInt(2, shoes.getShoeSize());
                stmt.setString(3, shoes.getColor());
                stmt.setString(4, shoes.getBrand());
                return stmt.executeUpdate() > 0;
            }
        } else if (product instanceof Accessories) {
            Accessories acc = (Accessories) product;
            String sql = "INSERT INTO accessories (product_id, type, color, material) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, product.getId());
                stmt.setString(2, acc.getType());
                stmt.setString(3, acc.getColor());
                stmt.setString(4, acc.getMaterial());
                return stmt.executeUpdate() > 0;
            }
        }
        return false;
    }

    // Get all products
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        Connection conn = DatabaseConnection.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = createProductFromResultSet(rs);
                if (product != null) {
                    products.add(product);
                }
            }
        }
        return products;
    }

    // Get products by category
    public List<Product> getProductsByCategory(ProductCategory category) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category = ?";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = createProductFromResultSet(rs);
                    if (product != null) {
                        products.add(product);
                    }
                }
            }
        }
        return products;
    }

    private Product createProductFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        ProductCategory category = ProductCategory.valueOf(rs.getString("category"));
        double purchasePrice = rs.getDouble("purchase_price");
        double sellingPrice = rs.getDouble("selling_price");
        int stock = rs.getInt("stock_quantity");
        boolean discount = rs.getBoolean("discount_active");

        Product product = null;

        switch (category) {
            case CLOTHES:
                product = loadClothes(id, name, purchasePrice, sellingPrice, stock);
                break;
            case SHOES:
                product = loadShoes(id, name, purchasePrice, sellingPrice, stock);
                break;
            case ACCESSORIES:
                product = loadAccessories(id, name, purchasePrice, sellingPrice, stock);
                break;
        }

        if (product != null) {
            product.setId(id);
            product.setDiscountActive(discount);
        }

        return product;
    }

    private Clothes loadClothes(int id, String name, double purchase, double selling, int stock)
            throws SQLException {
        String sql = "SELECT * FROM clothes WHERE product_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Clothes(name, purchase, selling, stock,
                            rs.getString("size"),
                            rs.getString("color"),
                            rs.getString("material"));
                }
            }
        }
        return null;
    }

    private Shoes loadShoes(int id, String name, double purchase, double selling, int stock)
            throws SQLException {
        String sql = "SELECT * FROM shoes WHERE product_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Shoes(name, purchase, selling, stock,
                            rs.getInt("shoe_size"),
                            rs.getString("color"),
                            rs.getString("brand"));
                }
            }
        }
        return null;
    }

    private Accessories loadAccessories(int id, String name, double purchase, double selling, int stock)
            throws SQLException {
        String sql = "SELECT * FROM accessories WHERE product_id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Accessories(name, purchase, selling, stock,
                            rs.getString("type"),
                            rs.getString("color"),
                            rs.getString("material"));
                }
            }
        }
        return null;
    }

    // Update product
    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET name=?, purchase_price=?, selling_price=?, " +
                "stock_quantity=?, discount_active=? WHERE id=?";

        Connection conn = DatabaseConnection.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPurchasePrice());
            stmt.setDouble(3, product.getSellingPrice());
            stmt.setInt(4, product.getStockQuantity());
            stmt.setBoolean(5, product.isDiscountActive());
            stmt.setInt(6, product.getId());

            boolean result = stmt.executeUpdate() > 0;
            if (result) {
                updateCategorySpecificData(product);
            }
            return result;
        }
    }

    private void updateCategorySpecificData(Product product) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        if (product instanceof Clothes) {
            Clothes clothes = (Clothes) product;
            String sql = "UPDATE clothes SET size=?, color=?, material=? WHERE product_id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, clothes.getSize());
                stmt.setString(2, clothes.getColor());
                stmt.setString(3, clothes.getMaterial());
                stmt.setInt(4, product.getId());
                stmt.executeUpdate();
            }
        } else if (product instanceof Shoes) {
            Shoes shoes = (Shoes) product;
            String sql = "UPDATE shoes SET shoe_size=?, color=?, brand=? WHERE product_id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, shoes.getShoeSize());
                stmt.setString(2, shoes.getColor());
                stmt.setString(3, shoes.getBrand());
                stmt.setInt(4, product.getId());
                stmt.executeUpdate();
            }
        } else if (product instanceof Accessories) {
            Accessories acc = (Accessories) product;
            String sql = "UPDATE accessories SET type=?, color=?, material=? WHERE product_id=?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, acc.getType());
                stmt.setString(2, acc.getColor());
                stmt.setString(3, acc.getMaterial());
                stmt.setInt(4, product.getId());
                stmt.executeUpdate();
            }
        }
    }

    // Delete product
    public boolean deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Update stock quantity
    public boolean updateStock(int productId, int newQuantity) throws SQLException {
        String sql = "UPDATE products SET stock_quantity = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Toggle discount for a product
    public boolean toggleDiscount(int productId, boolean active) throws SQLException {
        String sql = "UPDATE products SET discount_active = ? WHERE id = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, active);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Toggle discount for a category
    public boolean toggleCategoryDiscount(ProductCategory category, boolean active) throws SQLException {
        String sql = "UPDATE products SET discount_active = ? WHERE category = ?";
        Connection conn = DatabaseConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, active);
            stmt.setString(2, category.name());
            return stmt.executeUpdate() > 0;
        }
    }
}
