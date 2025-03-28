package fr.oiha.mealplanner.gui;

import fr.oiha.mealplanner.service.MealPlannerService;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel for displaying and managing products/ingredients
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
        // Initialize components
        toolBar = new JToolBar();
        addProductButton = new JButton("Add a product");
        modifyButton = new JButton("Modify a product");
        deleteButton = new JButton("Delete a product");
        scrollPane = new JScrollPane();
        productTable = new JTable();

        // Setup panel
        setLayout(new BorderLayout());

        // Setup toolbar
        toolBar.setFloatable(false);
        toolBar.add(addProductButton);
        toolBar.add(modifyButton);
        toolBar.add(deleteButton);
        add(toolBar, BorderLayout.NORTH);

        // Setup table
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
        
        // Cacher la colonne ID
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
                
                // Récupérer l'ID réel du produit depuis la colonne cachée
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
                
                // Récupérer l'ID réel du produit depuis la colonne cachée
                int productId = (Integer) productTable.getValueAt(productTable.getSelectedRow(), 0);
                MealPlannerService.getInstance().removeProduct(productId);
                loadProducts();
            }
        });
    }

    public JTable getProductTable() {
        return productTable;
    }

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
