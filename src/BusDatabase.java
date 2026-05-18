import java.util.ArrayList;
import java.util.List;

public class BusDatabase {
    public List<BusSchedule> loadSchedules() {
        List<BusSchedule> schedules = new ArrayList<>();
        schedules.add(new BusSchedule("BUS-SEO-BUS-01", "Seoul", "Busan", "2026-05-20", "08:00", "12:10", "Premium Express", 42000));
        schedules.add(new BusSchedule("BUS-SEO-DAE-01", "Seoul", "Daegu", "2026-05-20", "09:30", "13:00", "Premium Express", 36000));
        schedules.add(new BusSchedule("BUS-SEO-GWA-01", "Seoul", "Gwangju", "2026-05-21", "10:00", "13:40", "Premium Express", 39000));
        schedules.add(new BusSchedule("BUS-INC-DAE-01", "Incheon", "Daejeon", "2026-05-21", "11:20", "13:50", "Premium Express", 31000));
        schedules.add(new BusSchedule("BUS-BUS-GWA-01", "Busan", "Gwangju", "2026-05-22", "13:00", "16:10", "Premium Express", 35000));
        schedules.add(new BusSchedule("BUS-DAE-BUS-01", "Daejeon", "Busan", "2026-05-22", "15:30", "18:50", "Premium Express", 37000));
        schedules.add(new BusSchedule("BUS-GWA-INC-01", "Gwangju", "Incheon", "2026-05-23", "07:40", "11:50", "Premium Express", 41000));
        schedules.add(new BusSchedule("BUS-DAE-SEO-01", "Daegu", "Seoul", "2026-05-23", "16:30", "20:00", "Premium Express", 36000));
        return schedules;
    }
}
