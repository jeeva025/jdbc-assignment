package bankingmanagement.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountService {

    public void createAccount(Connection con, Scanner sc) throws SQLException {
        System.out.print("Enter customer ID: ");
        int customerId = Integer.parseInt(sc.nextLine());

        System.out.print("Enter account number: ");
        String accNum = sc.nextLine();

        System.out.print("Enter account type (SAVINGS/CURRENT/SALARY/FD): ");
        String accType = sc.nextLine().toUpperCase();

        if (!accType.equals("SAVINGS") && !accType.equals("CURRENT") && !accType.equals("SALARY") && !accType.equals("FD")) {
            throw new IllegalArgumentException("Invalid account type.");
        }

        String sql = "INSERT INTO accounts (customer_id, account_number, account_type, balance) VALUES (?, ?, ?, 0.00)";
        
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, customerId);
        pstmt.setString(2, accNum);
        pstmt.setString(3, accType);

        pstmt.executeUpdate();
        pstmt.close();

        System.out.println("Account created successfully. Account Number: " + accNum);
    }

    public void deposit(Connection con, String accNum, double amount) throws SQLException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        int accountId = getAccountId(con, accNum);
        if (accountId == -1) {
            throw new IllegalArgumentException("Account not found or inactive.");
        }

        try {
            con.setAutoCommit(false);

            String updateSql = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
            PreparedStatement updateStmt = con.prepareStatement(updateSql);
            updateStmt.setDouble(1, amount);
            updateStmt.setInt(2, accountId);
            updateStmt.executeUpdate();
            updateStmt.close();

            String txnSql = "INSERT INTO transactions (account_id, transaction_type, amount, description) VALUES (?, 'DEPOSIT', ?, 'Cash deposit')";
            PreparedStatement txnStmt = con.prepareStatement(txnSql);
            txnStmt.setInt(1, accountId);
            txnStmt.setDouble(2, amount);
            txnStmt.executeUpdate();
            txnStmt.close();

            con.commit();
            System.out.println("Deposit successful. Deposited: ₹" + amount);

        } catch (Exception e) {
            con.rollback();
            throw new SQLException("Transaction failed: " + e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    public void withdrawal(Connection con, String accNum, double amount) throws SQLException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero.");
        }

        int accountId = getAccountId(con, accNum);
        if (accountId == -1) {
            throw new IllegalArgumentException("Account not found or inactive.");
        }

        try {
            con.setAutoCommit(false);

            String checkSql = "SELECT balance FROM accounts WHERE account_id = ?";
            PreparedStatement checkStmt = con.prepareStatement(checkSql);
            checkStmt.setInt(1, accountId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");
                if (currentBalance < amount) {
                    throw new IllegalArgumentException("Insufficient balance.");
                }
            }
            checkStmt.close();

            String updateSql = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
            PreparedStatement updateStmt = con.prepareStatement(updateSql);
            updateStmt.setDouble(1, amount);
            updateStmt.setInt(2, accountId);
            updateStmt.executeUpdate();
            updateStmt.close();

            String txnSql = "INSERT INTO transactions (account_id, transaction_type, amount, description) VALUES (?, 'WITHDRAWAL', ?, 'Cash withdrawal')";
            PreparedStatement txnStmt = con.prepareStatement(txnSql);
            txnStmt.setInt(1, accountId);
            txnStmt.setDouble(2, amount);
            txnStmt.executeUpdate();
            txnStmt.close();

            con.commit();
            System.out.println("Withdrawal successful. Withdrawn: ₹" + amount);

        } catch (Exception e) {
            con.rollback();
            throw new SQLException("Transaction failed: " + e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    public void updateStatus(Connection con, String accNum, String status) throws SQLException {
        status = status.toUpperCase();
        if (!status.equals("ACTIVE") && !status.equals("INACTIVE")) {
            throw new IllegalArgumentException("Status must be ACTIVE or INACTIVE.");
        }

        String sql = "UPDATE accounts SET status = ? WHERE account_number = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, status);
        pstmt.setString(2, accNum);

        int rows = pstmt.executeUpdate();
        pstmt.close();

        if (rows > 0) {
            System.out.println("Account status updated to " + status);
        } else {
            System.out.println("Account not found.");
        }
    }

    private int getAccountId(Connection con, String accNum) throws SQLException {
        String sql = "SELECT account_id FROM accounts WHERE account_number = ? AND status = 'ACTIVE'";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, accNum);
        
        ResultSet rs = pstmt.executeQuery();
        int id = -1;
        if (rs.next()) {
            id = rs.getInt("account_id");
        }
        
        rs.close();
        pstmt.close();
        return id;
    }
}