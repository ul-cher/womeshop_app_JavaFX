package com.example.projet_javafx;

import model.*;
import dao.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;

import java.sql.SQLException;
import java.util.Optional;

public class ProductDialogController {

    private ProductDAO productDAO;

    public ProductDialogController() {
        this.productDAO = new ProductDAO();
    }

    public void showProductDialog(Product existingProduct, Runnable onSuccess) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle(existingProduct == null ? "✨ Add New Product" : "✏️ Edit Product");
        dialog.setHeaderText(existingProduct == null ?
                "Add a new item to your boutique collection" :
                "Update product information");

        ButtonType saveButtonType = new ButtonType("💾 Save Product", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setStyle("-fx-background-color: #FDFBF7; -fx-font-family: 'Georgia';");
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(25));
        mainContainer.setStyle("-fx-background-color: #FDFBF7;");
        VBox basicSection = createSection("📋 Basic Information");
        GridPane basicGrid = new GridPane();
        basicGrid.setHgap(15);
        basicGrid.setVgap(12);
        basicGrid.setPadding(new Insets(15));

        TextField txtName = createStyledTextField("Product Name");

        ComboBox<ProductCategory> cmbCategory = new ComboBox<>();
        cmbCategory.getItems().addAll(ProductCategory.values());
        cmbCategory.setValue(ProductCategory.CLOTHES);
        cmbCategory.setStyle("-fx-pref-width: 250;");
        cmbCategory.setPromptText("Select Category");

        TextField txtPurchasePrice = createStyledTextField("0.00");
        TextField txtSellingPrice = createStyledTextField("0.00");

        basicGrid.add(createLabel("Product Name:"), 0, 0);
        basicGrid.add(txtName, 1, 0);
        basicGrid.add(createLabel("Category:"), 0, 1);
        basicGrid.add(cmbCategory, 1, 1);
        basicGrid.add(createLabel("Purchase Price (€):"), 0, 2);
        basicGrid.add(txtPurchasePrice, 1, 2);
        basicGrid.add(createLabel("Selling Price (€):"), 0, 3);
        basicGrid.add(txtSellingPrice, 1, 3);

        basicSection.getChildren().add(basicGrid);

        // Category-specific fields section
        VBox categorySection = createSection("🏷️ Category-Specific Details");
        VBox categoryFields = new VBox(12);
        categoryFields.setPadding(new Insets(15));

        // Clothes fields
        TextField txtSize = createStyledTextField("e.g., S, M, L, XL");
        TextField txtClothesColor = createStyledTextField("e.g., Red, Blue, Black");
        TextField txtMaterial = createStyledTextField("e.g., Cotton, Silk, Polyester");

        // Shoes fields
        TextField txtShoeSize = createStyledTextField("e.g., 36, 37, 38, 39");
        TextField txtShoesColor = createStyledTextField("e.g., Red, Blue, Black");
        TextField txtBrand = createStyledTextField("e.g., Nike, Adidas, Zara");

        // Accessories fields
        TextField txtType = createStyledTextField("e.g., Handbag, Jewelry, Scarf");
        TextField txtAccessoriesColor = createStyledTextField("e.g., Red, Blue, Black");
        TextField txtAccessoriesMaterial = createStyledTextField("e.g., Leather, Metal, Fabric");

