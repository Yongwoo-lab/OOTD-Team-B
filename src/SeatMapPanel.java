import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SeatMapPanel extends JPanel {
    private final SeatService seatService;
    private final Consumer<String> onSeatSelected;
    private final Map<String, JButton> seatButtons = new HashMap<>();

    public SeatMapPanel(SeatService seatService, Consumer<String> onSeatSelected) {
        this.seatService = seatService;
        this.onSeatSelected = onSeatSelected;

        setLayout(new BorderLayout(12, 12));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDER),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        add(createLegendPanel(), BorderLayout.NORTH);
        add(createSeatGridPanel(), BorderLayout.CENTER);
    }

    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendItem("Available", Color.WHITE, AppTheme.BLUE));
        legendPanel.add(createLegendItem("Selected", AppTheme.BLUE, Color.WHITE));
        legendPanel.add(createLegendItem("Occupied", new Color(226, 232, 240), AppTheme.MUTED));
        return legendPanel;
    }

    private JPanel createLegendItem(String text, Color background, Color foreground) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        itemPanel.setOpaque(false);

        JLabel swatch = new JLabel("  ");
        swatch.setOpaque(true);
        swatch.setBackground(background);
        swatch.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));

        JLabel label = new JLabel(text);
        label.setForeground(foreground);
        label.setFont(new Font("Arial", Font.BOLD, 12));

        itemPanel.add(swatch);
        itemPanel.add(label);
        return itemPanel;
    }

    private JPanel createSeatGridPanel() {
        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.NONE;

        addColumnHeaders(gridPanel, gbc);
        String[] columns = seatService.getColumns();
        int aisleIndex = seatService.getAisleIndex();

        for (int row = 1; row <= seatService.getRowCount(); row++) {
            gbc.gridy = row;
            gbc.gridx = 0;
            gridPanel.add(createRowLabel(row), gbc);

            int gridX = 1;
            for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
                if (columnIndex == aisleIndex) {
                    gbc.gridx = gridX++;
                    gridPanel.add(createAisleSpacer(), gbc);
                }

                Seat seat = seatService.findSeat(row + columns[columnIndex]);
                JButton seatButton = createSeatButton(seat);
                if (seat != null) {
                    seatButtons.put(seat.getSeatNumber(), seatButton);
                }
                gbc.gridx = gridX++;
                gridPanel.add(seatButton, gbc);
            }
        }

        return gridPanel;
    }

    private void addColumnHeaders(JPanel gridPanel, GridBagConstraints gbc) {
        String[] columns = seatService.getColumns();
        int aisleIndex = seatService.getAisleIndex();

        gbc.gridy = 0;
        gbc.gridx = 0;
        gridPanel.add(createHeaderLabel("Row"), gbc);

        int gridX = 1;
        for (int columnIndex = 0; columnIndex < columns.length; columnIndex++) {
            if (columnIndex == aisleIndex) {
                gbc.gridx = gridX++;
                gridPanel.add(createHeaderLabel("Aisle"), gbc);
            }
            gbc.gridx = gridX++;
            gridPanel.add(createHeaderLabel(columns[columnIndex]), gbc);
        }
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setPreferredSize(new Dimension(54, 24));
        label.setForeground(AppTheme.MUTED);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        return label;
    }

    private JLabel createRowLabel(int row) {
        JLabel label = new JLabel(String.valueOf(row), JLabel.CENTER);
        label.setPreferredSize(new Dimension(54, 32));
        label.setForeground(AppTheme.NAVY);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    private Component createAisleSpacer() {
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        spacer.setPreferredSize(new Dimension(30, 32));
        return spacer;
    }

    private JButton createSeatButton(Seat seat) {
        JButton button = new JButton(seat == null ? "-" : seat.getSeatNumber());
        button.setPreferredSize(new Dimension(54, 32));
        button.setMinimumSize(new Dimension(54, 32));
        button.setMaximumSize(new Dimension(54, 32));
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 11));

        if (seat == null) {
            button.setEnabled(false);
            return button;
        }

        button.addActionListener(e -> handleSeatClick(seat.getSeatNumber()));
        applySeatStyle(button, seat);
        return button;
    }

    private void handleSeatClick(String seatNumber) {
        Seat seat = seatService.findSeat(seatNumber);
        if (seat == null || seat.isOccupied()) {
            JOptionPane.showMessageDialog(this, "This seat is not available.");
            return;
        }

        if (!seatService.selectSeat(seatNumber)) {
            JOptionPane.showMessageDialog(this, "This seat cannot be selected.");
            return;
        }

        refreshSeatButtons();
        if (onSeatSelected != null) {
            onSeatSelected.accept(seatNumber);
        }
    }

    private void refreshSeatButtons() {
        for (Seat seat : seatService.getSeats()) {
            JButton button = seatButtons.get(seat.getSeatNumber());
            if (button != null) {
                applySeatStyle(button, seat);
            }
        }
    }

    private void applySeatStyle(JButton button, Seat seat) {
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(createSeatBorder(seat));
        button.setEnabled(!seat.isOccupied());

        if (seat.isSelected()) {
            button.setBackground(AppTheme.BLUE);
            button.setForeground(Color.WHITE);
            return;
        }
        if (seat.isOccupied()) {
            button.setBackground(new Color(226, 232, 240));
            button.setForeground(AppTheme.MUTED);
            return;
        }
        button.setBackground(Color.WHITE);
        button.setForeground(AppTheme.BLUE);
    }

    private Border createSeatBorder(Seat seat) {
        Color borderColor = seat.isSelected() ? AppTheme.BLUE : AppTheme.BORDER;
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        );
    }
}
