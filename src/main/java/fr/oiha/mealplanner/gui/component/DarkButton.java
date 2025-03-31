package fr.oiha.mealplanner.gui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DarkButton extends JButton {
    private Color hoverBackgroundColor = Color.GRAY;
    private Color originalBackgroundColor = Color.DARK_GRAY;
    private Color hoverTextColor = Color.WHITE;
    private Color originalTextColor = Color.WHITE;

    public DarkButton(String text) {
        super(text);
        initButton();
    }

    private void initButton() {
        setBackground(originalBackgroundColor);
        setForeground(originalTextColor);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Arial", Font.BOLD, 14));
        setPreferredSize(new Dimension(120, 30));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverBackgroundColor);
                setForeground(hoverTextColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(originalBackgroundColor);
                setForeground(originalTextColor);
            }
        });
    }
    
    public void setHoverBackgroundColor(Color hoverBackgroundColor) {
        this.hoverBackgroundColor = hoverBackgroundColor;
    }
}
