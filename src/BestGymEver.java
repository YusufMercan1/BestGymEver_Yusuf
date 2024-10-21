import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BestGymEver {

    //Exakta filvägen
    private static final String CUSTOMER_FILE = "/Users/yusufmercan/Desktop/JavaMapp/BestGymEver/src/Medlemmar.txt";
    private static final String TRAINER_FILE = "/Users/yusufmercan/Desktop/JavaMapp/BestGymEver/src/PtLogg.txt"; // Absolut sökväg till PtLogg.txt
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Ange kundens namn eller personnummer:");
        String input = scanner.nextLine();

        try {
            // Läser in filen med medlemsinformation
            List<String> customerData = Files.readAllLines(Paths.get(CUSTOMER_FILE));
            String[] customerInfo = findCustomer(customerData, input);

            if (customerInfo != null) {
                String personnummer = customerInfo[0];
                String namn = customerInfo[1];
                LocalDate betalningsdatum = LocalDate.parse(customerInfo[2], DATE_FORMATTER);
                LocalDate ettArSedan = LocalDate.now().minusYears(1);

                // Kontrollerar om betalningen är inom det senaste året
                if (betalningsdatum.isAfter(ettArSedan) || betalningsdatum.isEqual(ettArSedan)) {
                    System.out.println("Kunden är en nuvarande medlem.");
                    logCustomerVisit(personnummer, namn, LocalDate.now());  // Loggar besöket om medlemmen är nuvarande
                } else {
                    System.out.println("Kunden är en före detta medlem.");
                }
            } else {
                System.out.println("Personen finns inte i filen och är obehörig.");
            }

        } catch (IOException e) {
            System.err.println("Ett fel uppstod när filen lästes: " + e.getMessage());
        }
    }

    //För att hitta kunden med hjälp av namn/personnummer
    private static String[] findCustomer(List<String> customerData, String input) {
        for (int i = 0; i < customerData.size(); i += 2) {  // Gå igenom var andra rad
            String[] customerDetails = customerData.get(i).split(", ");
            String personnummer = customerDetails[0];
            String namn = customerDetails[1];
            String betalningsdatum = customerData.get(i + 1);

            if (personnummer.equals(input) || namn.equalsIgnoreCase(input)) {
                return new String[]{personnummer, namn, betalningsdatum};
            }
        }
        return null;
    }

    // logga kundens besök i pt:s loggfil
    private static void logCustomerVisit(String personnummer, String namn, LocalDate datum) {
        String logEntry = personnummer + ", " + namn + ", " + datum.format(DATE_FORMATTER) + "\n";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRAINER_FILE, true))) {
            writer.write(logEntry);
            System.out.println("Kundens träningsbesök loggades i PT-loggen.");
        } catch (IOException e) {
            System.err.println("Ett fel uppstod när besöket loggades: " + e.getMessage());
        }
    }
}
