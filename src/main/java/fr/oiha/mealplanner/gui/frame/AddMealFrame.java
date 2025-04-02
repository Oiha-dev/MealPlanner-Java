package fr.oiha.mealplanner.gui.frame;

import fr.oiha.mealplanner.gui.component.CustomButton;
import fr.oiha.mealplanner.gui.panel.MealPanel;
import fr.oiha.mealplanner.model.Ingredient;
import fr.oiha.mealplanner.model.Product;
import fr.oiha.mealplanner.service.DataStorageService;
import fr.oiha.mealplanner.service.MealPlannerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Frame for adding a new meal
 * This frame allows the user to enter the meal name, recipe, and ingredients.
 */
public class AddMealFrame extends JFrame {
    private JTextField nameField;
    private JTextArea recipeArea;
    private JTable ingredientsTable;
    private DefaultTableModel tableModel;
    private CustomButton addIngredientButton;
    private CustomButton removeIngredientButton;
    private CustomButton saveButton;
    private CustomButton cancelButton;
    private MealPanel parentFrame;

    public AddMealFrame(MealPanel parent) {
        super("Add Meal");
        this.parentFrame = parent;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initComponents() {
        nameField = new JTextField(20);

        recipeArea = new JTextArea(5, 20);
        recipeArea.setLineWrap(true);
        recipeArea.setWrapStyleWord(true);

        // Table for ingredients
        String[] columnNames = {"Product", "Quantity", "Unit"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ingredientsTable = new JTable(tableModel);
        ingredientsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        ingredientsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        ingredientsTable.getColumnModel().getColumn(2).setPreferredWidth(80);

        JTableHeader tableHeader = ingredientsTable.getTableHeader();

        JScrollPane tableScrollPane = new JScrollPane(ingredientsTable);

        addIngredientButton = new CustomButton("Add Ingredient");
        addIngredientButton.setPreferredSize(new Dimension(140, 40));
        removeIngredientButton = new CustomButton("Remove Ingredient");
        removeIngredientButton.setPreferredSize(new Dimension(170, 40));
        saveButton = new CustomButton("Save");
        cancelButton = new CustomButton("Cancel");

        addIngredientButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        removeIngredientButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        saveButton.setHoverBackgroundColor(new Color(82, 113, 82));
        cancelButton.setHoverBackgroundColor(new Color(128, 52, 52));
    }

    private void setupLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Meal Name:");
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        JLabel recipeLabel = new JLabel("Recipe:");
        formPanel.add(recipeLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JScrollPane recipeScrollPane = new JScrollPane(recipeArea);
        formPanel.add(recipeScrollPane, gbc);

        contentPanel.add(formPanel, BorderLayout.NORTH);

        JPanel ingredientsPanel = new JPanel(new BorderLayout(5, 5));
        ingredientsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Ingredients", 0, 0, null, Color.WHITE));

        JScrollPane tableScrollPane = new JScrollPane(ingredientsTable);
        ingredientsPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel ingredientButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ingredientButtonPanel.add(addIngredientButton);
        ingredientButtonPanel.add(removeIngredientButton);
        ingredientsPanel.add(ingredientButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(ingredientsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
    }

    private void setupEventHandlers() {
        // Show the dialog to add an ingredient
        addIngredientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddIngredientDialog();
            }
        });

        // Remove the selected ingredient from the table
        // If no ingredient is selected, show a warning message
        removeIngredientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ingredientsTable.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(AddMealFrame.this,
                            "Please select an ingredient to remove",
                            "No Selection",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Save the meal
        // Validate the input fields and save the meal
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    saveMeal();
                    dispose();
                    if (parentFrame != null) {
                        parentFrame.loadMeals();
                    }
                }
            }
        });

        // Cancel the action and close the dialog
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getRootPane().setDefaultButton(saveButton);
    }

    /**
     * Shows a dialog to add an ingredient to the meal
     * This dialog allows the user to select a product and enter the quantity
     */
    private void showAddIngredientDialog() {
        JDialog dialog = new JDialog(this, "Add Ingredient", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Product dropdown
        Set<Product> products = MealPlannerService.getInstance().getProducts();
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No products available. Please add products first.",
                    "No Products",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JComboBox<Product> productComboBox = new JComboBox<>();
        for (Product product : products) {
            productComboBox.addItem(product);
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel productLabel = new JLabel("Product:");
        panel.add(productLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(productComboBox, gbc);

        JFormattedTextField quantityField = new JFormattedTextField(NumberFormat.getNumberInstance());
        quantityField.setValue(1.0);
        quantityField.setColumns(10);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        JLabel quantityLabel = new JLabel("Quantity:");
        panel.add(quantityLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(quantityField, gbc);

        dialog.add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        CustomButton addButton = new CustomButton("Add");
        CustomButton cancelButton = new CustomButton("Cancel");

        addButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        cancelButton.setHoverBackgroundColor(Color.LIGHT_GRAY);

        // Add the selected product and quantity to the table
        // Validate the quantity and show an error message if invalid
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double quantity = ((Number)quantityField.getValue()).doubleValue();
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(dialog,
                                "Quantity must be greater than zero",
                                "Invalid Quantity",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Product selectedProduct = (Product)productComboBox.getSelectedItem();
                    if (selectedProduct != null) {
                        tableModel.addRow(new Object[]{
                                selectedProduct,
                                quantity,
                                selectedProduct.getUnit()
                        });
                        dialog.dispose();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please enter a valid quantity",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Cancel the action and close the dialog
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.getRootPane().setDefaultButton(addButton);
        dialog.setVisible(true);
    }

    /**
     * Validates the input fields
     * Checks if the meal name is empty and if at least one ingredient is added
     * @return true if the input is valid, false otherwise
     */
    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a meal name",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Please add at least one ingredient",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Saves the meal to the MealPlannerService
     * This method retrieves the meal name, recipe, and ingredients from the input fields
     * and adds the meal to the service
     */
    private void saveMeal() {
        String name = nameField.getText().trim();
        String recipe = recipeArea.getText().trim();
        List<Ingredient> ingredients = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Product product = (Product) tableModel.getValueAt(i, 0);
            double quantity = ((Number) tableModel.getValueAt(i, 1)).doubleValue();
            ingredients.add(new Ingredient(product, quantity));
        }

        MealPlannerService.getInstance().addMeal(name, ingredients, recipe);
        DataStorageService.saveMeals(MealPlannerService.getInstance().getMeals());
        JOptionPane.showMessageDialog(this,
                "Meal added successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
