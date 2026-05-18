public class AuthorizationService {
    public boolean canSearchFlights(Customer user) {
        return user != null;
    }

    public boolean canBookFlight(Customer user) {
        return user != null && !(user instanceof Guest);
    }

    public boolean canUseMileage(Customer user) {
        return user instanceof SkyPassMember;
    }

    public boolean canManageReservations(Customer user) {
        return user != null && !(user instanceof Guest);
    }

    public boolean canPurchaseBusTicket(Customer user) {
        return canBookFlight(user);
    }

    public boolean ownsReservation(Customer user, Reservation reservation) {
        return user != null
                && reservation != null
                && reservation.getCustomer() != null
                && user.getCustomerId().equals(reservation.getCustomer().getCustomerId());
    }
}
