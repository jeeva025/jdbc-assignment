package bankingmanagement.service;



import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TransactionService {


    public void showTransactionHistory(Connection con, String accountNumber) throws SQLException {

        String sql = """
                SELECT t.transaction_id,
                       t.transaction_type,
                       t.amount,
                       t.status,
                       t.description,
                       t.transaction_date
                FROM transactions t
                JOIN accounts a ON t.account_id = a.account_id
                WHERE a.account_number = ?
                ORDER BY t.transaction_date DESC
                """;

        try (PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, accountNumber);

            try (ResultSet rs = statement.executeQuery()) {

                boolean found = false;

                System.out.println("\n===== TRANSACTION HISTORY =====");

                while (rs.next()) {
                    found = true;

                    System.out.println("-------------------------------");
                    System.out.println(
                            "Transaction ID: " +
                                    rs.getLong("transaction_id"));

                    System.out.println(
                            "Type: " +
                                    rs.getString("transaction_type"));

                    System.out.println(
                            "Amount: " +
                                    rs.getBigDecimal("amount"));

                    System.out.println(
                            "Status: " +
                                    rs.getString("status"));



                    System.out.println(
                            "Description: " +
                                    rs.getString("description"));

                    System.out.println(
                            "Date: " +
                                    rs.getTimestamp("transaction_date"));
                }

                if (!found) {
                    System.out.println(
                            "No transactions found for this account.");
                }
            }
        }
    }
}


