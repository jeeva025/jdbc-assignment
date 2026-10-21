package bankingmanagement.service;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;


public class AccountService {

    public void accountCreation(Connection con, Scanner sc)
            throws SQLException {

        System.out.print("Enter customer ID: ");
        int customerId = sc.nextInt();
        sc.nextLine();

        System.out.print("Enter account number: ");
        String accountNumber = sc.nextLine();

        System.out.print("Enter account type (SAVINGS/CURRENT/SALARY/FD): ");
        String accountType = sc.nextLine().trim().toUpperCase();



        if (!accountType.matches("SAVINGS|CURRENT|SALARY|FD")) {
            throw new IllegalArgumentException("Invalid account type.");
        }

        if (!isCustomerExists(con, customerId)) {
            throw new IllegalArgumentException("Customer not found.");
        }

        if (isAccountNumberExists(con, accountNumber)) {
            throw new IllegalArgumentException("Account number already exists.");
        }

        String sql = """
                INSERT INTO accounts
                (customer_id, account_number, account_type, balance)
                VALUES (?, ?, ?, 0.00)
                """;

        try (PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, customerId);
            statement.setString(2, accountNumber);
            statement.setString(3, accountType);

            statement.executeUpdate();

            System.out.println("Account created successfully.");
            System.out.println("Account number: " + accountNumber);
        }
    }

    public void deposit(Connection con, String accountNumber, BigDecimal amount)
            throws SQLException {

        validateAmount(amount);

        if (!isAccountNumberExists(con, accountNumber)) {
            throw new IllegalArgumentException("Account not found.");
        }

        String sql = """
                UPDATE accounts
                SET balance = balance + ?
                WHERE account_number = ? AND status = 'ACTIVE'
                """;

        String transactionSQL = """
                INSERT INTO transactions
                (account_id, transaction_type, amount, status, description)
                SELECT account_id, 'DEPOSIT', ?, 'SUCCESS', 'Cash deposit'
                FROM accounts
                WHERE account_number = ? AND status = 'ACTIVE'
                """;

        insertTransaction(
                con, sql, transactionSQL, accountNumber, amount);

        System.out.println("Deposit successful.");
    }

    public void withdrawal(Connection con, String accountNumber, BigDecimal amount)
            throws SQLException {

        validateAmount(amount);

        if (!isAccountNumberExists(con, accountNumber)) {
            throw new IllegalArgumentException("Account not found.");
        }

        String sql = """
                UPDATE accounts
                SET balance = balance - ?
                WHERE account_number = ?
                  AND status = 'ACTIVE'
                  AND balance >= ?
                """;

        String transactionSQL = """
                INSERT INTO transactions
                (account_id, transaction_type, amount, status, description)
                SELECT account_id, 'WITHDRAWAL', ?, 'SUCCESS', 'Cash withdrawal'
                FROM accounts
                WHERE account_number = ? AND status = 'ACTIVE'
                """;

        insertTransaction(con, sql, transactionSQL, accountNumber, amount);

        System.out.println("Withdrawal successful.");
    }

    private void insertTransaction(
            Connection con,
            String balanceSQL,
            String transactionSQL,
            String accountNumber,
            BigDecimal amount) throws SQLException {

        boolean previousAutoCommit = con.getAutoCommit();

        try {
            con.setAutoCommit(false);

            int updatedRows;

            try (PreparedStatement statement =
                         con.prepareStatement(balanceSQL)) {

                statement.setBigDecimal(1, amount);
                statement.setString(2, accountNumber);

                if (balanceSQL.contains("balance >= ?")) {
                    statement.setBigDecimal(3, amount);
                }

                updatedRows = statement.executeUpdate();
            }

            if (updatedRows == 0) {
                if (!isAccountNumberExists(con, accountNumber)) {
                    throw new IllegalArgumentException("Account not found.");
                }

                throw new IllegalArgumentException(
                        "Account is inactive or has insufficient balance.");
            }

            try (PreparedStatement statement =
                         con.prepareStatement(transactionSQL)) {

                statement.setBigDecimal(1, amount);
                statement.setString(2, accountNumber);

                if (statement.executeUpdate() != 1) {
                    throw new SQLException("Transaction record was not created.");
                }
            }

            con.commit();

        } catch (SQLException | RuntimeException e) {
            try {
                con.rollback();
            } catch (SQLException rollbackException) {
                e.addSuppressed(rollbackException);
            }

            throw e;

        } finally {
            try {
                con.setAutoCommit(previousAutoCommit);
            } catch (SQLException e) {
                // Connection restoration failed.
                // Do not silently reuse a connection in an unknown state.
                throw e;
            }
        }
    }

    public void updateStatus(Connection con, String accountNumber, String status){
        String sql = "UPDATE accounts SET status = ? WHERE account_number = ?";
        status = status.toUpperCase();
        if (!status.matches("ACTIVE|INACTIVE")) {
            throw new IllegalArgumentException("Invalid account type.");
        }

        try(PreparedStatement statement = con.prepareStatement(sql)){
            statement.setString(1,status);
            statement.setString(2,accountNumber);

            int rows = statement.executeUpdate();
            if (rows == 0){
                throw new IllegalArgumentException("Account Not Found");
            }
            else {
                System.out.println("Updated Successfully");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isAccountNumberExists(Connection con, String accountNumber) throws SQLException {

        String sql = """
                SELECT 1 FROM accounts
                WHERE account_number = ?
                """;

        try (PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, accountNumber);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean isCustomerExists(Connection con, int customerId) throws SQLException {

        String sql = """
                SELECT 1 FROM customers
                WHERE customer_id = ?
                """;

        try (PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, customerId);

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void validateAmount(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
    }
}


