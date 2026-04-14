package JStream.controller;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import JStream.entity.Category;
import JStream.service.CategoryService;

public class CategoriesController {

    @FXML private VBox      categoryListContainer;
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private Button    addButton;
    @FXML private Button    cancelEditButton;
    @FXML private Label     validationLabel;   // add this Label to your FXML

    private CategoryService categoryService = new CategoryService();
    private Category        selectedCategory;

    @FXML
    public void initialize() {
        loadCategories();
    }

    // ── Load ──────────────────────────────────────────────────────────────────
    private void loadCategories() {
        List<Category> categories = categoryService.getAllCategories();
        categoryListContainer.getChildren().clear();
        for (Category category : categories) {
            categoryListContainer.getChildren().add(createCategoryCard(category));
        }
    }

    // ── Card builder ──────────────────────────────────────────────────────────
    private HBox createCategoryCard(Category category) {
        Label title = new Label(category.getName());
        title.getStyleClass().add("category-card-title");

        Label description = new Label(category.getDescription());
        description.getStyleClass().add("category-card-description");
        description.setWrapText(true);
        description.setMaxWidth(500);

        VBox textBox = new VBox(title, description);
        textBox.setSpacing(8);

        Button editButton = new Button("Edit");
        editButton.getStyleClass().addAll("category-card-button", "edit");
        editButton.setOnAction(e -> editCategory(category));

        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("category-card-button", "delete");
        deleteButton.setOnAction(e -> deleteCategory(category));

        HBox buttonBox = new HBox(editButton, deleteButton);
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        HBox card = new HBox(textBox, buttonBox);
        card.getStyleClass().add("category-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setSpacing(20);
        card.setPadding(new Insets(20));
        HBox.setHgrow(textBox, Priority.ALWAYS);

        return card;
    }

    // ── Edit / cancel ─────────────────────────────────────────────────────────
    private void editCategory(Category category) {
        selectedCategory = category;
        nameField.setText(category.getName());
        descriptionField.setText(category.getDescription());
        addButton.setText("Save");
        cancelEditButton.setVisible(true);
        cancelEditButton.setManaged(true);
        clearValidation();
    }

    @FXML
    private void cancelEdit() {
        selectedCategory = null;
        nameField.clear();
        descriptionField.clear();
        addButton.setText("Add");
        cancelEditButton.setVisible(false);
        cancelEditButton.setManaged(false);
        clearValidation();
    }

    // ── Add / update ──────────────────────────────────────────────────────────
    @FXML
    private void addCategory() {
        String name        = nameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty()) {
            showValidation("Category name is required.");
            return;
        }

        // ── FIX: duplicate name check ─────────────────────────────────────────
        // Only block creation when adding a brand-new category.
        // When editing, skip the check for the category's own current name.
        if (selectedCategory == null) {
            // Adding new — name must not already exist (case-insensitive)
            boolean nameExists = categoryService.getAllCategories()
                .stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));

            if (nameExists) {
                showValidation("A category named \"" + name + "\" already exists.");
                return;
            }
        } else {
            // Editing — allow keeping the same name, but block collision with others
            boolean nameConflict = categoryService.getAllCategories()
                .stream()
                .filter(c -> c.getCategory_id() != selectedCategory.getCategory_id())
                .anyMatch(c -> c.getName().equalsIgnoreCase(name));

            if (nameConflict) {
                showValidation("Another category named \"" + name + "\" already exists.");
                return;
            }
        }

        clearValidation();

        if (selectedCategory != null) {
            selectedCategory.setName(name);
            selectedCategory.setDescription(description);
            categoryService.updateCategory(selectedCategory);
        } else {
            Category cat = new Category();
            cat.setName(name);
            cat.setDescription(description);
            categoryService.addCategory(cat);
        }

        cancelEdit();
        loadCategories();
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    private void deleteCategory(Category category) {
        categoryService.deleteCategory(category.getCategory_id());
        if (selectedCategory != null &&
            selectedCategory.getCategory_id() == category.getCategory_id()) {
            cancelEdit();
        }
        loadCategories();
    }

    // ── Validation helpers ────────────────────────────────────────────────────
    private void showValidation(String msg) {
        if (validationLabel != null) {
            validationLabel.setText(msg);
            validationLabel.setVisible(true);
            validationLabel.setManaged(true);
        }
    }

    private void clearValidation() {
        if (validationLabel != null) {
            validationLabel.setText("");
            validationLabel.setVisible(false);
            validationLabel.setManaged(false);
        }
    }
}