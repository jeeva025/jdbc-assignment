package bankingmanagement;

import bankingmanagement.service.AccountService;
import bankingmanagement.service.CustomerService;
import bankingmanagement.service.TransactionService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class Main {

    static final String DB_URL = "jdbc:mysql://localhost:3306/bank_management";
    static final String USER = "root";
    static final String PASS = "root";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Scanner sc = new Scanner(System.in);
            

            CustomerService custService = new CustomerService();
            AccountService accService = new AccountService();
            TransactionService txnService = new TransactionService();

            boolean flag = true;
            
            while (flag) {
                System.out.println("\n---------------BANKING MANAGEMENT SYSTEM -----------------");
                System.out.println("1. Customer Registration");
                System.out.println("2. Account Creation");
                System.out.println("3. Deposit");
                System.out.println("4. Withdrawal");
                System.out.println("5. Update Phone Number");
                System.out.println("6. Update Account Status");
                System.out.println("7. Transaction History");
                System.out.println("8. Exit");
                System.out.print("Enter your choice: ");

                String input = sc.nextLine();
                int choice;
                
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                    continue;
                }

                try {
                    switch (choice) {
                        case 1:
                            custService.registerCustomer(con, sc);
                            break;
                        case 2:
                            accService.createAccount(con, sc);
                            break;
                        case 3:
                            System.out.print("Enter account number: ");
                            String depAcc = sc.nextLine();

                            System.out.print("Enter deposit amount: ");
                            double depAmt = Double.parseDouble(sc.nextLine());

                            accService.deposit(con, depAcc, depAmt);
                            break;
                        case 4:
                            System.out.print("Enter account number: ");
                            String withAcc = sc.nextLine();

                            System.out.print("Enter withdrawal amount: ");
                            double withAmt = Double.parseDouble(sc.nextLine());

                            accService.withdrawal(con, withAcc, withAmt);
                            break;
                        case 5:
                            System.out.print("Enter customer ID: ");
                            int custId = Integer.parseInt(sc.nextLine());

                            System.out.print("Enter new Phone number: ");
                            String newPhone = sc.nextLine();

                            custService.updatePhone(con, custId, newPhone);
                            break;
                        case 6:
                            System.out.print("Enter account number: ");
                            String accNo = sc.nextLine();

                            System.out.print("Enter Status (ACTIVE | INACTIVE): ");
                            String status = sc.nextLine();

                            accService.updateStatus(con, accNo, status);
                            break;
                        case 7:
                            System.out.print("Enter account number: ");
                            String historyAcc = sc.nextLine();
                            txnService.showHistory(con, historyAcc);
                            break;
                        case 8:
                            System.out.println("Thank you for using our banking system. Goodbye!");
                            flag = false;
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            
            sc.close();
            con.close();

        } catch (Exception e) {
            System.out.println("Application failed to start.");
            e.printStackTrace();
        }
    }
}