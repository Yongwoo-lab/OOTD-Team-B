public class SeatLayoutStrategyFactory {
    public SeatLayoutStrategy createStrategy(Flight flight) {
        String arrival = flight == null ? "" : flight.getArrival();

        if (isSmallAircraftDestination(arrival)) {
            return new SmallAirplaneSeatLayoutStrategy();
        }
        if (isMediumAircraftDestination(arrival)) {
            return new MediumAirplaneSeatLayoutStrategy();
        }
        if (isLargeAircraftDestination(arrival)) {
            return new LargeAirplaneSeatLayoutStrategy();
        }
        return new MediumAirplaneSeatLayoutStrategy();
    }

    private boolean isSmallAircraftDestination(String arrival) {
        return containsAny(arrival, "Tokyo", "Osaka");
    }

    private boolean isMediumAircraftDestination(String arrival) {
        return containsAny(arrival, "Manila", "Ho Chi Minh", "Bangkok", "Singapore");
    }

    private boolean isLargeAircraftDestination(String arrival) {
        return containsAny(arrival,
                "Dubai",
                "Sydney",
                "Auckland",
                "New York",
                "Los Angeles",
                "Paris",
                "London");
    }

    private boolean containsAny(String text, String... keywords) {
        if (text == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
