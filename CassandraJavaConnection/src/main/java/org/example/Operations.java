package org.example;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import java.util.Scanner;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

public class Operations {
    private static final Set<String> COMMON_STOCKS = Set.of(
            "AAPL", "MSFT", "GOOGL", "AMZN", "TSLA",
            "META", "NVDA", "JPM", "V", "WMT"
    );

    static {
        // Suppress driver debug logs
        Logger root = (Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
    }

    public static void main(String[] args) {
        CqlSession session = Connector.getSession();
        if (session == null) {
            System.err.println("Failed to connect to database");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            clearScreen();
            System.out.println("\n========== TRADING MENU ==========");
            System.out.println("1. BUY Stocks");
            System.out.println("2. SELL Stocks");
            System.out.println("3. SHOW Transactions");
            System.out.println("4. EXIT");
            System.out.println("=================================");
            System.out.print("Enter your choice (1-4): ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    processTransaction(session, scanner, "BUY");
                    break;

                case "2":
                    processTransaction(session, scanner, "SELL");
                    break;

                case "3":
                    showTransactions(session);
                    break;

                case "4":
                    System.out.println("Exiting program...");
                    session.close();
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please enter 1-4.");
                    pause(scanner);
            }
        }
    }

    private static void processTransaction(CqlSession session, Scanner scanner, String type) {
        clearScreen();
        System.out.println("\n=== " + type + " STOCKS ===");
        try {
            String symbol;
            while (true) {
                System.out.print("Enter stock symbol (e.g., AAPL, MSFT): ");
                symbol = scanner.nextLine().trim().toUpperCase();

                if (symbol.matches("[A-Z]{1,5}")) {
                    break;
                }
                System.out.println("Invalid symbol. Must be 1-5 uppercase letters.");
                System.out.println("Common symbols: " + String.join(", ", COMMON_STOCKS));
            }

            System.out.print("Enter quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter price per share: $");
            double price = Double.parseDouble(scanner.nextLine());

            session.execute(
                    "INSERT INTO transactions (id, stock_symbol, quantity, price_per_stock, transaction_type, timestamp) " +
                            "VALUES (?, ?, ?, ?, ?, toTimestamp(now()))",
                    UUID.randomUUID(), symbol, quantity, price, type
            );

            System.out.printf("\nSuccess! %s order placed for %d shares of %s at $%.2f%n",
                    type, quantity, symbol, price);
            pause(scanner);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
            pause(scanner);
        } catch (Exception e) {
            System.out.println("Error processing transaction: " + e.getMessage());
            pause(scanner);
        }
    }

    private static void showTransactions(CqlSession session) {
        clearScreen();
        System.out.println("\n=== TRANSACTION HISTORY ===");
        System.out.println("ID\t| Stock\t| Qty\t| Price\t| Type");
        System.out.println("-----------------------------------------");

        ResultSet result = session.execute("SELECT * FROM transactions");
        boolean hasTransactions = false;

        for (Row row : result) {
            hasTransactions = true;
            System.out.printf("%s\t| %s\t| %d\t| $%.2f\t| %s%n",
                    row.getUuid("id").toString().substring(0, 8),
                    row.getString("stock_symbol"),
                    row.getInt("quantity"),
                    row.getDouble("price_per_stock"),
                    row.getString("transaction_type"));
        }

        if (!hasTransactions) {
            System.out.println("No transactions found.");
        }
        pause(new Scanner(System.in));
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void pause(Scanner scanner) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}