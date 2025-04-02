package fr.oiha.mealplanner.gui.frame;

import fr.oiha.mealplanner.gui.panel.MealPanel;
import fr.oiha.mealplanner.gui.panel.MealPlanPanel;
import fr.oiha.mealplanner.gui.panel.ProductPanel;
import fr.oiha.mealplanner.gui.component.CustomButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MainFrame class that serves as the main window for the Meal Planner application.
 * It contains a toolbar with buttons to navigate between different panels:
 * Meal, Products, and Meal Planner.
 * and manages the content displayed in the center panel.
 * @see MealPanel
 * @see ProductPanel
 * @see MealPlanPanel
 */
public class MainFrame {
    private JFrame frame;
    private JToolBar toolBar;
    private CustomButton mealButton;
    private CustomButton ingredientsButton;
    private CustomButton mealPlannerButton;
    private JPanel contentPanel;

    public MainFrame() {
        initComponents();
        setupEventHandlers();
        frame.setVisible(true);
    }

    private void initComponents() {
        frame = new JFrame("Meal Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(850, 600));
        frame.setLayout(new BorderLayout());

        toolBar = new JToolBar();

        mealButton = new CustomButton("Meal");
        ingredientsButton = new CustomButton("Products");
        mealPlannerButton = new CustomButton("Meal Planner");
        contentPanel = new MealPanel();

        toolBar.setFloatable(false);
        toolBar.add(mealButton);
        toolBar.add(ingredientsButton);
        toolBar.add(mealPlannerButton);

        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(contentPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
    }

    private void setupEventHandlers() {
        // This method is called when the meal button is clicked
        // It checks if the current content panel is not already the MealPanel
        // If not, it removes the current content panel and adds a new MealPanel
        // Then it validates and repaints the frame to reflect the changes
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

        // This method is called when the ingredients button is clicked
        // It checks if the current content panel is not already the ProductPanel
        // If not, it removes the current content panel and adds a new ProductPanel
        // Then it validates and repaints the frame to reflect the changes
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

        // This method is called when the meal planner button is clicked
        // It checks if the current content panel is not already the MealPlanPanel
        // If not, it removes the current content panel and adds a new MealPlanPanel
        // Then it validates and repaints the frame to reflect the changes
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
