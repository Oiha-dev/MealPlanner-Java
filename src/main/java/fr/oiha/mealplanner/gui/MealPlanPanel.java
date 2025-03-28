package main.java.fr.oiha.mealplanner.gui;

import fr.oiha.mealplanner.model.Meal;
import fr.oiha.mealplanner.model.MealPlan;
import fr.oiha.mealplanner.service.MealPlannerService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;

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
                try {
                    int mealCount = (int) mealCountSpinner.getValue();
                    double budget = Double.parseDouble(budgetField.getText().replace(',', '.'));
                    
                    if (mealCount <= 0) {
                        JOptionPane.showMessageDialog(MealPlanPanel.this,
                                "Le nombre de repas doit être supérieur à zéro.", 
                                "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (budget <= 0) {
                        JOptionPane.showMessageDialog(MealPlanPanel.this,
                                "Le budget doit être supérieur à zéro.", 
                                "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    currentMealPlan = service.generateMealPlan(budget, mealCount);
                    
                    if (currentMealPlan == null || currentMealPlan.getMeals().isEmpty()) {
                        JOptionPane.showMessageDialog(MealPlanPanel.this,
                                "Impossible de générer un plan de repas. Vérifiez que vous avez des repas enregistrés.", 
                                "Génération échouée", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    updateMealPlanTable();
                    service.generateShoppingList(currentMealPlan);
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(MealPlanPanel.this,
                            "Veuillez entrer un budget valide.", 
                            "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentMealPlan == null || currentMealPlan.getMeals().isEmpty()) {
                    JOptionPane.showMessageDialog(MealPlanPanel.this,
                            "Veuillez d'abord générer un plan de repas.",
                            "Export impossible", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Sauvegarder le plan de repas");
                fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers Markdown (*.md)", "md"));
                fileChooser.setSelectedFile(new File("plan_repas.md"));
                
                int userSelection = fileChooser.showSaveDialog(MealPlanPanel.this);
                
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    String filePath = fileToSave.getAbsolutePath();
                    
                    // Ajouter l'extension .md si nécessaire
                    if (!filePath.toLowerCase().endsWith(".md")) {
                        filePath += ".md";
                    }
                    
                    if (service.exportMealPlanToMarkdown(currentMealPlan, filePath)) {
                        JOptionPane.showMessageDialog(MealPlanPanel.this,
                                "Plan de repas exporté avec succès vers " + filePath,
                                "Exportation réussie", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(MealPlanPanel.this,
                                "Erreur lors de l'exportation du plan de repas.",
                                "Erreur d'exportation", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }
    
    private void updateMealPlanTable() {
        if (currentMealPlan == null) {
            return;
        }
        
        DefaultTableModel model = new DefaultTableModel(
                new Object[][] {},
                new String[] {"Repas", "Prix"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre toutes les cellules non modifiables
            }
        };
        
        double totalCost = 0.0;
        
        for (Meal meal : currentMealPlan.getMeals()) {
            double mealCost = service.calculateMealCost(meal);
            totalCost += mealCost;
            model.addRow(new Object[] {
                    meal.getName(),
                    priceFormat.format(mealCost)
            });
        }
        
        mealPlanTable.setModel(model);
        
        // Mise à jour du coût total
        updateTotalCost(totalCost);
    }

    public JTable getMealPlanTable() {
        return mealPlanTable;
    }

    public void updateTotalCost(double cost) {
        totalCostLabel.setText(String.format("Coût total: %.2f €", cost));
    }
}
