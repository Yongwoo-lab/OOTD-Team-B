import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BusDatabase {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String ARRIVAL_CITY = "Incheon";

    public List<BusSchedule> loadSchedules() {
        Flight defaultFlight = new Flight(
                "DEFAULT",
                "Incheon International Airport (ICN)",
                "Tokyo Narita",
                "2026-05-20",
                "10:00",
                "12:20",
                0
        );
        return loadSchedules(defaultFlight);
    }

    public List<BusSchedule> loadSchedules(Flight flight) {
        List<BusSchedule> schedules = new ArrayList<>();

        String flightDate = flight == null ? "2026-05-20" : flight.getDate();
        String flightDepartureTime = flight == null ? "10:00" : flight.getDepartureTime();
        LocalTime busArrivalTime = parseTime(flightDepartureTime).minusHours(2);

        addSchedule(schedules, "BUS-SEO-INC-01", "Seoul", flightDate, busArrivalTime, 80, 18000);
        addSchedule(schedules, "BUS-BUS-INC-01", "Busan", flightDate, busArrivalTime, 280, 44000);
        addSchedule(schedules, "BUS-DAE-INC-01", "Daegu", flightDate, busArrivalTime, 220, 39000);
        addSchedule(schedules, "BUS-DJN-INC-01", "Daejeon", flightDate, busArrivalTime, 150, 31000);
        addSchedule(schedules, "BUS-GWA-INC-01", "Gwangju", flightDate, busArrivalTime, 240, 41000);
        addSchedule(schedules, "BUS-ULS-INC-01", "Ulsan", flightDate, busArrivalTime, 290, 45000);

        return schedules;
    }

    private void addSchedule(List<BusSchedule> schedules, String scheduleId, String departureCity,
                             String date, LocalTime arrivalTime, int durationMinutes, double fare) {
        LocalTime departureTime = arrivalTime.minusMinutes(durationMinutes);
        schedules.add(new BusSchedule(
                scheduleId,
                departureCity,
                ARRIVAL_CITY,
                date,
                departureTime.format(TIME_FORMATTER),
                arrivalTime.format(TIME_FORMATTER),
                "Premium Express",
                fare
        ));
    }

    private LocalTime parseTime(String timeText) {
        try {
            return LocalTime.parse(timeText, TIME_FORMATTER);
        } catch (RuntimeException e) {
            return LocalTime.of(10, 0);
        }
    }
}