        // Update category-specific fields based on selection
        cmbCategory.setOnAction(e -> {
            categoryFields.getChildren().clear();
            ProductCategory category = cmbCategory.getValue();

            GridPane catGrid = new GridPane();
            catGrid.setHgap(15);
            catGrid.setVgap(12);

            switch (category) {
                case CLOTHES:
                    catGrid.add(createLabel("Size:"), 0, 0);
                    catGrid.add(txtSize, 1, 0);
                    catGrid.add(createLabel("Color:"), 0, 1);
                    catGrid.add(txtClothesColor, 1, 1);
                    catGrid.add(createLabel("Material:"), 0, 2);
                    catGrid.add(txtMaterial, 1, 2);
                    categorySection.setStyle(getSectionStyle("#e3f2fd"));
                    break;
                case SHOES:
                    catGrid.add(createLabel("Shoe Size:"), 0, 0);
                    catGrid.add(txtShoeSize, 1, 0);
                    catGrid.add(createLabel("Color:"), 0, 1);
                    catGrid.add(txtShoesColor, 1, 1);
                    catGrid.add(createLabel("Brand:"), 0, 2);
                    catGrid.add(txtBrand, 1, 2);
                    categorySection.setStyle(getSectionStyle("#f3e5f5"));
                    break;
                case ACCESSORIES:
                    catGrid.add(createLabel("Type:"), 0, 0);
                    catGrid.add(txtType, 1, 0);
                    catGrid.add(createLabel("Color:"), 0, 1);
                    catGrid.add(txtAccessoriesColor, 1, 1);
                    catGrid.add(createLabel("Material:"), 0, 2);
                    catGrid.add(txtAccessoriesMaterial, 1, 2);
                    categorySection.setStyle(getSectionStyle("#fff3e0"));
                    break;
            }
            categoryFields.getChildren().add(catGrid);
        });

        categorySection.getChildren().add(categoryFields);

        // Info box
        HBox infoBox = new HBox(10);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setStyle("-fx-background-color: #D4E4D0; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #9BB896; -fx-border-width: 1; -fx-border-radius: 8;");
        Label infoLabel = new Label("💡 Ensure selling price exceeds cost for profitable margins");
        infoLabel.setStyle("-fx-text-fill: #4A5D48; -fx-font-size: 12px; -fx-font-family: 'Georgia';");
        infoBox.getChildren().add(infoLabel);

        mainContainer.getChildren().addAll(basicSection, categorySection, infoBox);

        dialog.getDialogPane().setContent(mainContainer);
        dialog.getDialogPane().setPrefWidth(550);

        // Populate fields if editing
        if (existingProduct != null) {
            txtName.setText(existingProduct.getName());
            cmbCategory.setValue(existingProduct.getCategory());
            txtPurchasePrice.setText(String.format("%.2f", existingProduct.getPurchasePrice()));
            txtSellingPrice.setText(String.format("%.2f", existingProduct.getSellingPrice()));

            if (existingProduct instanceof Clothes) {
                Clothes clothes = (Clothes) existingProduct;
                txtSize.setText(clothes.getSize());
                txtClothesColor.setText(clothes.getColor());
                txtMaterial.setText(clothes.getMaterial());
            } else if (existingProduct instanceof Shoes) {
                Shoes shoes = (Shoes) existingProduct;
                txtShoeSize.setText(String.valueOf(shoes.getShoeSize()));
                txtShoesColor.setText(shoes.getColor());
                txtBrand.setText(shoes.getBrand());
            } else if (existingProduct instanceof Accessories) {
                Accessories acc = (Accessories) existingProduct;
                txtType.setText(acc.getType());
                txtAccessoriesColor.setText(acc.getColor());
                txtAccessoriesMaterial.setText(acc.getMaterial());
            }

            cmbCategory.setDisable(true);
        }

        // Trigger initial category setup
        cmbCategory.fireEvent(new javafx.event.ActionEvent());

        // Enable/disable save button based on validation
        javafx.scene.Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Real-time validation
        txtName.textProperty().addListener((obs, old, newVal) ->
                saveButton.setDisable(newVal.trim().isEmpty()));

        // Convert result to Product
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = txtName.getText().trim();
                    double purchasePrice = Double.parseDouble(txtPurchasePrice.getText().replace(",", "."));
                    double sellingPrice = Double.parseDouble(txtSellingPrice.getText().replace(",", "."));
                    ProductCategory category = cmbCategory.getValue();

                    // Validation
                    if (name.isEmpty()) {
                        showAlert("Validation Error", "Product name cannot be empty!", Alert.AlertType.ERROR);
                        return null;
                    }

                    if (purchasePrice <= 0) {
                        showAlert("Validation Error", "Purchase price must be greater than zero!", Alert.AlertType.ERROR);
                        return null;
                    }

                    if (sellingPrice <= 0) {
                        showAlert("Validation Error", "Selling price must be greater than zero!", Alert.AlertType.ERROR);
                        return null;
                    }

