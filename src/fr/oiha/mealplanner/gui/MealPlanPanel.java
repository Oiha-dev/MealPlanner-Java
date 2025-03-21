package fr.oiha.mealplanner.gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel for planning meals
 */
public class MealPlanPanel extends JPanel {
    private JPanel optionsPanel;
    private JLabel mealCountLabel;
    private JSpinner mealCountSpinner;
    private JLabel budgetLabel;
    private JTextField budgetField;

    private JButton generatePlanButton;
    private JButton exportButton;

    private JPanel planPanel;
    private JScrollPane scrollPane;
    private JTable mealPlanTable;

    private JLabel totalCostLabel;

    public MealPlanPanel() {
        initComponents();
        setupEventHandlers();
        setName("MealPlanPanel");
    }

    private void initComponents() {
        // Setup main panel
        setLayout(new BorderLayout(0, 10));

        // Options panel - single line layout
        optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        // Meal count components
        mealCountLabel = new JLabel("Number of meals:");
        mealCountSpinner = new JSpinner(new SpinnerNumberModel(7, 1, 30, 1));
        JComponent editor = mealCountSpinner.getEditor();
        JFormattedTextField ftf = ((JSpinner.DefaultEditor) editor).getTextField();
        ftf.setColumns(2);

        // Budget components
        budgetLabel = new JLabel("Maximum budget (€):");
        budgetField = new JTextField("100", 8);

        // Generate and export buttons - added to the same line
        generatePlanButton = new JButton("Generate Meal Plan");
        exportButton = new JButton("Export List and Recipes");

        // Add all components to a single line
        optionsPanel.add(mealCountLabel);
        optionsPanel.add(mealCountSpinner);
        optionsPanel.add(budgetLabel);
        optionsPanel.add(budgetField);
        optionsPanel.add(generatePlanButton);
        optionsPanel.add(exportButton);

        add(optionsPanel, BorderLayout.NORTH);

        // Meal plan panel
        planPanel = new JPanel(new BorderLayout());
        planPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Generated Meal Plan",
                TitledBorder.LEADING,
                TitledBorder.TOP));

        // Table setup
        mealPlanTable = new JTable();
        mealPlanTable.setShowHorizontalLines(true);
        mealPlanTable.setShowVerticalLines(true);
        mealPlanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mealPlanTable.setRowSelectionAllowed(true);
        mealPlanTable.setModel(new DefaultTableModel(
                new Object[][] {
                        // Empty table initially
                },
                new String[] {
                        "Meal", "Price"
                }
        ));

        // Make the price column right-aligned
        TableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        ((DefaultTableCellRenderer)rightRenderer).setHorizontalAlignment(JLabel.RIGHT);
        mealPlanTable.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

        scrollPane = new JScrollPane(mealPlanTable);
        planPanel.add(scrollPane, BorderLayout.CENTER);

        // Total cost label
        totalCostLabel = new JLabel("Total cost: 0.00 €", JLabel.RIGHT);
        totalCostLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        planPanel.add(totalCostLabel, BorderLayout.SOUTH);

        add(planPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        generatePlanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Implement meal plan generation
                JOptionPane.showMessageDialog(MealPlanPanel.this,
                        "Export functionality not implemented");
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO: Implement exporting functionality
                JOptionPane.showMessageDialog(MealPlanPanel.this,
                        "Export functionality not implemented");
            }
        });
    }

    public JTable getMealPlanTable() {
        return mealPlanTable;
    }

    public void updateTotalCost(double cost) {
        totalCostLabel.setText(String.format("Total cost: %.2f €", cost));
    }
}