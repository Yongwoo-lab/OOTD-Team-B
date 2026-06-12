public class MileageService {
    private static final double EARN_RATE = 0.10;
    private final MemberFareDiscountService memberFareDiscountService = new MemberFareDiscountService();

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
        double payableFlightFare = calculateDiscountedFlightFare(user, flightFare);
        return canUseMileageAgainstPayableFlightFare(user, mileageToUse, payableFlightFare);
    }

    private boolean canUseMileageAgainstPayableFlightFare(Customer user, int mileageToUse, double payableFlightFare) {
        if (!(user instanceof SkyPassMember)) {
            return mileageToUse == 0;
        }
        if (mileageToUse < 0) {
            return false;
        }
        if (mileageToUse == 0) {
            return true;
        }
        return mileageToUse <= getCurrentMileage(user) && mileageToUse <= payableFlightFare;
    }

    public double calculateFlightDiscount(Customer user, int mileageToUse, double flightFare) {
        double payableFlightFare = calculateDiscountedFlightFare(user, flightFare);
        if (!canUseMileageAgainstPayableFlightFare(user, mileageToUse, payableFlightFare)) {
            return 0;
        }
        return Math.min(mileageToUse, payableFlightFare);
    }

    public double calculateFinalAmount(Reservation reservation, int mileageToUse) {
        if (reservation == null) {
            return 0;
        }
        FlightFare payableFlightFare = createPayableFlightFare(
                reservation.getCustomer(),
                reservation.getFlightFare(),
                mileageToUse
        );
        return payableFlightFare.getAmount() + reservation.getBusFare();
    }

    public double calculateDiscountedFlightFare(Customer user, double flightFare) {
        return memberFareDiscountService.calculateDiscountedFlightFare(user, flightFare);
    }

    public FlightFare createPayableFlightFare(Customer user, double flightFare, int mileageToUse) {
        FlightFare fare = memberFareDiscountService.createFare(user, flightFare);
        if (mileageToUse <= 0) {
            return fare;
        }
        return new MileageFlightFareDecorator(fare, mileageToUse);
    }

    public double calculateMemberDiscount(Customer user, double flightFare) {
        return memberFareDiscountService.calculateMemberDiscount(user, flightFare);
    }

    public String getMemberDiscountMessage(Customer user, double flightFare) {
        return memberFareDiscountService.getDiscountMessage(user, flightFare);
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

    public void revokeEarnedMileage(Customer user, double paidFlightFare, AuthService authService) {
        if (!(user instanceof SkyPassMember)) {
            return;
        }
        int earnedMileage = calculateEarnedMileage(paidFlightFare);
        if (earnedMileage <= 0) {
            return;
        }
        ((SkyPassMember) user).revokeMileage(earnedMileage);
        if (authService != null) {
            authService.saveCustomerData();
        }
    }

    public int calculateEarnedMileageForPayment(Reservation reservation, Payment payment) {
        if (reservation == null || payment == null || !payment.isSuccess()) {
            return 0;
        }
        double paidFlightFare = Math.max(0, reservation.getFlightFare() - payment.getDiscountAmount());
        return calculateEarnedMileage(paidFlightFare);
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
