package com.example.projet_javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import model.*;
import dao.*;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.Optional;

public class MainController {

    @FXML private TableView<ProductTableItem> productTable;
    @FXML private TableColumn<ProductTableItem, String> colName;
    @FXML private TableColumn<ProductTableItem, String> colCategory;
    @FXML private TableColumn<ProductTableItem, Double> colPurchasePrice;
    @FXML private TableColumn<ProductTableItem, Double> colSellingPrice;
    @FXML private TableColumn<ProductTableItem, Double> colFinalPrice;
    @FXML private TableColumn<ProductTableItem, Integer> colStock;
    @FXML private TableColumn<ProductTableItem, String> colDiscount;
    @FXML private TableColumn<ProductTableItem, Void> colActions;

    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> sortOrder;
    @FXML private Label lblCapital;
    @FXML private Label lblIncome;
    @FXML private Label lblCost;

    private ProductDAO productDAO;
    private AppSettingsDAO appSettingsDAO;
    private ObservableList<ProductTableItem> productList;

    @FXML
    public void initialize() {
        productDAO = new ProductDAO();
        appSettingsDAO = new AppSettingsDAO();
        productList = FXCollections.observableArrayList();

        setupTableColumns();
        setupFiltersAndSorting();
        loadProducts();
        updateFinancialInfo();
    }

