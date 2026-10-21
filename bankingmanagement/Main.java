package bankingmanagement;

import bankingmanagement.service.AccountService;
import bankingmanagement.service.CustomerService;
import bankingmanagement.service.TransactionService;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    static final String url = "jdbc:mysql://localhost:3306/bank_management";
    static final String user = "root";
    static final String pass = "root";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection con = DriverManager.getConnection(url, user, pass);
                 Scanner sc = new Scanner(System.in)) {

                createTables(con);

                CustomerService customerService = new CustomerService();
                AccountService accountService = new AccountService();
                TransactionService transactionService = new TransactionService();

                while (true) {
                    System.out.println("\n===== BANKING MANAGEMENT SYSTEM =====");
                    System.out.println("1. Customer Registration");
                    System.out.println("2. Account Creation");
                    System.out.println("3. Deposit");
                    System.out.println("4. Withdrawal");
                    System.out.println("5. Update Phone Number");
                    System.out.println("6. Update Account Status");
                    System.out.println("7. Transaction History");
                    System.out.println("8. Exit");
                    System.out.print("Enter your choice: ");

                    int choice = sc.nextInt();

                    try {
                        switch (choice) {

                            case 1:
                                customerService.customerRegistration(con, sc);
                                break;

                            case 2:
                                accountService.accountCreation(con, sc);
                                break;

                            case 3:
                                System.out.print("Enter account number: ");
                                String depositAccount = sc.next();
                                sc.nextLine();

                                System.out.print("Enter deposit amount: ");
                                java.math.BigDecimal depositAmount =
                                        new java.math.BigDecimal(sc.nextLine());

                                accountService.deposit(con, depositAccount, depositAmount);
                                break;

                            case 4:
                                System.out.print("Enter account number: ");
                                String withdrawalAccount = sc.next();
                                sc.nextLine();

                                System.out.print("Enter withdrawal amount: ");
                                BigDecimal withdrawalAmount = new BigDecimal(sc.nextLine());

                                accountService.withdrawal(con, withdrawalAccount, withdrawalAmount);
                                break;

                            case 5:
                                System.out.print("Enter customer ID: ");
                                int customerId = sc.nextInt();
                                sc.nextLine();

                                System.out.println("Enter new Phone number");
                                String newPhone = sc.nextLine();

                                customerService.updatePhone(con,customerId,newPhone);


                                break;
                            case 6:
                                System.out.print("Enter account number: ");
                                String accNumber = sc.next();
                                sc.nextLine();

                                System.out.println("Enter Status (ACTIVE | INACTIVE)");
                                String status = sc.nextLine();

                                accountService.updateStatus(con,accNumber,status);
                                break;


                            case 7:
                                System.out.print("Enter account number: ");
                                String historyAccount = sc.next();


                                transactionService.showTransactionHistory(con, historyAccount);
                                break;

                            case 8:
                                System.out.println("Thank you for using our banking system.");
                                return;

                            default:
                                System.out.println("Invalid choice.");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number or amount.");
                    } catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid input: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Operation failed: " + e.getMessage());
                    }
                }

            }

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Application error: " + e.getMessage());
        }
    }

    private static void createTables(Connection con) throws SQLException {

        String customerSQL = """
                CREATE TABLE IF NOT EXISTS customers (
                    customer_id INT PRIMARY KEY AUTO_INCREMENT,
                    customer_name VARCHAR(100) NOT NULL,
                    email VARCHAR(100) NOT NULL UNIQUE,
                    phone VARCHAR(10) NOT NULL UNIQUE,
                    address VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    CHECK (CHAR_LENGTH(phone) = 10)
                )
                """;

        String accountSQL = """
                CREATE TABLE IF NOT EXISTS accounts (
                    account_id INT PRIMARY KEY AUTO_INCREMENT,
                    customer_id INT NOT NULL,
                    account_number VARCHAR(20) NOT NULL UNIQUE,
                    account_type VARCHAR(20) NOT NULL,
                    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
                    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (customer_id)
                        REFERENCES customers(customer_id),
                    CHECK (balance >= 0),
                    CHECK (account_type IN ('SAVINGS', 'CURRENT', 'SALARY', 'FD')),
                    CHECK (status IN ('ACTIVE', 'INACTIVE'))
                )
                """;

        String transactionSQL = """
                CREATE TABLE IF NOT EXISTS transactions (
                    transaction_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                    account_id INT NOT NULL,
                    transaction_type VARCHAR(20) NOT NULL,
                    amount DECIMAL(15,2) NOT NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
                    description VARCHAR(255),
                    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (account_id)
                        REFERENCES accounts(account_id),
                    CHECK (amount > 0),
                    CHECK (transaction_type IN
                        ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER_IN', 'TRANSFER_OUT')),
                    CHECK (status IN ('SUCCESS', 'FAILED'))
                )
                """;

        createTable(con, customerSQL, "Customers");
        createTable(con, accountSQL, "Accounts");
        createTable(con, transactionSQL, "Transactions");
    }

    private static void createTable(
            Connection con, String sql, String tableName) throws SQLException {

        try (Statement statement = con.createStatement()) {
            statement.execute(sql);
            System.out.println(tableName + " table is ready.");
        }
    }
}


