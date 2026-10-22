package bankingmanagement.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TransactionService {

    public void showHistory(Connection con, String accNum) throws SQLException {
        String sql = "SELECT t.transaction_id, t.transaction_type, t.amount, t.status, t.description, t.transaction_date " +
                     "FROM transactions t " +
                     "JOIN accounts a ON t.account_id = a.account_id " +
                     "WHERE a.account_number = ? ORDER BY t.transaction_date DESC";

        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, accNum);
        
        ResultSet rs = pstmt.executeQuery();
        boolean hasRecords = false;

        System.out.println("\n---------------- TRANSACTION HISTORY ---------------");
        
        while (rs.next()) {
            hasRecords = true;
            System.out.println("-------------------------------");
            System.out.println("ID: " + rs.getLong("transaction_id"));
            System.out.println("Type: " + rs.getString("transaction_type"));
            System.out.println("Amount: $" + rs.getDouble("amount"));
            System.out.println("Status: " + rs.getString("status"));
            System.out.println("Description: " + rs.getString("description"));
            System.out.println("Date: " + rs.getTimestamp("transaction_date"));
        }

        if (!hasRecords) {
            System.out.println("No transactions found for account: " + accNum);
        }
        
        rs.close();
        pstmt.close();
    }
}