import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthService authService = new AuthService();
            Customer currentUser = null;

            new SearchFlightFrame(authService, currentUser);
        });
    }
}