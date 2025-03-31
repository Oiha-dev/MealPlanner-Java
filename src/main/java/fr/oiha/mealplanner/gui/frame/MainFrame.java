package fr.oiha.mealplanner.gui.frame;

import fr.oiha.mealplanner.gui.panel.MealPanel;
import fr.oiha.mealplanner.gui.panel.MealPlanPanel;
import fr.oiha.mealplanner.gui.panel.ProductPanel;
import fr.oiha.mealplanner.gui.component.DarkButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MainFrame {
    private JFrame frame;
    private JToolBar toolBar;
    private DarkButton mealButton;
    private DarkButton ingredientsButton;
    private DarkButton mealPlannerButton;
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

        mealButton = new DarkButton("Meal");
        ingredientsButton = new DarkButton("Products");
        mealPlannerButton = new DarkButton("Meal Planner");
        contentPanel = new MealPanel();

        toolBar.setFloatable(false);
        toolBar.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        toolBar.add(mealButton);
        toolBar.add(ingredientsButton);
        toolBar.add(mealPlannerButton);

        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(contentPanel, BorderLayout.CENTER);

        frame.getContentPane().setBackground(Color.DARK_GRAY);
        toolBar.setBackground(Color.DARK_GRAY);

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
