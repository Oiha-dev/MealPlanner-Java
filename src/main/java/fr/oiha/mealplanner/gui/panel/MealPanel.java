package fr.oiha.mealplanner.gui.panel;

import fr.oiha.mealplanner.gui.component.DarkButton;
import fr.oiha.mealplanner.gui.frame.AddMealFrame;
import fr.oiha.mealplanner.gui.frame.ModifyMealFrame;
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
 * MealPanel is a JPanel that displays a list of meals in a table format.
 * It allows users to add, modify, and delete meals.
 * It uses a JToolBar for action buttons and a JTable to display meal information.
 * It also handles the loading of meals from the MealPlannerService.
 */
public class MealPanel extends JPanel {
    private JToolBar toolBar;
    private JButton addMealButton;
    private JButton modifyButton;
    private JButton deleteButton;
    private JScrollPane scrollPane;
    private JTable mealTable;
    private Map<Integer, Integer> rowToMealId;

    public MealPanel() {
        rowToMealId = new HashMap<>();
        initComponents();
        setupEventHandlers();
        setName("MealPanel");
        loadMeals();
    }

    private void initComponents() {
        toolBar = new JToolBar();
        addMealButton = new DarkButton("Add a meal");
        modifyButton = new DarkButton("Modify a meal");
        deleteButton = new DarkButton("Delete a meal");
        scrollPane = new JScrollPane();
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        mealTable = new JTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                if (component instanceof JComponent) {
                    ((JComponent) component).setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
                }
                return component;
            }
        };


        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);


        toolBar.setFloatable(false);
        toolBar.setBackground(Color.DARK_GRAY);
        toolBar.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        addMealButton.setBackground(Color.DARK_GRAY);
        addMealButton.setForeground(Color.WHITE);
        modifyButton.setBackground(Color.DARK_GRAY);
        modifyButton.setForeground(Color.WHITE);
        deleteButton.setBackground(Color.DARK_GRAY);
        deleteButton.setForeground(Color.WHITE);
        toolBar.add(addMealButton);
        toolBar.add(modifyButton);
        toolBar.add(deleteButton);
        add(toolBar, BorderLayout.NORTH);


        mealTable.setShowHorizontalLines(true);
        mealTable.setShowVerticalLines(true);
        mealTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mealTable.setRowSelectionAllowed(true);
        mealTable.setBackground(Color.DARK_GRAY);
        mealTable.setForeground(Color.WHITE);
        mealTable.setGridColor(Color.GRAY);
        mealTable.setSelectionBackground(Color.GRAY);
        mealTable.setSelectionForeground(Color.WHITE);
        mealTable.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "Name of the Meal", "Total Price", "Nbr Ingredients"
                }
        ));

        JTableHeader tableHeader = mealTable.getTableHeader();
        tableHeader.setBackground(Color.DARK_GRAY);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setReorderingAllowed(false);

        scrollPane.setViewportView(mealTable);
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        // This method is called when the add meal button is clicked
        // It opens a dialog to add a new meal
        addMealButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddMealFrame dialog = new AddMealFrame(MealPanel.this);
                dialog.setVisible(true);
            }
        });

        // This method is called when the modify button is clicked
        // It checks if a meal is selected and opens a dialog to modify it
        // If no meal is selected, it shows a message dialog
        // If the meal ID is not found, it shows an error message
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = mealTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(MealPanel.this, "Please select a meal to modify");
                    return;
                }

                Integer mealId = rowToMealId.get(selectedRow);
                if (mealId == null) {
                    JOptionPane.showMessageDialog(MealPanel.this, "Error: Could not find meal ID");
                    return;
                }
                ModifyMealFrame dialog = new ModifyMealFrame(MealPanel.this, mealId);
                dialog.setVisible(true);
            }
        });

        // This method is called when the delete button is clicked
        // It checks if a meal is selected and deletes it
        // If no meal is selected, it shows a message dialog
        // If the meal ID is not found, it shows an error message
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = mealTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(MealPanel.this, "Please select a meal to delete");
                    return;
                }

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

    /**
     * Loads the meals from the MealPlannerService and populates the meal table.
     * It clears the existing rows in the table and adds new rows for each meal.
     * It also calculates the total price of each meal based on its ingredients.
     */
    public void loadMeals() {
        ((DefaultTableModel) mealTable.getModel()).setRowCount(0);
        rowToMealId.clear();
        int rowIndex = 0;

        for (Meal meal : MealPlannerService.getInstance().getMeals()) {

            double totalPrice = 0.0;
            for (Ingredient ingredient : meal.getIngredients()) {

                double proportion = ingredient.getQuantity() / ingredient.getProduct().getWeightPerPack();
                totalPrice += proportion * ingredient.getProduct().getPricePerPack();
            }
            
            ((DefaultTableModel) mealTable.getModel()).addRow(new Object[]{
                    meal.getName(),
                    String.format("%.2f €", totalPrice),
                    meal.getIngredients().size()
            });


            rowToMealId.put(rowIndex, meal.getId());
            rowIndex++;
        }
    }
}
