package fr.oiha.mealplanner.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main GUI window for the Meal Planner application
 */
public class MainFrame {
    private JFrame frame;
    private JToolBar toolBar;
    private JButton mealButton;
    private JButton ingredientsButton;
    private JButton mealPlannerButton;
    private JPanel contentPanel;

    public MainFrame() {
        initComponents();
        setupEventHandlers();
        frame.setVisible(true);
    }

    private void initComponents() {
        // Initialize frame
        frame = new JFrame("Meal Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setLayout(new BorderLayout());

        // Initialize components
        toolBar = new JToolBar();
        mealButton = new JButton("Meal");
        ingredientsButton = new JButton("Products");
        mealPlannerButton = new JButton("Meal Planner");
        contentPanel = new MealPanel();

        // Setup toolbar
        toolBar.setFloatable(false);
        toolBar.add(mealButton);
        toolBar.add(ingredientsButton);
        toolBar.add(mealPlannerButton);

        // Add components to frame
        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(contentPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void setupEventHandlers() {
        mealButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!contentPanel.getName().equals("MealPanel")) {
                    frame.remove(contentPanel);
                    contentPanel = new MealPanel();
                    frame.add(contentPanel, BorderLayout.CENTER);
                    frame.validate();
                    frame.repaint();
                }
            }
        });

        ingredientsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!contentPanel.getName().equals("ProductPanel")) {
                    frame.remove(contentPanel);
                    contentPanel = new ProductPanel();
                    frame.add(contentPanel, BorderLayout.CENTER);
                    frame.validate();
                    frame.repaint();
                }
            }
        });

        mealPlannerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!contentPanel.getName().equals("MealPlanPanel")) {
                    frame.remove(contentPanel);
                    contentPanel = new MealPlanPanel();
                    frame.add(contentPanel, BorderLayout.CENTER);
                    frame.validate();
                    frame.repaint();
                }
            }
        });
    }
}