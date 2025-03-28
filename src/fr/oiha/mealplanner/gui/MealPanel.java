package fr.oiha.mealplanner.gui;

import fr.oiha.mealplanner.service.MealPlannerService;
import fr.oiha.mealplanner.model.Ingredient;
import fr.oiha.mealplanner.model.Meal;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel for displaying and managing meals
 */
public class MealPanel extends JPanel {
    private JToolBar toolBar;
    private JButton addMealButton;
    private JButton modifyButton;
    private JButton deleteButton;
    private JScrollPane scrollPane;
    private JTable mealTable;
    // Mapping entre les indices de ligne et les IDs de repas
    private Map<Integer, Integer> rowToMealId;

    public MealPanel() {
        rowToMealId = new HashMap<>();
        initComponents();
        setupEventHandlers();
        setName("MealPanel");
        loadMeals();
    }

    private void initComponents() {
        // Initialize components
        toolBar = new JToolBar();
        addMealButton = new JButton("Add a meal");
        modifyButton = new JButton("Modify a meal");
        deleteButton = new JButton("Delete a meal");
        scrollPane = new JScrollPane();
        mealTable = new JTable();

        // Setup panel
        setLayout(new BorderLayout());

        // Setup toolbar
        toolBar.setFloatable(false);
        toolBar.add(addMealButton);
        toolBar.add(modifyButton);
        toolBar.add(deleteButton);
        add(toolBar, BorderLayout.NORTH);

        // Setup table
        mealTable.setShowHorizontalLines(true);
        mealTable.setShowVerticalLines(true);
        mealTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mealTable.setRowSelectionAllowed(true);
        mealTable.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "Name of the Meal", "Total Price", "Nbr Ingredients"
                }
        ));

        scrollPane.setViewportView(mealTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        addMealButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddMealFrame dialog = new AddMealFrame(MealPanel.this);
                dialog.setVisible(true);
            }
        });

        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = mealTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(MealPanel.this, "Please select a meal to modify");
                    return;
                }
                // Récupérer l'ID du repas à partir de la ligne sélectionnée
                Integer mealId = rowToMealId.get(selectedRow);
                if (mealId == null) {
                    JOptionPane.showMessageDialog(MealPanel.this, "Error: Could not find meal ID");
                    return;
                }
                ModifyMealFrame dialog = new ModifyMealFrame(MealPanel.this, mealId);
                dialog.setVisible(true);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = mealTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(MealPanel.this, "Please select a meal to delete");
                    return;
                }
                // Récupérer l'ID du repas à partir de la ligne sélectionnée
                Integer mealId = rowToMealId.get(selectedRow);
                if (mealId == null) {
                    JOptionPane.showMessageDialog(MealPanel.this, "Error: Could not find meal ID");
                    return;
                }
                MealPlannerService.getInstance().removeMeal(mealId);
                loadMeals();
            }
        });
    }

    public void loadMeals() {
        ((DefaultTableModel) mealTable.getModel()).setRowCount(0);
        rowToMealId.clear();
        int rowIndex = 0;
        
        for (Meal meal : MealPlannerService.getInstance().getMeals()) {
            // Calculate total price
            double totalPrice = 0.0;
            for (Ingredient ingredient : meal.getIngredients()) {
                // Calculate cost based on the proportion of the pack used
                double proportion = ingredient.getQuantity() / ingredient.getProduct().getWeightPerPack();
                totalPrice += proportion * ingredient.getProduct().getPricePerPack();
            }
            
            ((DefaultTableModel) mealTable.getModel()).addRow(new Object[]{
                    meal.getName(),
                    String.format("%.2f €", totalPrice),
                    meal.getIngredients().size()
            });
            
            // Enregistrer l'ID du repas pour cette ligne
            rowToMealId.put(rowIndex, meal.getId());
            rowIndex++;
        }
    }

    public JTable getMealTable() {
        return mealTable;
    }
}
