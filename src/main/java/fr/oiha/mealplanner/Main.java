package main.java.fr.oiha.mealplanner;

import main.java.fr.oiha.mealplanner.gui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame gui = new MainFrame();
        });
    }
}
