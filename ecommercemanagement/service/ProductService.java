package ecommercemanagement.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ProductService {

    public void addProduct(Connection con, Scanner sc)
            throws SQLException {

        System.out.print("Enter product name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter description: ");
        String description = sc.nextLine().trim();

        System.out.print("Enter price: ");
        double price = sc.nextDouble();

        System.out.print("Enter stock quantity: ");
        int stock = sc.nextInt();
        sc.nextLine();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }

        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }

        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }

        String sql = """
                INSERT INTO products
                (product_name, description, price, stock)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, description);
            statement.setDouble(3, price);
            statement.setInt(4, stock);

            statement.executeUpdate();

            System.out.println("Product added successfully.");
        }
    }

    public void viewProducts(Connection con)
            throws SQLException {

        String sql = """
                SELECT *
                FROM products
                """;

        try (PreparedStatement statement = con.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            System.out.println("\n============= PRODUCT CATALOG ===========");

            boolean found = false;

            while (rs.next()) {
                found = true;

                System.out.println("-----------------------------");
                System.out.println("Product ID: " + rs.getInt("product_id"));
                System.out.println("Name: " + rs.getString("product_name"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Price: " + rs.getBigDecimal("price"));
                System.out.println("Stock: " + rs.getInt("stock"));
                System.out.println("Status: " + rs.getString("status"));
            }

            if (!found) {
                System.out.println("No products found.");
            }
        }
    }
}
