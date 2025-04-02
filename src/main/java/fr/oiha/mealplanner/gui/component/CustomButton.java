package fr.oiha.mealplanner.gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Custom JButton with a hover effect.
 * This button changes its background and text color on hover.
 */
public class CustomButton extends JButton {
    private Color hoverBackgroundColor = Color.LIGHT_GRAY;
    private Color originalBackgroundColor = Color.WHITE;

    public CustomButton(String text) {
        super(text);
        initButton();
    }

    private void initButton() {
        setBackground(originalBackgroundColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Arial", Font.BOLD, 14));
        setPreferredSize(new Dimension(120, 30));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverBackgroundColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(originalBackgroundColor);
            }
        });
    }
    
    public void setHoverBackgroundColor(Color hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }
}
