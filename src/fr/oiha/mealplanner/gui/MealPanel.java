package fr.oiha.mealplanner.gui;

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
                // TODO: Implement meal addition
                JOptionPane.showMessageDialog(MealPanel.this, "Add meal functionality not implemented yet");
            }
        });

        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mealTable.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(MealPanel.this, "Please select a meal to modify");
                    return;
                }
                // TODO: Implement meal modification
                JOptionPane.showMessageDialog(MealPanel.this, "Modify meal functionality not implemented yet");
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mealTable.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(MealPanel.this, "Please select a meal to delete");
                    return;
                }
                // TODO: Implement meal deletion
                JOptionPane.showMessageDialog(MealPanel.this, "Delete meal functionality not implemented yet");
            }
        });
    }

    public JTable getMealTable() {
        return mealTable;
    }
}