    private void setupTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPurchasePrice.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));
        colSellingPrice.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));
        colFinalPrice.setCellValueFactory(new PropertyValueFactory<>("finalPrice"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discountStatus"));

        // Format price columns
        colPurchasePrice.setCellFactory(col -> new TableCell<ProductTableItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : String.format("€%.2f", price));
            }
        });

        colSellingPrice.setCellFactory(col -> new TableCell<ProductTableItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : String.format("€%.2f", price));
            }
        });

        colFinalPrice.setCellFactory(col -> new TableCell<ProductTableItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty ? null : String.format("€%.2f", price));
                if (!empty && getTableRow() != null) {
                    ProductTableItem item = (ProductTableItem) getTableRow().getItem();
                    if (item != null && item.getProduct().isDiscountActive()) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        // Add action buttons
        addActionButtonsToTable();

        productTable.setItems(productList);
    }

    private void addActionButtonsToTable() {
        Callback<TableColumn<ProductTableItem, Void>, TableCell<ProductTableItem, Void>> cellFactory =
                new Callback<>() {
                    @Override
                    public TableCell<ProductTableItem, Void> call(final TableColumn<ProductTableItem, Void> param) {
                        return new TableCell<>() {
                            private final Button btnEdit = new Button("Edit");
                            private final Button btnDelete = new Button("Delete");
                            private final Button btnBuy = new Button("Buy");
                            private final Button btnSell = new Button("Sell");

                            {
                                btnEdit.setOnAction(event -> {
                                    ProductTableItem item = getTableView().getItems().get(getIndex());
                                    editProduct(item.getProduct());
                                });

                                btnDelete.setOnAction(event -> {
                                    ProductTableItem item = getTableView().getItems().get(getIndex());
                                    deleteProduct(item.getProduct());
                                });

                                btnBuy.setOnAction(event -> {
                                    ProductTableItem item = getTableView().getItems().get(getIndex());
                                    purchaseItems(item.getProduct());
                                });

                                btnSell.setOnAction(event -> {
                                    ProductTableItem item = getTableView().getItems().get(getIndex());
                                    sellItems(item.getProduct());
                                });

                                btnEdit.setStyle("-fx-font-size: 10px; -fx-padding: 3px;");
                                btnDelete.setStyle("-fx-font-size: 10px; -fx-padding: 3px;");
                                btnBuy.setStyle("-fx-font-size: 10px; -fx-padding: 3px;");
                                btnSell.setStyle("-fx-font-size: 10px; -fx-padding: 3px;");
                            }

                            @Override
                            public void updateItem(Void item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                } else {
                                    javafx.scene.layout.HBox hbox = new javafx.scene.layout.HBox(5);
                                    hbox.getChildren().addAll(btnEdit, btnDelete, btnBuy, btnSell);
                                    setGraphic(hbox);
                                }
                            }
                        };
                    }
                };

        colActions.setCellFactory(cellFactory);
    }

    private void setupFiltersAndSorting() {
        categoryFilter.getItems().addAll("All", "Clothes", "Shoes", "Accessories");
        categoryFilter.setValue("All");
        categoryFilter.setOnAction(e -> filterProducts());

        sortOrder.getItems().addAll("None", "Price: Low to High", "Price: High to Low");
        sortOrder.setValue("None");
        sortOrder.setOnAction(e -> sortProducts());
    }

    @FXML
    private void handleAddProduct() {
        showProductDialog(null);
    }

    @FXML
    private void handleApplyDiscount() {
        Dialog<ProductCategory> dialog = new Dialog<>();
        dialog.setTitle("Apply Discount");
        dialog.setHeaderText("Select category to apply discount");

        ButtonType applyButtonType = new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButtonType, ButtonType.CANCEL);

        ComboBox<ProductCategory> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(ProductCategory.values());
        categoryCombo.setValue(ProductCategory.CLOTHES);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Category:"), 0, 0);
        grid.add(categoryCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == applyButtonType) {
                return categoryCombo.getValue();
            }
            return null;
        });

        Optional<ProductCategory> result = dialog.showAndWait();
        result.ifPresent(category -> {
            try {
                productDAO.toggleCategoryDiscount(category, true);
                loadProducts();
                showAlert("Success", "Discount applied to " + category.getDisplayName(), Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error", "Failed to apply discount: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleStopDiscount() {
        Dialog<ProductCategory> dialog = new Dialog<>();
        dialog.setTitle("Stop Discount");
        dialog.setHeaderText("Select category to stop discount");

        ButtonType stopButtonType = new ButtonType("Stop", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(stopButtonType, ButtonType.CANCEL);

        ComboBox<ProductCategory> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(ProductCategory.values());
        categoryCombo.setValue(ProductCategory.CLOTHES);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(new Label("Category:"), 0, 0);
        grid.add(categoryCombo, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == stopButtonType) {
                return categoryCombo.getValue();
            }
            return null;
        });

        Optional<ProductCategory> result = dialog.showAndWait();
        result.ifPresent(category -> {
            try {
                productDAO.toggleCategoryDiscount(category, false);
                loadProducts();
                showAlert("Success", "Discount stopped for " + category.getDisplayName(), Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Error", "Failed to stop discount: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void loadProducts() {
        try {
            productList.clear();
            var products = productDAO.getAllProducts();
            for (Product product : products) {
                productList.add(new ProductTableItem(product));
            }
            filterProducts();
        } catch (SQLException e) {
            showAlert("Error", "Failed to load products: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filterProducts() {
        String category = categoryFilter.getValue();
        ObservableList<ProductTableItem> filtered = FXCollections.observableArrayList();

        for (ProductTableItem item : productList) {
            if (category.equals("All") || item.getCategory().equals(category)) {
                filtered.add(item);
            }
        }

        productTable.setItems(filtered);
        sortProducts();
    }

    private void sortProducts() {
        String order = sortOrder.getValue();
        ObservableList<ProductTableItem> items = productTable.getItems();

        if (order.equals("Price: Low to High")) {
            FXCollections.sort(items, Comparator.comparingDouble(ProductTableItem::getFinalPrice));
        } else if (order.equals("Price: High to Low")) {
            FXCollections.sort(items, Comparator.comparingDouble(ProductTableItem::getFinalPrice).reversed());
        }
    }

    private void updateFinancialInfo() {
        try {
            AppSettings settings = appSettingsDAO.getAppSettings();
            if (settings != null) {
                lblCapital.setText(String.format("€%.2f", settings.getCurrentCapital()));
                lblIncome.setText(String.format("€%.2f", settings.getTotalIncome()));
                lblCost.setText(String.format("€%.2f", settings.getTotalCost()));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load financial info: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showProductDialog(Product product) {
        // Implementation continued in next artifact due to size
    }

    private void editProduct(Product product) {
        showProductDialog(product);
    }

    private void deleteProduct(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText("Are you sure you want to delete this product?");
        alert.setContentText(product.getName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                productDAO.deleteProduct(product.getId());
                loadProducts();
                updateFinancialInfo();
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete product: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void purchaseItems(Product product) {
        // Show dialog for purchasing items
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Purchase Items");
        dialog.setHeaderText("Purchase " + product.getName());
        dialog.setContentText("Enter quantity:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantity -> {
            try {
                int qty = Integer.parseInt(quantity);
                if (qty <= 0) {
                    showAlert("Error", "Quantity must be positive", Alert.AlertType.ERROR);
                    return;
                }

                boolean success = appSettingsDAO.processPurchase(product.getId(), qty, product.getPurchasePrice());
                if (success) {
                    loadProducts();
                    updateFinancialInfo();
                    showAlert("Success", "Purchase completed successfully", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Insufficient capital", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid quantity", Alert.AlertType.ERROR);
            } catch (SQLException e) {
                showAlert("Error", "Purchase failed: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void sellItems(Product product) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sell Items");
        dialog.setHeaderText("Sell " + product.getName());
        dialog.setContentText("Enter quantity:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantity -> {
            try {
                int qty = Integer.parseInt(quantity);
                if (qty <= 0) {
                    showAlert("Error", "Quantity must be positive", Alert.AlertType.ERROR);
                    return;
                }

                boolean success = appSettingsDAO.processSale(product.getId(), qty, product.getFinalPrice());
                if (success) {
                    loadProducts();
                    updateFinancialInfo();
                    showAlert("Success", "Sale completed successfully", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Error", "Insufficient stock", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid quantity", Alert.AlertType.ERROR);
            } catch (SQLException e) {
                showAlert("Error", "Sale failed: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Inner class for table items
    public static class ProductTableItem {
        private final Product product;
        private final SimpleStringProperty name;
        private final SimpleStringProperty category;
        private final SimpleDoubleProperty purchasePrice;
        private final SimpleDoubleProperty sellingPrice;
        private final SimpleDoubleProperty finalPrice;
        private final SimpleIntegerProperty stock;
        private final SimpleStringProperty discountStatus;

        public ProductTableItem(Product product) {
            this.product = product;
            this.name = new SimpleStringProperty(product.getName());
            this.category = new SimpleStringProperty(product.getCategory().getDisplayName());
            this.purchasePrice = new SimpleDoubleProperty(product.getPurchasePrice());
            this.sellingPrice = new SimpleDoubleProperty(product.getSellingPrice());
            this.finalPrice = new SimpleDoubleProperty(product.getFinalPrice());
            this.stock = new SimpleIntegerProperty(product.getStockQuantity());
            this.discountStatus = new SimpleStringProperty(
                    product.isDiscountActive() ?
                            String.format("%.0f%% OFF", product.getDiscountRate() * 100) : "No"
            );
        }

        public Product getProduct() { return product; }
        public String getName() { return name.get(); }
        public String getCategory() { return category.get(); }
        public double getPurchasePrice() { return purchasePrice.get(); }
        public double getSellingPrice() { return sellingPrice.get(); }
        public double getFinalPrice() { return finalPrice.get(); }
        public int getStock() { return stock.get(); }
        public String getDiscountStatus() { return discountStatus.get(); }
    }
}
