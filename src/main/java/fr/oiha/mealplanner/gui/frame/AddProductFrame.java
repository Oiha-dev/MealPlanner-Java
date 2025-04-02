package fr.oiha.mealplanner.gui.frame;

import fr.oiha.mealplanner.gui.component.CustomButton;
import fr.oiha.mealplanner.gui.panel.ProductPanel;
import fr.oiha.mealplanner.service.DataStorageService;
import fr.oiha.mealplanner.service.MealPlannerService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;

public class AddProductFrame extends JFrame {
    private JTextField nameField;
    private JComboBox<String> unitComboBox;
    private JFormattedTextField weightPerPackField;
    private JFormattedTextField pricePerPackField;
    private ProductPanel parentFrame;

    private CustomButton addButton;
    private CustomButton cancelButton;

    public AddProductFrame(ProductPanel parent) {
        super("Add New Product");
        this.parentFrame = parent;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        setupLayout();
        setupEventHandlers();
    }

    private void initComponents() {
        nameField = new JTextField(20);

        String[] units = {"kg", "g", "L", "ml", "unit", "pack"};
        unitComboBox = new JComboBox<>(units);

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);

        weightPerPackField = new JFormattedTextField(numberFormat);
        weightPerPackField.setValue(1.0);
        weightPerPackField.setColumns(10);

        pricePerPackField = new JFormattedTextField(numberFormat);
        pricePerPackField.setValue(0.0);
        pricePerPackField.setColumns(10);

        addButton = new CustomButton("Add");
        cancelButton = new CustomButton("Cancel");

        addButton.setHoverBackgroundColor(new Color(82, 113, 82));
        cancelButton.setHoverBackgroundColor(new Color(128, 52, 52));
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
        JLabel nameLabel = new JLabel("Product Name:");
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        JLabel unitLabel = new JLabel("Unit:");
        formPanel.add(unitLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(unitComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel weightLabel = new JLabel("Weight per Pack:");
        formPanel.add(weightLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(weightPerPackField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        JLabel priceLabel = new JLabel("Price per Pack (€):");
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(pricePerPackField, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPanel);
    }

    private void setupEventHandlers() {
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    MealPlannerService.getInstance().addProduct(getProductName(), getPricePerPack(), getWeightPerPack(), getUnit());
                    DataStorageService.saveProducts(MealPlannerService.getInstance().getProducts());
                    parentFrame.loadProducts();
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
        getRootPane().setDefaultButton(addButton);
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a product name",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        try {
            double weight = ((Number)weightPerPackField.getValue()).doubleValue();
            if (weight <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Weight must be greater than zero",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                weightPerPackField.requestFocus();
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid weight",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            weightPerPackField.requestFocus();
            return false;
        }

        try {
            double price = ((Number)pricePerPackField.getValue()).doubleValue();
            if (price < 0) {
                JOptionPane.showMessageDialog(this,
                        "Price cannot be negative",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                pricePerPackField.requestFocus();
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid price",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            pricePerPackField.requestFocus();
            return false;
        }

        return true;
    }

    public String getProductName() {
        return nameField.getText().trim();
    }

    public String getUnit() {
        return (String) unitComboBox.getSelectedItem();
    }

    public double getWeightPerPack() {
        return ((Number)weightPerPackField.getValue()).doubleValue();
    }

    public double getPricePerPack() {
        return ((Number)pricePerPackField.getValue()).doubleValue();
    }
}
