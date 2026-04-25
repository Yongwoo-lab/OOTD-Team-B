public class Guest extends Customer {
    public Guest() {
        super("GUEST", "Guest", "guest", "");
    }

    @Override
    public String getUserType() {
        return "Guest";
    }
}