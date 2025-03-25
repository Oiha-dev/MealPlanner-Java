package fr.oiha.mealplanner.gui;

import fr.oiha.mealplanner.model.Ingredient;
import fr.oiha.mealplanner.model.Meal;
import fr.oiha.mealplanner.service.MealPlannerService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ModifyMealFrame extends JFrame {
    private JTextField nameField;
    private JTextArea recipeArea;
    private JList<Ingredient> ingredientList;
    private DefaultListModel<Ingredient> ingredientListModel;
    private MealPanel parentFrame;
    private int id;

    private JButton saveButton;
    private JButton cancelButton;

    public ModifyMealFrame(MealPanel parent, int id) {
        super("Modify Meal");
        this.parentFrame = parent;
        this.id = id;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
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
        ingredientListModel = new DefaultListModel<>();
        ingredientList = new JList<>(ingredientListModel);

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
            meal.getIngredients().forEach(ingredientListModel::addElement);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Meal not found",
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
        formPanel.add(new JLabel("Meal Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Recipe:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JScrollPane(recipeArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Ingredients:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JScrollPane(ingredientList), gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
    }

    private void setupEventHandlers() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    MealPlannerService.getInstance().modifyMeal(id, getMealName(), getIngredients(), getRecipe());
                    parentFrame.loadMeals();
                    dispose();
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

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a meal name",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        if (recipeArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a recipe",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            recipeArea.requestFocus();
            return false;
        }

        if (ingredientListModel.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please add at least one ingredient",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public String getMealName() {
        return nameField.getText().trim();
    }

    public String getRecipe() {
        return recipeArea.getText().trim();
    }

    public List<Ingredient> getIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        for (int i = 0; i < ingredientListModel.size(); i++) {
            ingredients.add(ingredientListModel.get(i));
        }
        return ingredients;
    }
}
