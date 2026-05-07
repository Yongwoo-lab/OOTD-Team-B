public class MileageService {
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
}
