package ecommercemanagement.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

public class CartService {

    public void addToCart(Connection con, Scanner sc, Map<Integer, Integer> cart)
            throws SQLException {

        System.out.print("Enter product ID: ");
        int productId = sc.nextInt();

        System.out.print("Enter quantity: ");
        int quantity = sc.nextInt();

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero.");
        }

        String sql = """
                SELECT stock, status
                FROM products
                WHERE product_id = ?
                """;

        try (PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, productId);

            try (ResultSet rs = statement.executeQuery()) {

                if (!rs.next()) {
                    throw new IllegalArgumentException(
                            "Product not found.");
                }

                if (!rs.getString("status").equals("AVAILABLE")) {
                    throw new IllegalArgumentException(
                            "Product is unavailable.");
                }

                int currentQuantity = cart.getOrDefault(productId, 0);

                if (quantity > rs.getInt("stock") - currentQuantity) {
                    throw new IllegalArgumentException(
                            "Requested quantity exceeds available stock.");
                }

                cart.put(productId, currentQuantity + quantity);

                System.out.println("Product added to cart.");
            }
        }
    }

    public void viewCart(Connection con,
                         Map<Integer, Integer> cart)
            throws SQLException {

        if (cart.isEmpty()) {
            System.out.println("Your shopping cart is empty.");
            return;
        }

        String sql = """
                SELECT product_name, price, stock, status
                FROM products
                WHERE product_id = ?
                """;

        double total = 0;

        System.out.println("\n========== SHOPPING CART ==========");

        try (PreparedStatement statement = con.prepareStatement(sql)) {

            for (Map.Entry<Integer, Integer> item : cart.entrySet()) {

                statement.setInt(1, item.getKey());

                try (ResultSet rs = statement.executeQuery()) {

                    if (!rs.next()) {
                        System.out.println(
                                "Product " + item.getKey() + " no longer exists.");
                        continue;
                    }

                    String name = rs.getString("product_name");
                    double price = rs.getDouble("price");
                    int quantity = item.getValue();

                    double subtotal = price * quantity;

                    total = total+subtotal;

                    System.out.println("-----------------------------");
                    System.out.println("Product: " + name);
                    System.out.println("Price: " + price);
                    System.out.println("Quantity: " + quantity);
                    System.out.println("Subtotal: " + subtotal);
                }
            }
        }

        System.out.println("-----------------------------");
        System.out.println("Total Amount: " + total);
    }
}