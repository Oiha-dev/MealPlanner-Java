package main.java.fr.oiha.mealplanner.gui;

import main.java.fr.oiha.mealplanner.service.MealPlannerService;
import main.java.fr.oiha.mealplanner.model.Ingredient;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public MealPanel() {
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
                if (mealTable.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(MealPanel.this, "Please select a meal to modify");
                    return;
                }
                ModifyMealFrame dialog = new ModifyMealFrame(MealPanel.this, mealTable.getSelectedRow());
                dialog.setVisible(true);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mealTable.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(MealPanel.this, "Please select a meal to delete");
                    return;
                }
                MealPlannerService.getInstance().removeMeal(mealTable.getSelectedRow());
                loadMeals();
            }
        });
    }

    public void loadMeals() {
        ((DefaultTableModel) mealTable.getModel()).setRowCount(0);
        MealPlannerService.getInstance().getMeals().forEach(m -> {
            // Calculate total price
            double totalPrice = 0.0;
            for (Ingredient ingredient : m.getIngredients()) {
                // Calculate cost based on the proportion of the pack used
                double proportion = ingredient.getQuantity() / ingredient.getProduct().getWeightPerPack();
                totalPrice += proportion * ingredient.getProduct().getPricePerPack();
            }
            
            ((DefaultTableModel) mealTable.getModel()).addRow(new Object[]{
                    m.getName(),
                    String.format("%.2f €", totalPrice),
                    m.getIngredients().size()
            });
        });
    }

    public JTable getMealTable() {
        return mealTable;
    }
}
