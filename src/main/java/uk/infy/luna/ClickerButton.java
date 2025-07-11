package uk.infy.luna;
import javax.swing.*;
import java.awt.*;
public class ClickerButton {
    private JButton button;
    public ClickerButton(Game game) {
        button = new JButton();
        button.setText("Click Me!");
        button.setFont(new Font("Arial", Font.BOLD, 32));
        button.setForeground(Color.WHITE);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        ImageIcon icon = new ImageIcon(getClass().getResource("/buttonlogo.png"));
        button.setIcon(icon);
        button.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                int width = button.getWidth();
                int height = button.getHeight();
                if (width > 0 && height > 0) {
                    Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    button.setIcon(new ImageIcon(scaled));
                }
            }
        });
        button.addActionListener(e -> {
            game.addCurrency(game.getClickValue());
        });
    }
    public JButton getButton() {
        return button;
    }
}
