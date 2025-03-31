package fr.oiha.mealplanner.gui.frame;

import fr.oiha.mealplanner.exception.ProductNotFoundException;
import fr.oiha.mealplanner.gui.panel.ProductPanel;
import fr.oiha.mealplanner.model.Product;
import fr.oiha.mealplanner.service.MealPlannerService;
import fr.oiha.mealplanner.gui.component.DarkButton;

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
    private int productId; 

    private DarkButton saveButton;
    private DarkButton cancelButton;

    public ModifyProductFrame(ProductPanel parent, int productId) {
        super("Modify Product");
        this.parentFrame = parent;
        this.productId = productId; 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setResizable(false);

        getContentPane().setBackground(Color.DARK_GRAY); 

        initComponents();
        loadProductData();
        setupLayout();
        setupEventHandlers();
    }

    private void initComponents() {
        nameField = new JTextField(20);
        nameField.setBackground(Color.DARK_GRAY);
        nameField.setForeground(Color.WHITE);

        String[] units = {"kg", "g", "L", "ml", "unit", "pack"};
        unitComboBox = new JComboBox<>(units);
        unitComboBox.setBackground(Color.DARK_GRAY);
        unitComboBox.setForeground(Color.WHITE);

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);

        weightPerPackField = new JFormattedTextField(numberFormat);
        weightPerPackField.setValue(1.0);
        weightPerPackField.setColumns(10);
        weightPerPackField.setBackground(Color.DARK_GRAY);
        weightPerPackField.setForeground(Color.WHITE);

        pricePerPackField = new JFormattedTextField(numberFormat);
        pricePerPackField.setValue(0.0);
        pricePerPackField.setColumns(10);
        pricePerPackField.setBackground(Color.DARK_GRAY);
        pricePerPackField.setForeground(Color.WHITE);

        saveButton = new DarkButton("Save");
        cancelButton = new DarkButton("Cancel");

        
        saveButton.setHoverBackgroundColor(new Color(82, 113, 82)); 
        cancelButton.setHoverBackgroundColor(new Color(128, 52, 52)); 
    }

    private void loadProductData() {
        Product product = MealPlannerService.getInstance().getProducts().stream()
                .filter(p -> p.getId() == productId)
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
        contentPanel.setBackground(Color.DARK_GRAY); 

        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.DARK_GRAY); 
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Product Name:");
        nameLabel.setForeground(Color.WHITE); 
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(nameField, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        JLabel unitLabel = new JLabel("Unit:");
        unitLabel.setForeground(Color.WHITE); 
        formPanel.add(unitLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(unitComboBox, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel weightLabel = new JLabel("Weight per Pack:");
        weightLabel.setForeground(Color.WHITE); 
        formPanel.add(weightLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(weightPerPackField, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        JLabel priceLabel = new JLabel("Price per Pack (€):");
        priceLabel.setForeground(Color.WHITE); 
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(pricePerPackField, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.DARK_GRAY); 
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
                    try {
                        MealPlannerService.getInstance().modifyProduct(productId, getProductName(), getPricePerPack(), getWeightPerPack(), getUnit());
                        parentFrame.loadProducts();
                        dispose();
                    } catch (ProductNotFoundException ex) {
                        JOptionPane.showMessageDialog(ModifyProductFrame.this,
                                "Product not found: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
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
