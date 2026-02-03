package com.example.projet_javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import javafx.scene.layout.VBox;
import model.*;
import dao.*;

import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;


public class MainController {

    @FXML private TableView<ProductTableItem> productTable;
    @FXML private TableColumn<ProductTableItem, String> colName;
    @FXML private TableColumn<ProductTableItem, String> colCategory;
    @FXML private TableColumn<ProductTableItem, String> colDetails;
    @FXML private TableColumn<ProductTableItem, Double> colPurchasePrice;
    @FXML private TableColumn<ProductTableItem, Double> colSellingPrice;
    @FXML private TableColumn<ProductTableItem, Double> colFinalPrice;
    @FXML private TableColumn<ProductTableItem, Integer> colStock;
    @FXML private TableColumn<ProductTableItem, String> colDiscount;
    @FXML private TableColumn<ProductTableItem, Void> colActions;

    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> sortOrder;
    @FXML private TextField searchField;
    @FXML private Label lblCapital;
    @FXML private Label lblIncome;
    @FXML private Label lblCost;
    @FXML private Label lblProfit;
    @FXML private Label lblTotalProducts;
    @FXML private Label lblLastUpdate;

    private ProductDAO productDAO;
    private AppSettingsDAO appSettingsDAO;
    private ObservableList<ProductTableItem> productList;
    private ObservableList<ProductTableItem> filteredList;

