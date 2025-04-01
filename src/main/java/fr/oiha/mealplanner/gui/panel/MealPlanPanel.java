package fr.oiha.mealplanner.gui.panel;

import fr.oiha.mealplanner.model.Meal;
import fr.oiha.mealplanner.model.MealPlan;
import fr.oiha.mealplanner.service.MealPlannerService;
import fr.oiha.mealplanner.gui.component.DarkButton;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.DecimalFormat;

/**
 * MealPlanPanel is a JPanel that allows users to generate and export meal plans.
 * It provides options to specify the number of meals and budget,
 * and displays the generated meal plan in a table format.
 * It also allows users to export the meal plan to a Markdown file.
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

    private MealPlan currentMealPlan;
    private final MealPlannerService service;
    private final DecimalFormat priceFormat = new DecimalFormat("0.00 €");

    public MealPlanPanel() {
        service = MealPlannerService.getInstance();
        initComponents();
        setupEventHandlers();
        setName("MealPlanPanel");
    }

    private void initComponents() {
        
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.DARK_GRAY);

        
        optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        optionsPanel.setBackground(Color.DARK_GRAY);

        mealCountLabel = new JLabel("Number of meals:");
        mealCountLabel.setForeground(Color.WHITE);
        mealCountSpinner = new JSpinner(new SpinnerNumberModel(7, 1, 30, 1));
        JComponent editor = mealCountSpinner.getEditor();
        JFormattedTextField ftf = ((JSpinner.DefaultEditor) editor).getTextField();
        ftf.setColumns(2);
        ftf.setBackground(Color.DARK_GRAY);
        ftf.setForeground(Color.WHITE);

        budgetLabel = new JLabel("Maximum budget (€):");
        budgetLabel.setForeground(Color.WHITE);
        budgetField = new JTextField("100", 8);
        budgetField.setBackground(Color.DARK_GRAY);
        budgetField.setForeground(Color.WHITE);

        generatePlanButton = new DarkButton("Generate Meal Plan");
        generatePlanButton.setPreferredSize(new Dimension(180, 40));
        exportButton = new DarkButton("Export List and Recipes");
        exportButton.setPreferredSize(new Dimension(200, 40));

        optionsPanel.add(mealCountLabel);
        optionsPanel.add(mealCountSpinner);
        optionsPanel.add(budgetLabel);
        optionsPanel.add(budgetField);
        optionsPanel.add(generatePlanButton);
        optionsPanel.add(exportButton);

        add(optionsPanel, BorderLayout.NORTH);

        
        planPanel = new JPanel(new BorderLayout());
        planPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Generated Meal Plan",
                TitledBorder.LEADING,
                TitledBorder.TOP, null, Color.WHITE));
        planPanel.setBackground(Color.DARK_GRAY);

        mealPlanTable = new JTable();
        mealPlanTable.setShowHorizontalLines(true);
        mealPlanTable.setShowVerticalLines(true);
        mealPlanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mealPlanTable.setRowSelectionAllowed(true);
        mealPlanTable.setBackground(Color.DARK_GRAY);
        mealPlanTable.setForeground(Color.WHITE);
        mealPlanTable.setGridColor(Color.GRAY);
        mealPlanTable.setSelectionBackground(Color.GRAY);
        mealPlanTable.setSelectionForeground(Color.WHITE);
        mealPlanTable.setModel(new DefaultTableModel(
                new Object[][] {},
                new String[] { "Meal", "Price" }
        ));

        JTableHeader tableHeader = mealPlanTable.getTableHeader();
        tableHeader.setBackground(Color.DARK_GRAY);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setReorderingAllowed(false);

        scrollPane = new JScrollPane(mealPlanTable);
        scrollPane.getViewport().setBackground(Color.DARK_GRAY);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        planPanel.add(scrollPane, BorderLayout.CENTER);

        totalCostLabel = new JLabel("Total cost: 0.00 €", JLabel.RIGHT);
        totalCostLabel.setForeground(Color.WHITE);
        totalCostLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        planPanel.add(totalCostLabel, BorderLayout.SOUTH);

        add(planPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        generatePlanButton.addActionListener(e -> generateMealPlan());
        exportButton.addActionListener(e -> exportMealPlan());
    }

    /**
     * Generates a meal plan based on the specified budget and number of meals.
     * Displays the generated meal plan in a table format.
     * If the input is invalid or no meals are available,
     * shows an error message.
     */
    private void generateMealPlan() {
        try {
            int mealCount = (int) mealCountSpinner.getValue();
            double budget = Double.parseDouble(budgetField.getText().replace(',', '.'));
            if (mealCount <= 0 || budget <= 0) {
                JOptionPane.showMessageDialog(this, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            currentMealPlan = service.generateMealPlan(budget, mealCount);
            if (currentMealPlan == null || currentMealPlan.getMeals().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No meals available.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            updateMealPlanTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid budget.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exports the current meal plan to a Markdown file.
     * If no meal plan is generated, shows a warning message.
     * If the export is successful, shows a success message.
     * If the export fails, shows an error message.
     */
    private void exportMealPlan() {
        if (currentMealPlan == null || currentMealPlan.getMeals().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Generate a meal plan first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Meal Plan");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Markdown Files (*.md)", "md"));
        fileChooser.setSelectedFile(new File("meal_plan.md"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".md")) filePath += ".md";
            if (service.exportMealPlanToMarkdown(currentMealPlan, filePath)) {
                JOptionPane.showMessageDialog(this, "Exported successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Export failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Updates the meal plan table with the current meal plan.
     * Clears the existing rows and adds new rows for each meal.
     * Calculates the total cost of the meal plan and updates the label.
     */
    private void updateMealPlanTable() {
        DefaultTableModel model = (DefaultTableModel) mealPlanTable.getModel();
        model.setRowCount(0);
        double totalCost = 0;
        for (Meal meal : currentMealPlan.getMeals()) {
            double mealCost = service.calculateMealCost(meal);
            totalCost += mealCost;
            model.addRow(new Object[]{meal.getName(), priceFormat.format(mealCost)});
        }
        totalCostLabel.setText("Total cost: " + priceFormat.format(totalCost));
    }
}
