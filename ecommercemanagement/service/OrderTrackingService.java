package ecommercemanagement.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class OrderTrackingService {

    public void trackOrder(Connection con, Scanner sc)
            throws SQLException {

        System.out.print("Enter order ID: ");
        int orderId = sc.nextInt();

        String sql = """
                SELECT o.order_id,
                       c.customer_name,
                       o.total_amount,
                       o.order_status,
                       o.order_date,
                       p.product_name,
                       oi.quantity,
                       oi.price
                FROM orders o
                JOIN customers c
                    ON o.customer_id = c.customer_id
                JOIN order_items oi
                    ON o.order_id = oi.order_id
                JOIN products p
                    ON oi.product_id = p.product_id
                WHERE o.order_id = ?
                ORDER BY oi.order_item_id
                """;

        try (PreparedStatement statement =
                     con.prepareStatement(sql)) {

            statement.setInt(1, orderId);

            try (ResultSet rs = statement.executeQuery()) {

                boolean found = false;

                System.out.println("\n========== ORDER TRACKING =======");

                while (rs.next()) {

                    if (!found) {
                        found = true;

                        System.out.println(
                                "Order ID: " + rs.getInt("order_id"));

                        System.out.println(
                                "Customer: " + rs.getString("customer_name"));

                        System.out.println(
                                "Total Amount: "
                                        + rs.getDouble("total_amount"));

                        System.out.println(
                                "Status: " + rs.getString("order_status"));

                        System.out.println(
                                "Order Date: " + rs.getTimestamp("order_date"));

                        System.out.println("\nProducts:");
                    }

                }

                if (!found) {
                    System.out.println("Order not found.");
                }
            }
        }
    }
}