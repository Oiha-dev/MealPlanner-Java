package main.java.fr.oiha.mealplanner.gui;

import main.java.fr.oiha.mealplanner.model.Ingredient;
import main.java.fr.oiha.mealplanner.model.Meal;
import main.java.fr.oiha.mealplanner.service.MealPlannerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ModifyMealFrame extends JFrame {
    private JTextField nameField;
    private JTextArea recipeArea;
    private JTable ingredientsTable;
    private DefaultTableModel tableModel;
    private JButton addIngredientButton;
    private JButton removeIngredientButton;
    private JButton saveButton;
    private JButton cancelButton;
    private MealPanel parentFrame;
    private int id;

    public ModifyMealFrame(MealPanel parent, int id) {
        super("Modify Meal");
        this.parentFrame = parent;
        this.id = id;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        loadMealData();
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
                return column == 1; // Only quantity is editable
            }
        };
        ingredientsTable = new JTable(tableModel);
        ingredientsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        ingredientsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        ingredientsTable.getColumnModel().getColumn(2).setPreferredWidth(80);

        addIngredientButton = new JButton("Add Ingredient");
        removeIngredientButton = new JButton("Remove Ingredient");
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
    }

    private void loadMealData() {
        Meal meal = MealPlannerService.getInstance().getMeals().stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElse(null);

        if (meal != null) {
            nameField.setText(meal.getName());
            recipeArea.setText(meal.getRecipe());
            meal.getIngredients().forEach(ingredient ->
                tableModel.addRow(new Object[]{ingredient.getProduct(), ingredient.getQuantity(), ingredient.getProduct().getUnit()}));
        } else {
            JOptionPane.showMessageDialog(this,
                    "Meal not found (ID: " + id + ")",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void setupLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel for name and recipe
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Meal Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        // Recipe field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Recipe:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JScrollPane recipeScrollPane = new JScrollPane(recipeArea);
        formPanel.add(recipeScrollPane, gbc);

        contentPanel.add(formPanel, BorderLayout.NORTH);

        // Ingredients panel
        JPanel ingredientsPanel = new JPanel(new BorderLayout(5, 5));
        ingredientsPanel.setBorder(BorderFactory.createTitledBorder("Ingredients"));

        JScrollPane tableScrollPane = new JScrollPane(ingredientsTable);
        ingredientsPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel ingredientButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ingredientButtonPanel.add(addIngredientButton);
        ingredientButtonPanel.add(removeIngredientButton);
        ingredientsPanel.add(ingredientButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(ingredientsPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
    }

    private void setupEventHandlers() {
        addIngredientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddIngredientDialog();
            }
        });

        removeIngredientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = ingredientsTable.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(ModifyMealFrame.this,
                            "Please select an ingredient to remove",
                            "No Selection",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

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

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getRootPane().setDefaultButton(saveButton);
    }

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
        panel.add(new JLabel("Product:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(productComboBox, gbc);

        // Quantity field
        JFormattedTextField quantityField = new JFormattedTextField(NumberFormat.getNumberInstance());
        quantityField.setValue(1.0);
        quantityField.setColumns(10);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(quantityField, gbc);

        dialog.add(panel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

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

    private void saveMeal() {
        String name = nameField.getText().trim();
        String recipe = recipeArea.getText().trim();
        List<Ingredient> ingredients = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Product product = (Product) tableModel.getValueAt(i, 0);
            double quantity = ((Number) tableModel.getValueAt(i, 1)).doubleValue();
            ingredients.add(new Ingredient(product, quantity));
        }

        try {
            MealPlannerService.getInstance().modifyMeal(id, name, ingredients, recipe);
            DataStorageService.saveMeals(MealPlannerService.getInstance().getMeals());
            JOptionPane.showMessageDialog(this,
                    "Meal modified successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (MealNotFoundException ex) {
            JOptionPane.showMessageDialog(this,
                    "Meal not found: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
