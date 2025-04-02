package fr.oiha.mealplanner.gui.panel;

import fr.oiha.mealplanner.gui.component.CustomButton;
import fr.oiha.mealplanner.gui.frame.AddProductFrame;
import fr.oiha.mealplanner.gui.frame.ModifyProductFrame;
import fr.oiha.mealplanner.service.MealPlannerService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ProductPanel is a JPanel that displays a list of products in a table format.
 * It allows users to add, modify, and delete products.
 * It uses a JToolBar for action buttons and a JTable to display product information.
 * It also handles the loading of products from the MealPlannerService.
 */
public class ProductPanel extends JPanel {
    private JToolBar toolBar;
    private JButton addProductButton;
    private JButton modifyButton;
    private JButton deleteButton;
    private JScrollPane scrollPane;
    private JTable productTable;

    public ProductPanel() {
        initComponents();
        setupEventHandlers();
        setName("ProductPanel");
        loadProducts();
    }

    private void initComponents() {
        toolBar = new JToolBar();
        addProductButton = new CustomButton("Add a product");
        modifyButton = new CustomButton("Modify a product");
        deleteButton = new CustomButton("Delete a product");
        scrollPane = new JScrollPane();
        productTable = new JTable();

        setLayout(new BorderLayout());

        toolBar.setFloatable(false);
        addProductButton.setBackground(Color.DARK_GRAY);
        addProductButton.setForeground(Color.WHITE);
        modifyButton.setBackground(Color.DARK_GRAY);
        modifyButton.setForeground(Color.WHITE);
        deleteButton.setBackground(Color.DARK_GRAY);
        deleteButton.setForeground(Color.WHITE);
        toolBar.add(addProductButton);
        toolBar.add(modifyButton);
        toolBar.add(deleteButton);
        add(toolBar, BorderLayout.NORTH);

        productTable.setShowHorizontalLines(true);
        productTable.setShowVerticalLines(true);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.setRowSelectionAllowed(true);
        productTable.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "ID", "Product Name", "Price", "Unit"
                }
        ));

        TableColumnModel columnModel = productTable.getColumnModel();
        columnModel.getColumn(0).setMinWidth(0);
        columnModel.getColumn(0).setMaxWidth(0);
        columnModel.getColumn(0).setPreferredWidth(0);
        columnModel.getColumn(0).setResizable(false);

        scrollPane.setViewportView(productTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        addProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddProductFrame dialog = new AddProductFrame(ProductPanel.this);
                dialog.setVisible(true);
            }
        });

        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (productTable.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(ProductPanel.this, "Please select a product to modify");
                    return;
                }

                int productId = (Integer) productTable.getValueAt(productTable.getSelectedRow(), 0);
                ModifyProductFrame dialog = new ModifyProductFrame(ProductPanel.this, productId);
                dialog.setVisible(true);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (productTable.getSelectedRow() == -1) {
                    JOptionPane.showMessageDialog(ProductPanel.this, "Please select a product to delete");
                    return;
                }

                int productId = (Integer) productTable.getValueAt(productTable.getSelectedRow(), 0);
                MealPlannerService.getInstance().removeProduct(productId);
                loadProducts();
            }
        });
    }

    /**
     * Loads the products from the MealPlannerService and populates the product table.
     * It clears the existing rows in the table and adds new rows for each product.
     */
    public void loadProducts() {
        ((DefaultTableModel) productTable.getModel()).setRowCount(0);
        MealPlannerService.getInstance().getProducts().forEach(p -> {
            ((DefaultTableModel) productTable.getModel()).addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getPricePerPack(),
                    p.getUnit()
            });
        });
    }
}
