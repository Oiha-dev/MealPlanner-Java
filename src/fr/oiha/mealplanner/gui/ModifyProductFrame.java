package fr.oiha.mealplanner.gui;

import fr.oiha.mealplanner.model.Product;
import fr.oiha.mealplanner.service.MealPlannerService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;

public class ModifyProductFrame extends JFrame {
    private JTextField nameField;
    private JComboBox<String> unitComboBox;
    private JFormattedTextField weightPerPackField;
    private JFormattedTextField pricePerPackField;
    private ProductPanel parentFrame;
    private int id;

    private JButton saveButton;
    private JButton cancelButton;

    public ModifyProductFrame(ProductPanel parent, int id) {
        super("Modify Product");
        this.parentFrame = parent;
        this.id = id;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        loadProductData();
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

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
    }

    private void loadProductData() {
        Product product = MealPlannerService.getInstance().getProducts().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);

        if (product != null) {
            nameField.setText(product.getName());
            unitComboBox.setSelectedItem(product.getUnit());
            pricePerPackField.setValue(product.getPricePerPack());
            weightPerPackField.setValue(product.getWeightPerPack());
        } else {
            JOptionPane.showMessageDialog(this,
                    "Product not found",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            dispose();
        }
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
                    MealPlannerService.getInstance().modifyProduct(id, getProductName(), getPricePerPack(), getWeightPerPack(), getUnit());
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
        getRootPane().setDefaultButton(saveButton);
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