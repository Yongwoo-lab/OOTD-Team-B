public class SkyPassMember extends Customer {
    private int mileage;

    public SkyPassMember(String customerId, String name, String email, String password, int mileage) {
        super(customerId, name, email, password);
        this.mileage = mileage;
    }

    public int getMileage() {
        return mileage;
    }

    public void earnMileage(int amount) {
        if (amount > 0) {
            mileage += amount;
        }
    }

    public boolean useMileage(int amount) {
        if (amount > 0 && mileage >= amount) {
            mileage -= amount;
            return true;
        }
        return false;
    }

    @Override
    public String getUserType() {
        return "SkyPass Member";
    }
}