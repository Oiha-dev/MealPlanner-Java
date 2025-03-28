package fr.oiha.mealplanner;

import fr.oiha.mealplanner.gui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame gui = new MainFrame();
        });
    }
}
