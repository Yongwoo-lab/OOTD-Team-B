public class MileageService {
    private static final double EARN_RATE = 0.10;

    public int getCurrentMileage(Customer user) {
        if (user instanceof SkyPassMember) {
            SkyPassMember member = (SkyPassMember) user;
            return member.getMileage();
        }
        return 0;
    }

    public String getMileageMessage(Customer user) {
        if (user instanceof SkyPassMember) {
            return String.format("%,d miles", getCurrentMileage(user));
        }
        return "SkyPass members only";
    }

    public boolean canUseMileage(Customer user, int mileageToUse, double flightFare) {
        if (!(user instanceof SkyPassMember)) {
            return mileageToUse == 0;
        }
        if (mileageToUse < 0) {
            return false;
        }
        if (mileageToUse == 0) {
            return true;
        }
        return mileageToUse <= getCurrentMileage(user) && mileageToUse <= flightFare;
    }

    public double calculateFlightDiscount(Customer user, int mileageToUse, double flightFare) {
        if (!canUseMileage(user, mileageToUse, flightFare)) {
            return 0;
        }
        return Math.min(mileageToUse, flightFare);
    }

    public double calculateFinalAmount(Reservation reservation, int mileageToUse) {
        if (reservation == null) {
            return 0;
        }
        double discount = calculateFlightDiscount(
                reservation.getCustomer(),
                mileageToUse,
                reservation.getFlightFare()
        );
        return reservation.getFlightFare() - discount + reservation.getBusFare();
    }

    public boolean useMileage(Customer user, int mileageToUse, AuthService authService) {
        if (mileageToUse == 0) {
            return true;
        }
        if (!(user instanceof SkyPassMember)) {
            return false;
        }
        boolean used = ((SkyPassMember) user).useMileage(mileageToUse);
        if (used && authService != null) {
            authService.saveCustomerData();
        }
        return used;
    }

    public void restoreMileage(Customer user, int mileageToRestore, AuthService authService) {
        if (mileageToRestore <= 0 || !(user instanceof SkyPassMember)) {
            return;
        }
        ((SkyPassMember) user).earnMileage(mileageToRestore);
        if (authService != null) {
            authService.saveCustomerData();
        }
    }

    public int calculateEarnedMileage(double flightFare) {
        if (flightFare <= 0) {
            return 0;
        }
        return (int) Math.floor(flightFare * EARN_RATE);
    }

    public void earnFlightMileage(Customer user, double flightFare, AuthService authService) {
        if (!(user instanceof SkyPassMember)) {
            return;
        }
        int earnedMileage = calculateEarnedMileage(flightFare);
        if (earnedMileage <= 0) {
            return;
        }
        ((SkyPassMember) user).earnMileage(earnedMileage);
        if (authService != null) {
            authService.saveCustomerData();
        }
    }
}
