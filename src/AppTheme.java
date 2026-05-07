import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class AppTheme {
    public static final Color NAVY = new Color(0, 35, 91);
    public static final Color BLUE = new Color(0, 78, 152);
    public static final Color LIGHT_BLUE = new Color(232, 241, 252);
    public static final Color BACKGROUND = new Color(246, 248, 252);
    public static final Color BORDER = new Color(218, 225, 235);
    public static final Color TEXT = new Color(30, 41, 59);
    public static final Color MUTED = new Color(100, 116, 139);
    public static final Color RED = new Color(204, 36, 48);

    public static JPanel createPagePanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 16));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));
        return panel;
    }

    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        return panel;
    }

    public static JLabel createTitle(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setForeground(NAVY);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        return label;
    }

    public static JLabel createSubtitle(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setForeground(MUTED);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        return label;
    }

    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBorder(buttonBorder(BLUE));
        return button;
    }

    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(NAVY);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBorder(buttonBorder(BORDER));
        return button;
    }

    public static JLabel createBadge(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setOpaque(true);
        label.setBackground(LIGHT_BLUE);
        label.setForeground(NAVY);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 211, 238)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return label;
    }

    public static JButton createTextButton(String text) {
        JButton button = new JButton(text);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setForeground(BLUE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        return button;
    }

    public static void styleField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 13));
        field.setForeground(TEXT);
        field.setBackground(Color.WHITE);
        field.setCaretColor(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)
        ));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        comboBox.setForeground(TEXT);
        comboBox.setBackground(Color.WHITE);
    }

    private static Border buttonBorder(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)
        );
    }
}
