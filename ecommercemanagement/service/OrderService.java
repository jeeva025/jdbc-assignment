package ecommercemanagement.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OrderService {

    public void placeOrder(Connection con, Scanner sc,
                           Map<Integer, Integer> cart)
            throws SQLException {

        if (cart.isEmpty()) {
            throw new IllegalArgumentException(
                    "Your shopping cart is empty.");
        }

        System.out.print("Enter customer ID: ");
        int customerId = sc.nextInt();

        boolean previousAutoCommit = con.getAutoCommit();

        try {
            con.setAutoCommit(false);

            String customerSQL =
                    "SELECT customer_id FROM customers WHERE customer_id = ?";

            try (PreparedStatement statement =
                         con.prepareStatement(customerSQL)) {

                statement.setInt(1, customerId);

                try (ResultSet rs = statement.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException(
                                "Customer not found.");
                    }
                }
            }

            double total = 0;

            Map<Integer, Double> prices = new HashMap<>(); // stores the price of each product at the time of order placement.

            String productSQL = """
                    SELECT price, stock, status
                    FROM products
                    WHERE product_id = ?
                 """;

            try (PreparedStatement statement =
                         con.prepareStatement(productSQL)) {

                for (Map.Entry<Integer, Integer> item : cart.entrySet()) {

                    statement.setInt(1, item.getKey());

                    try (ResultSet rs = statement.executeQuery()) {

                        if (!rs.next()) {
                            throw new IllegalArgumentException(
                                    "Product not found: " + item.getKey());
                        }

                        double price = rs.getDouble("price");
                        int stock = rs.getInt("stock");
                        String status = rs.getString("status");

                        if (!status.equals("AVAILABLE")) {
                            throw new IllegalArgumentException(
                                    "Product is unavailable: " + item.getKey());
                        }

                        if (stock < item.getValue()) {
                            throw new IllegalArgumentException(
                                    "Insufficient stock for product ID: "
                                            + item.getKey());
                        }

                        prices.put(item.getKey(), price);

                        double subtotal = price * item.getValue();

                        total = total + subtotal;
                    }
                }
            }

            String orderSQL = """
                    INSERT INTO orders
                    (customer_id, total_amount, order_status)
                    VALUES (?, ?, 'PLACED')
                    """;

            int orderId;

            try (PreparedStatement statement = con.prepareStatement(
                                 orderSQL,
                                 PreparedStatement.RETURN_GENERATED_KEYS)) {

                statement.setInt(1, customerId);
                statement.setDouble(2, total);

                statement.executeUpdate();

                try (ResultSet rs = statement.getGeneratedKeys()) {

                    if (!rs.next()) {
                        throw new SQLException(
                                "Failed to generate order ID.");
                    }

                    orderId = rs.getInt(1);
                }
            }

            String itemSQL = """
                    INSERT INTO order_items
                    (order_id, product_id, quantity, price)
                    VALUES (?, ?, ?, ?)
                    """;

            String stockSQL = """
                    UPDATE products
                    SET stock = stock - ?
                    WHERE product_id = ?
                      AND stock >= ?
                      AND status = 'AVAILABLE'
                    """;

            try (PreparedStatement itemStatement =
                         con.prepareStatement(itemSQL);
                 PreparedStatement stockStatement =
                         con.prepareStatement(stockSQL)) {

                for (Map.Entry<Integer, Integer> item : cart.entrySet()) {

                    int productId = item.getKey();
                    int quantity = item.getValue();

                    itemStatement.setInt(1, orderId);
                    itemStatement.setInt(2, productId);
                    itemStatement.setInt(3, quantity);
                    itemStatement.setDouble(
                            4, prices.get(productId));

                    itemStatement.addBatch();

                    stockStatement.setInt(1, quantity);
                    stockStatement.setInt(2, productId);
                    stockStatement.setInt(3, quantity);

                    if (stockStatement.executeUpdate() != 1) {
                        throw new SQLException(
                                "Failed to update stock for product ID: "
                                        + productId);
                    }
                }

                itemStatement.executeBatch();
            }

            con.commit();

            cart.clear();

            System.out.println("Order placed successfully.");
            System.out.println("Order ID: " + orderId);
            System.out.println("Total Amount: " + total);
            System.out.println("Order Status: PLACED");

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
                throw e;
            }
        }
    }
}