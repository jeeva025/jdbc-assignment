package ecommercemanagement.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerService {

    public void registerCustomer(Connection con, Scanner sc)
            throws SQLException {

        System.out.print("Enter customer name: ");
        String name = sc.nextLine();

        System.out.print("Enter email: ");
        String email = sc.nextLine();

        System.out.print("Enter phone number: ");
        String phone = sc.nextLine();

        System.out.print("Enter address: ");
        String address = sc.nextLine();

        if (name.isEmpty() || address.isEmpty()) {
            throw new IllegalArgumentException(
                    "Name and address cannot be empty.");
        }

        if (!email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email address.");
        }

        if (!phone.matches("[0-9]{10}")) {
            throw new IllegalArgumentException(
                    "Phone number must contain exactly 10 digits.");
        }

        String sql = """
                INSERT INTO customers
                (customer_name, email, phone, address)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, phone);
            statement.setString(4, address);

            statement.executeUpdate();

            System.out.println("Customer registered successfully.");
        }
    }
}