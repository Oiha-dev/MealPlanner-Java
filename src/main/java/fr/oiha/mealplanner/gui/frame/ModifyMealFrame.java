package fr.oiha.mealplanner.gui.frame;

import fr.oiha.mealplanner.exception.MealNotFoundException;
import fr.oiha.mealplanner.gui.component.CustomButton;
import fr.oiha.mealplanner.gui.dialog.AddIngredientDialog;
import fr.oiha.mealplanner.gui.panel.MealPanel;
import fr.oiha.mealplanner.model.Ingredient;
import fr.oiha.mealplanner.model.Meal;
import fr.oiha.mealplanner.model.Product;
import fr.oiha.mealplanner.service.DataStorageService;
import fr.oiha.mealplanner.service.MealPlannerService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Frame for modifying a meal
 * This class allows the user to modify the details of a meal, including its name, recipe, and ingredients.
 * It provides a user-friendly interface for adding, removing, and saving ingredients.
 * It also validates the input before saving the changes.
 */
public class ModifyMealFrame extends JFrame {
    private JTextField nameField;
    private JTextArea recipeArea;
    private JTable ingredientsTable;
    private DefaultTableModel tableModel;
    private CustomButton addIngredientButton;
    private CustomButton removeIngredientButton;
    private CustomButton saveButton;
    private CustomButton cancelButton;
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

        String[] columnNames = {"Product", "Quantity", "Unit"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; 
            }
        };
        ingredientsTable = new JTable(tableModel);
        ingredientsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        ingredientsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        ingredientsTable.getColumnModel().getColumn(2).setPreferredWidth(80);

        JTableHeader tableHeader = ingredientsTable.getTableHeader();
        tableHeader.setReorderingAllowed(false);

        JScrollPane tableScrollPane = new JScrollPane(ingredientsTable);

        addIngredientButton = new CustomButton("Add Ingredient");
        addIngredientButton.setPreferredSize(new Dimension(140, 40));
        removeIngredientButton = new CustomButton("Remove Ingredient");
        removeIngredientButton.setPreferredSize(new Dimension(170, 40));
        saveButton = new CustomButton("Save");
        cancelButton = new CustomButton("Cancel");
    }

    /**
     * Loads the meal data into the form fields and table.
     * This method retrieves the meal by its ID from the MealPlannerService
     * and populates the name, recipe, and ingredients fields.
     * If the meal is not found, it shows an error message and closes the frame.
     */
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
        ingredientsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Ingredients"));

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
        // This method is called when the "Add Ingredient" button is clicked.
        // It opens a dialog to add a new ingredient to the meal.
        addIngredientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddIngredientDialog(ModifyMealFrame.this, tableModel).setVisible(true);
            }
        });

        // This method is called when the "Remove Ingredient" button is clicked.
        // It removes the selected ingredient from the meal.
        // If no ingredient is selected, it shows a warning message.
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

        // This method is called when the "Save" button is clicked.
        // It validates the input and saves the modified meal.
        // If the input is valid, it saves the meal and closes the frame.
        // If the input is invalid, it shows an error message.
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

        // This method is called when the "Cancel" button is clicked.
        // It closes the frame without saving any changes.
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getRootPane().setDefaultButton(saveButton);
    }

    /**
     * Validates the input fields before saving the meal.
     * This method checks if the meal name is not empty and if at least one ingredient is added.
     * If any validation fails, it shows an error message and returns false.
     * @return true if all validations pass, false otherwise.
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
     * Saves the modified meal to the MealPlannerService.
     * This method retrieves the meal name, recipe, and ingredients from the form fields,
     * and updates the meal in the MealPlannerService.
     * It also saves the updated meals to the data storage.
     * If the meal is not found, it shows an error message.
     * @throws MealNotFoundException if the meal is not found in the MealPlannerService.
     * This exception is caught and handled in the calling method.
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
