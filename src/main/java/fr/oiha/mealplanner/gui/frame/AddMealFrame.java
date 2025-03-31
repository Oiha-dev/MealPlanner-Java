package fr.oiha.mealplanner.gui.frame;

import fr.oiha.mealplanner.gui.component.DarkButton;
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

public class AddMealFrame extends JFrame {
    private JTextField nameField;
    private JTextArea recipeArea;
    private JTable ingredientsTable;
    private DefaultTableModel tableModel;
    private DarkButton addIngredientButton;
    private DarkButton removeIngredientButton;
    private DarkButton saveButton;
    private DarkButton cancelButton;
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
        nameField.setBackground(Color.DARK_GRAY);
        nameField.setForeground(Color.WHITE);

        recipeArea = new JTextArea(5, 20);
        recipeArea.setLineWrap(true);
        recipeArea.setWrapStyleWord(true);
        recipeArea.setBackground(Color.DARK_GRAY);
        recipeArea.setForeground(Color.WHITE);

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
        ingredientsTable.setBackground(Color.DARK_GRAY);
        ingredientsTable.setForeground(Color.WHITE);
        ingredientsTable.setGridColor(Color.GRAY);
        ingredientsTable.setSelectionBackground(Color.GRAY);
        ingredientsTable.setSelectionForeground(Color.WHITE);

        JTableHeader tableHeader = ingredientsTable.getTableHeader();
        tableHeader.setBackground(Color.DARK_GRAY);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setReorderingAllowed(false);

        JScrollPane tableScrollPane = new JScrollPane(ingredientsTable);
        tableScrollPane.getViewport().setBackground(Color.DARK_GRAY);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        addIngredientButton = new DarkButton("Add Ingredient");
        addIngredientButton.setPreferredSize(new Dimension(140, 40));
        removeIngredientButton = new DarkButton("Remove Ingredient");
        removeIngredientButton.setPreferredSize(new Dimension(170, 40));
        saveButton = new DarkButton("Save");
        cancelButton = new DarkButton("Cancel");

        addIngredientButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        removeIngredientButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        saveButton.setHoverBackgroundColor(new Color(82, 113, 82));
        cancelButton.setHoverBackgroundColor(new Color(128, 52, 52));
    }

    private void setupLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(Color.DARK_GRAY);

        // Form panel for name and recipe
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.DARK_GRAY);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Meal Name:");
        nameLabel.setForeground(Color.WHITE);
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        // Recipe field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        JLabel recipeLabel = new JLabel("Recipe:");
        recipeLabel.setForeground(Color.WHITE);
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
        ingredientsPanel.setBackground(Color.DARK_GRAY);

        JScrollPane tableScrollPane = new JScrollPane(ingredientsTable);
        tableScrollPane.getViewport().setBackground(Color.DARK_GRAY);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        ingredientsPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel ingredientButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ingredientButtonPanel.setBackground(Color.DARK_GRAY);
        ingredientButtonPanel.add(addIngredientButton);
        ingredientButtonPanel.add(removeIngredientButton);
        ingredientsPanel.add(ingredientButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(ingredientsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.DARK_GRAY);
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
                    JOptionPane.showMessageDialog(AddMealFrame.this,
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
        dialog.getContentPane().setBackground(Color.DARK_GRAY);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.DARK_GRAY);
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
        productComboBox.setBackground(Color.DARK_GRAY);
        productComboBox.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel productLabel = new JLabel("Product:");
        productLabel.setForeground(Color.WHITE);
        panel.add(productLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(productComboBox, gbc);

        // Quantity field
        JFormattedTextField quantityField = new JFormattedTextField(NumberFormat.getNumberInstance());
        quantityField.setValue(1.0);
        quantityField.setColumns(10);
        quantityField.setBackground(Color.DARK_GRAY);
        quantityField.setForeground(Color.WHITE);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setForeground(Color.WHITE);
        panel.add(quantityLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(quantityField, gbc);

        dialog.add(panel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.DARK_GRAY);
        DarkButton addButton = new DarkButton("Add");
        DarkButton cancelButton = new DarkButton("Cancel");

        addButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        cancelButton.setHoverBackgroundColor(Color.LIGHT_GRAY);

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

        MealPlannerService.getInstance().addMeal(name, ingredients, recipe);
        DataStorageService.saveMeals(MealPlannerService.getInstance().getMeals());
        JOptionPane.showMessageDialog(this,
                "Meal added successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
