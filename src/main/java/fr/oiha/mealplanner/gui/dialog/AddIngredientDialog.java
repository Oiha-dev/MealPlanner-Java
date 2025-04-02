package fr.oiha.mealplanner.gui.dialog;

import fr.oiha.mealplanner.model.Product;
import fr.oiha.mealplanner.service.MealPlannerService;
import fr.oiha.mealplanner.gui.component.CustomButton;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Set;

/**
 * AddIngredientDialog is a JDialog that allows the user to add an ingredient to a meal.
 * It contains a JComboBox for selecting a product,
 * a JFormattedTextField for entering the quantity,
 * and buttons to add or cancel the action.
 * It validates the input and updates the table model with the new ingredient.
 */
public class AddIngredientDialog extends JDialog {

    public AddIngredientDialog(JFrame parent, DefaultTableModel tableModel) {
        super(parent, "Add Ingredient", true);
        setSize(350, 200);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.DARK_GRAY);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        Set<Product> products = MealPlannerService.getInstance().getProducts();
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "No products available. Please add products first.",
                    "No Products",
                    JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        JComboBox<Product> productComboBox = new JComboBox<>();
        for (Product product : products) {
            productComboBox.addItem(product);
        }

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel productLabel = new JLabel("Product:");
        panel.add(productLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(productComboBox, gbc);

        JFormattedTextField quantityField = new JFormattedTextField(NumberFormat.getNumberInstance());
        quantityField.setValue(1.0);
        quantityField.setColumns(10);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        JLabel quantityLabel = new JLabel("Quantity:");
        panel.add(quantityLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(quantityField, gbc);

        add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        CustomButton addButton = new CustomButton("Add");
        CustomButton cancelButton = new CustomButton("Cancel");

        addButton.setHoverBackgroundColor(Color.LIGHT_GRAY);
        cancelButton.setHoverBackgroundColor(Color.LIGHT_GRAY);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    double quantity = ((Number) quantityField.getValue()).doubleValue();
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(AddIngredientDialog.this,
                                "Quantity must be greater than zero",
                                "Invalid Quantity",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Product selectedProduct = (Product) productComboBox.getSelectedItem();
                    if (selectedProduct != null) {
                        tableModel.addRow(new Object[]{
                                selectedProduct,
                                quantity,
                                selectedProduct.getUnit()
                        });
                        dispose();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AddIngredientDialog.this,
                            "Please enter a valid quantity",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(addButton);
    }
}
