import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FlightDatabase {
    private static final String DATA_DIR = "data";
    private static final String FLIGHT_FILE = DATA_DIR + File.separator + "flights.txt";

    public List<Flight> loadFlights() {
        ensureDatabaseExists();

        List<Flight> flights = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FLIGHT_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Flight flight = parseFlight(line);
                if (flight != null) {
                    flights.add(flight);
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load flight database: " + e.getMessage());
        }

        return flights;
    }

    private Flight parseFlight(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split("\\|", -1);
        if (parts.length != 7) {
            return null;
        }

        return new Flight(
                parts[0].trim(),
                parts[1].trim(),
                parts[2].trim(),
                parts[3].trim(),
                parts[4].trim(),
                parts[5].trim(),
                parsePrice(parts[6])
        );
    }

    private double parsePrice(String priceText) {
        try {
            return Double.parseDouble(priceText.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void ensureDatabaseExists() {
        ensureDataDirectoryExists();

        File file = new File(FLIGHT_FILE);
        if (file.exists()) {
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(FLIGHT_FILE))) {
            writer.println("KE701|Incheon International Airport (ICN)|Tokyo Narita|2026-05-20|07:30|09:50|280000");
            writer.println("KE703|Incheon International Airport (ICN)|Tokyo Narita|2026-05-20|14:15|16:35|295000");
            writer.println("KE705|Incheon International Airport (ICN)|Tokyo Haneda|2026-05-21|09:10|11:25|315000");
            writer.println("KE711|Incheon International Airport (ICN)|Osaka Kansai|2026-05-21|08:05|09:55|260000");
            writer.println("KE713|Incheon International Airport (ICN)|Osaka Kansai|2026-05-22|16:40|18:30|275000");
            writer.println("KE081|Incheon International Airport (ICN)|New York JFK|2026-05-22|10:00|11:20|1320000");
            writer.println("KE085|Incheon International Airport (ICN)|New York JFK|2026-05-24|19:30|20:50|1280000");
            writer.println("KE017|Incheon International Airport (ICN)|Los Angeles|2026-05-23|14:30|09:50|1180000");
            writer.println("KE011|Incheon International Airport (ICN)|Los Angeles|2026-05-25|19:40|15:00|1210000");
            writer.println("KE901|Incheon International Airport (ICN)|Paris Charles de Gaulle|2026-05-23|11:10|18:30|1120000");
            writer.println("KE903|Incheon International Airport (ICN)|Paris Charles de Gaulle|2026-05-26|20:30|03:50|1080000");
            writer.println("KE907|Incheon International Airport (ICN)|London Heathrow|2026-05-24|12:50|18:35|1160000");
            writer.println("KE951|Incheon International Airport (ICN)|Dubai|2026-05-24|13:25|18:55|820000");
            writer.println("KE647|Incheon International Airport (ICN)|Singapore Changi|2026-05-25|23:10|04:45|560000");
            writer.println("KE651|Incheon International Airport (ICN)|Bangkok Suvarnabhumi|2026-05-25|17:20|21:15|430000");
            writer.println("KE657|Incheon International Airport (ICN)|Bangkok Suvarnabhumi|2026-05-26|09:30|13:25|410000");
            writer.println("KE475|Incheon International Airport (ICN)|Ho Chi Minh City|2026-05-26|18:40|22:10|390000");
            writer.println("KE621|Incheon International Airport (ICN)|Manila|2026-05-27|07:45|10:50|350000");
            writer.println("KE401|Incheon International Airport (ICN)|Sydney|2026-05-27|18:55|06:15|940000");
            writer.println("KE411|Incheon International Airport (ICN)|Auckland|2026-05-28|17:45|08:10|1050000");
        } catch (IOException e) {
            System.out.println("Failed to create flight database: " + e.getMessage());
        }
    }

    private void ensureDataDirectoryExists() {
        File dataDirectory = new File(DATA_DIR);
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
        }
    }
}