    @FXML
    public void initialize() {
        productDAO = new ProductDAO();
        appSettingsDAO = new AppSettingsDAO();
        productList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();

        setupTableColumns();
        setupFiltersAndSorting();
        loadProducts();
        updateFinancialInfo();
        updateLastUpdateTime();

        // Setup search functionality
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterProducts();
            });
        }
    }

    private void setupTableColumns() {
        // Name column with custom cell factory for styling
        colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        colName.setCellFactory(column -> new TableCell<ProductTableItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                }
            }
        });

        colCategory.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());
        colCategory.setCellFactory(column -> new TableCell<ProductTableItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    String style = "";
                    switch (item) {
                        case "Clothes":
                            style = "-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; -fx-padding: 5; -fx-background-radius: 5; -fx-font-weight: bold;";
                            break;
                        case "Shoes":
                            style = "-fx-background-color: #f3e5f5; -fx-text-fill: #7b1fa2; -fx-padding: 5; -fx-background-radius: 5; -fx-font-weight: bold;";
                            break;
                        case "Accessories":
                            style = "-fx-background-color: #fff3e0; -fx-text-fill: #e65100; -fx-padding: 5; -fx-background-radius: 5; -fx-font-weight: bold;";
                            break;
                    }
                    setStyle(style);
                }
            }
        });

        colDetails.setCellValueFactory(cellData -> cellData.getValue().detailsProperty());
        colDetails.setCellFactory(column -> new TableCell<ProductTableItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
                }
            }
        });

        colPurchasePrice.setCellValueFactory(cellData -> cellData.getValue().purchasePriceProperty().asObject());
        colSellingPrice.setCellValueFactory(cellData -> cellData.getValue().sellingPriceProperty().asObject());
        colFinalPrice.setCellValueFactory(cellData -> cellData.getValue().finalPriceProperty().asObject());
        colStock.setCellValueFactory(cellData -> cellData.getValue().stockProperty().asObject());

        // Format price columns with currency
        colPurchasePrice.setCellFactory(col -> new TableCell<ProductTableItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("€%.2f", price));
                    setStyle("-fx-font-size: 12px;");
                }
            }
        });

        colSellingPrice.setCellFactory(col -> new TableCell<ProductTableItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("€%.2f", price));
                    setStyle("-fx-font-size: 12px;");
                }
            }
        });

        colFinalPrice.setCellFactory(col -> new TableCell<ProductTableItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("€%.2f", price));
                    if (getTableRow() != null) {
                        ProductTableItem item = (ProductTableItem) getTableRow().getItem();
                        if (item != null && item.getProduct().isDiscountActive()) {
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 13px;");
                        } else {
                            setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                        }
                    }
                }
            }
        });

        // Stock column with color coding
        colStock.setCellFactory(col -> new TableCell<ProductTableItem, Integer>() {
            @Override
            protected void updateItem(Integer stock, boolean empty) {
                super.updateItem(stock, empty);
                if (empty || stock == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(stock));
                    if (stock == 0) {
                        setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                    } else if (stock < 10) {
                        setStyle("-fx-background-color: #fff3e0; -fx-text-fill: #e65100; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: #e8f5e9; -fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                    }
                }
            }
        });

        colDiscount.setCellValueFactory(cellData -> cellData.getValue().discountStatusProperty());
        colDiscount.setCellFactory(column -> new TableCell<ProductTableItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("%")) {
                        setStyle("-fx-background-color: #fff59d; -fx-text-fill: #f57f17; -fx-padding: 5; -fx-background-radius: 5; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #999;");
                    }
                }
            }
        });

        // Add modern action buttons
        addActionButtonsToTable();

        productTable.setItems(filteredList);
        productTable.setPlaceholder(new Label("🛍️ No products found. Click 'Add Product' to get started!"));
    }

    private void addActionButtonsToTable() {
        colActions.setCellFactory(param -> new TableCell<ProductTableItem, Void>() {
            private final Button btnEdit = createStyledButton("✏️ Edit", "#3498db");
            private final Button btnDelete = createStyledButton("🗑️ Delete", "#e74c3c");
            private final Button btnBuy = createStyledButton("🛒 Buy", "#27ae60");
            private final Button btnSell = createStyledButton("💰 Sell", "#f39c12");

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
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5);
                    hbox.getChildren().addAll(btnEdit, btnDelete, btnBuy, btnSell);
                    setGraphic(hbox);
                }
            }
        });
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 10px; " +
                        "-fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold;",
                color
        ));
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-opacity: 0.8;"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle().replace("-fx-opacity: 0.8;", "")));
        return button;
    }

    private void setupFiltersAndSorting() {
        categoryFilter.getItems().addAll("All Categories", "Clothes", "Shoes", "Accessories");
        categoryFilter.setValue("All Categories");
        categoryFilter.setOnAction(e -> filterProducts());

        sortOrder.getItems().addAll("Default Order", "Price: Low to High", "Price: High to Low",
                "Stock: Low to High", "Stock: High to Low", "Name: A-Z", "Name: Z-A");
        sortOrder.setValue("Default Order");
        sortOrder.setOnAction(e -> sortProducts());
    }

    @FXML
    private void handleAddProduct() {
        ProductDialogController dialogController = new ProductDialogController();
        dialogController.showProductDialog(null, () -> {
            loadProducts();
            updateFinancialInfo();
            showSuccessAnimation("Product added successfully! 🎉");
        });
    }

    @FXML
    private void handleApplyDiscount() {
        Dialog<ProductCategory> dialog = new Dialog<>();
        dialog.setTitle("Apply Discount");
        dialog.setHeaderText("🎉 Select category to apply discount");

        ButtonType applyButtonType = new ButtonType("Apply Discount", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(applyButtonType, ButtonType.CANCEL);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label infoLabel = new Label("Fixed discount rates:");
        infoLabel.setStyle("-fx-font-weight: bold;");

        Label clothesInfo = new Label("👗 Clothes: 30% OFF");
        clothesInfo.setStyle("-fx-text-fill: #1976d2;");

        Label shoesInfo = new Label("👠 Shoes: 20% OFF");
        shoesInfo.setStyle("-fx-text-fill: #7b1fa2;");

        Label accessoriesInfo = new Label("👜 Accessories: 50% OFF");
        accessoriesInfo.setStyle("-fx-text-fill: #e65100;");

        ComboBox<ProductCategory> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(ProductCategory.values());
        categoryCombo.setValue(ProductCategory.CLOTHES);
        categoryCombo.setStyle("-fx-pref-width: 200;");

        content.getChildren().addAll(infoLabel, clothesInfo, shoesInfo, accessoriesInfo,
                new Separator(), new Label("Select Category:"), categoryCombo);

        dialog.getDialogPane().setContent(content);

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
                showSuccessAnimation("Discount applied to " + category.getDisplayName() + "! 🎉");
            } catch (SQLException e) {
                showAlert("Error", "Failed to apply discount: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleStopDiscount() {
        Dialog<ProductCategory> dialog = new Dialog<>();
        dialog.setTitle("Stop Discount");
        dialog.setHeaderText("⛔ Select category to stop discount");

        ButtonType stopButtonType = new ButtonType("Stop Discount", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(stopButtonType, ButtonType.CANCEL);

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        ComboBox<ProductCategory> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll(ProductCategory.values());
        categoryCombo.setValue(ProductCategory.CLOTHES);
        categoryCombo.setStyle("-fx-pref-width: 200;");

        content.getChildren().addAll(new Label("Select Category:"), categoryCombo);

        dialog.getDialogPane().setContent(content);

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
                showSuccessAnimation("Discount stopped for " + category.getDisplayName());
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
            updateLastUpdateTime();
        } catch (SQLException e) {
            showAlert("Error", "Failed to load products: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filterProducts() {
        String category = categoryFilter.getValue();
        String searchText = searchField != null ? searchField.getText().toLowerCase() : "";

        filteredList.clear();

        for (ProductTableItem item : productList) {
            boolean categoryMatch = category.equals("All Categories") || item.getCategory().equals(category);
            boolean searchMatch = searchText.isEmpty() ||
                    item.getName().toLowerCase().contains(searchText) ||
                    item.getDetails().toLowerCase().contains(searchText);

            if (categoryMatch && searchMatch) {
                filteredList.add(item);
            }
        }

        lblTotalProducts.setText(String.format("Total: %d products", filteredList.size()));
        sortProducts();
    }

    private void sortProducts() {
        String order = sortOrder.getValue();

        switch (order) {
            case "Price: Low to High":
                FXCollections.sort(filteredList, Comparator.comparingDouble(ProductTableItem::getFinalPrice));
                break;
            case "Price: High to Low":
                FXCollections.sort(filteredList, Comparator.comparingDouble(ProductTableItem::getFinalPrice).reversed());
                break;
            case "Stock: Low to High":
                FXCollections.sort(filteredList, Comparator.comparingInt(ProductTableItem::getStock));
                break;
            case "Stock: High to Low":
                FXCollections.sort(filteredList, Comparator.comparingInt(ProductTableItem::getStock).reversed());
                break;
            case "Name: A-Z":
                FXCollections.sort(filteredList, Comparator.comparing(ProductTableItem::getName));
                break;
            case "Name: Z-A":
                FXCollections.sort(filteredList, Comparator.comparing(ProductTableItem::getName).reversed());
                break;
        }
    }

    private void updateFinancialInfo() {
        try {
            AppSettings settings = appSettingsDAO.getAppSettings();
            if (settings != null) {
                lblCapital.setText(String.format("€%.2f", settings.getCurrentCapital()));
                lblIncome.setText(String.format("€%.2f", settings.getTotalIncome()));
                lblCost.setText(String.format("€%.2f", settings.getTotalCost()));

                double profit = settings.getTotalIncome() - settings.getTotalCost();
                lblProfit.setText(String.format("€%.2f", profit));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load financial info: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateLastUpdateTime() {
        if (lblLastUpdate != null) {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            lblLastUpdate.setText("Last updated: " + time);
        }
    }

    private void editProduct(Product product) {
        ProductDialogController dialogController = new ProductDialogController();
        dialogController.showProductDialog(product, () -> {
            loadProducts();
            updateFinancialInfo();
            showSuccessAnimation("Product updated successfully! ✅");
        });
    }

    private void deleteProduct(Product product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText("🗑️ Are you sure you want to delete this product?");
        alert.setContentText("Product: " + product.getName() + "\n\nThis action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                productDAO.deleteProduct(product.getId());
                loadProducts();
                updateFinancialInfo();
                showSuccessAnimation("Product deleted successfully");
            } catch (SQLException e) {
                showAlert("Error", "Failed to delete product: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void purchaseItems(Product product) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Purchase Items");
        dialog.setHeaderText("🛒 Purchase " + product.getName());
        dialog.setContentText(String.format("Unit Price: €%.2f\nEnter quantity:", product.getPurchasePrice()));

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantity -> {
            try {
                int qty = Integer.parseInt(quantity);
                if (qty <= 0) {
                    showAlert("Error", "Quantity must be positive", Alert.AlertType.ERROR);
                    return;
                }

                double totalCost = qty * product.getPurchasePrice();

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Purchase");
                confirmAlert.setHeaderText("Purchase Summary");
                confirmAlert.setContentText(String.format(
                        "Product: %s\nQuantity: %d\nUnit Price: €%.2f\nTotal Cost: €%.2f\n\nProceed with purchase?",
                        product.getName(), qty, product.getPurchasePrice(), totalCost
                ));

                Optional<ButtonType> confirm = confirmAlert.showAndWait();
                if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                    boolean success = appSettingsDAO.processPurchase(product.getId(), qty, product.getPurchasePrice());
                    if (success) {
                        loadProducts();
                        updateFinancialInfo();
                        showSuccessAnimation(String.format("Purchase completed! Added %d items 🎉", qty));
                    } else {
                        showAlert("Error", "Insufficient capital for this purchase", Alert.AlertType.ERROR);
                    }
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid number", Alert.AlertType.ERROR);
            } catch (SQLException e) {
                showAlert("Error", "Purchase failed: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void sellItems(Product product) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Sell Items");
        dialog.setHeaderText("💰 Sell " + product.getName());
        dialog.setContentText(String.format("Final Price: €%.2f\nAvailable Stock: %d\nEnter quantity:",
                product.getFinalPrice(), product.getStockQuantity()));

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantity -> {
            try {
                int qty = Integer.parseInt(quantity);
                if (qty <= 0) {
                    showAlert("Error", "Quantity must be positive", Alert.AlertType.ERROR);
                    return;
                }

                if (qty > product.getStockQuantity()) {
                    showAlert("Error", String.format("Insufficient stock! Only %d items available",
                            product.getStockQuantity()), Alert.AlertType.ERROR);
                    return;
                }

                double totalIncome = qty * product.getFinalPrice();

                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Sale");
                confirmAlert.setHeaderText("Sale Summary");
                String discountInfo = product.isDiscountActive() ?
                        String.format("\n(%.0f%% discount applied)", product.getDiscountRate() * 100) : "";
                confirmAlert.setContentText(String.format(
                        "Product: %s\nQuantity: %d\nUnit Price: €%.2f%s\nTotal Income: €%.2f\n\nProceed with sale?",
                        product.getName(), qty, product.getFinalPrice(), discountInfo, totalIncome
                ));

                Optional<ButtonType> confirm = confirmAlert.showAndWait();
                if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                    boolean success = appSettingsDAO.processSale(product.getId(), qty, product.getFinalPrice());
                    if (success) {
                        loadProducts();
                        updateFinancialInfo();
                        showSuccessAnimation(String.format("Sale completed! Sold %d items 💰", qty));
                    } else {
                        showAlert("Error", "Sale failed", Alert.AlertType.ERROR);
                    }
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid number", Alert.AlertType.ERROR);
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

    private void showSuccessAnimation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), alert.getDialogPane());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        alert.showAndWait();
    }

    // Inner class for table items
    public static class ProductTableItem {
        private final Product product;
        private final SimpleStringProperty name;
        private final SimpleStringProperty category;
        private final SimpleStringProperty details;
        private final SimpleDoubleProperty purchasePrice;
        private final SimpleDoubleProperty sellingPrice;
        private final SimpleDoubleProperty finalPrice;
        private final SimpleIntegerProperty stock;
        private final SimpleStringProperty discountStatus;

        public ProductTableItem(Product product) {
            this.product = product;
            this.name = new SimpleStringProperty(product.getName());
            this.category = new SimpleStringProperty(product.getCategory().getDisplayName());
            this.details = new SimpleStringProperty(getProductDetails(product));
            this.purchasePrice = new SimpleDoubleProperty(product.getPurchasePrice());
            this.sellingPrice = new SimpleDoubleProperty(product.getSellingPrice());
            this.finalPrice = new SimpleDoubleProperty(product.getFinalPrice());
            this.stock = new SimpleIntegerProperty(product.getStockQuantity());
            this.discountStatus = new SimpleStringProperty(
                    product.isDiscountActive() ?
                            String.format("%.0f%% OFF", product.getDiscountRate() * 100) : "No discount"
            );
        }

        private String getProductDetails(Product product) {
            if (product instanceof Clothes) {
                Clothes c = (Clothes) product;
                return String.format("Size: %s, %s", c.getSize(), c.getColor());
            } else if (product instanceof Shoes) {
                Shoes s = (Shoes) product;
                return String.format("Size: %d, %s", s.getShoeSize(), s.getColor());
            } else if (product instanceof Accessories) {
                Accessories a = (Accessories) product;
                return String.format("%s, %s", a.getType(), a.getColor());
            }
            return "";
        }

        public Product getProduct() { return product; }
        public String getName() { return name.get(); }
        public String getCategory() { return category.get(); }
        public String getDetails() { return details.get(); }
        public double getPurchasePrice() { return purchasePrice.get(); }
        public double getSellingPrice() { return sellingPrice.get(); }
        public double getFinalPrice() { return finalPrice.get(); }
        public int getStock() { return stock.get(); }
        public String getDiscountStatus() { return discountStatus.get(); }

        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty categoryProperty() { return category; }
        public SimpleStringProperty detailsProperty() { return details; }
        public SimpleDoubleProperty purchasePriceProperty() { return purchasePrice; }
        public SimpleDoubleProperty sellingPriceProperty() { return sellingPrice; }
        public SimpleDoubleProperty finalPriceProperty() { return finalPrice; }
        public SimpleIntegerProperty stockProperty() { return stock; }
        public SimpleStringProperty discountStatusProperty() { return discountStatus; }
    }
}