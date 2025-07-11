package uk.infy.luna;

import com.formdev.flatlaf.*;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
//            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            Game game = new Game();
            game.init();
        });

    }
}