                    if (sellingPrice <= purchasePrice) {
                        showAlert("Validation Error",
                                "⚠️ Warning: Selling price should be higher than purchase price for profit!\n\n" +
                                        "Current margin: €" + String.format("%.2f", sellingPrice - purchasePrice),
                                Alert.AlertType.WARNING);
                        return null;
                    }

                    Product product = null;
                    int stock = existingProduct != null ? existingProduct.getStockQuantity() : 0;

                    switch (category) {
                        case CLOTHES:
                            String size = txtSize.getText().trim();
                            String clothesColor = txtClothesColor.getText().trim();
                            String material = txtMaterial.getText().trim();

                            if (size.isEmpty() || clothesColor.isEmpty()) {
                                showAlert("Validation Error", "Please fill in Size and Color fields!", Alert.AlertType.ERROR);
                                return null;
                            }

                            product = new Clothes(name, purchasePrice, sellingPrice, stock,
                                    size, clothesColor, material.isEmpty() ? "N/A" : material);
                            break;

                        case SHOES:
                            int shoeSize;
                            try {
                                shoeSize = Integer.parseInt(txtShoeSize.getText().trim());
                            } catch (NumberFormatException e) {
                                showAlert("Validation Error", "Please enter a valid shoe size (number)!", Alert.AlertType.ERROR);
                                return null;
                            }

                            String shoesColor = txtShoesColor.getText().trim();
                            String brand = txtBrand.getText().trim();

                            if (shoeSize <= 0 || shoesColor.isEmpty()) {
                                showAlert("Validation Error", "Please fill in Shoe Size and Color fields!", Alert.AlertType.ERROR);
                                return null;
                            }

                            product = new Shoes(name, purchasePrice, sellingPrice, stock,
                                    shoeSize, shoesColor, brand.isEmpty() ? "Generic" : brand);
                            break;

                        case ACCESSORIES:
                            String type = txtType.getText().trim();
                            String accColor = txtAccessoriesColor.getText().trim();
                            String accMaterial = txtAccessoriesMaterial.getText().trim();

                            if (type.isEmpty() || accColor.isEmpty()) {
                                showAlert("Validation Error", "Please fill in Type and Color fields!", Alert.AlertType.ERROR);
                                return null;
                            }

                            product = new Accessories(name, purchasePrice, sellingPrice, stock,
                                    type, accColor, accMaterial.isEmpty() ? "N/A" : accMaterial);
                            break;
                    }

                    if (existingProduct != null && product != null) {
                        product.setId(existingProduct.getId());
                        product.setDiscountActive(existingProduct.isDiscountActive());
                    }

                    return product;

                } catch (NumberFormatException e) {
                    showAlert("Validation Error",
                            "Please enter valid numbers for prices!\n\nUse format: 10.50 or 10,50",
                            Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        Optional<Product> result = dialog.showAndWait();

        result.ifPresent(product -> {
            try {
                boolean success;
                if (existingProduct == null) {
                    success = productDAO.createProduct(product);
                } else {
                    success = productDAO.updateProduct(product);
                }

                if (success) {
                    onSuccess.run();
                } else {
                    showAlert("Error", "Failed to save product to database", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                showAlert("Database Error",
                        "An error occurred while saving to database:\n\n" + e.getMessage(),
                        Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        });
    }

    private VBox createSection(String title) {
        VBox section = new VBox(10);
        section.setStyle(getSectionStyle("#FFFFFF"));

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #5A4A3A; -fx-font-family: 'Georgia';");
        section.getChildren().add(titleLabel);

        return section;
    }

    private String getSectionStyle(String bgColor) {
        return String.format(
                "-fx-background-color: %s; -fx-padding: 18; -fx-background-radius: 10; " +
                        "-fx-border-color: #E8DCC8; -fx-border-width: 1.5; -fx-border-radius: 10;",
                bgColor
        );
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-pref-width: 280; -fx-padding: 10; -fx-background-radius: 6; " +
                "-fx-background-color: #FDFBF7; -fx-border-color: #D4C4B0; -fx-border-radius: 6; -fx-font-family: 'Georgia';");
        return field;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #5A4A3A; -fx-font-family: 'Georgia';");
        return label;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}