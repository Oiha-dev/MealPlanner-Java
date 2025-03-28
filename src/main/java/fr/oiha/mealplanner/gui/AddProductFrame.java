package fr.oiha.mealplanner.gui;

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

    private JButton addButton;
    private JButton cancelButton;

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

        addButton = new JButton("Add");
        cancelButton = new JButton("Cancel");
    }

    private void setupLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel with labels and fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Product Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(nameField, gbc);

        // Unit field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Unit:"), gbc);

        gbc.gridx = 1;
        formPanel.add(unitComboBox, gbc);

        // Weight per pack field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Weight per Pack:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(weightPerPackField, gbc);

        // Price per pack field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Price per Pack (€):"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(pricePerPackField, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
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