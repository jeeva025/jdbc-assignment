package bankingmanagement.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerService {

    public void registerCustomer(Connection con, Scanner sc) throws SQLException {
        System.out.print("Enter customer name: ");
        String name = sc.nextLine();

        System.out.print("Enter email: ");
        String email = sc.nextLine();

        System.out.print("Enter phone number: ");
        String phone = sc.nextLine();

        System.out.print("Enter address: ");
        String address = sc.nextLine();

        if (name.trim().isEmpty() || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Name and address cannot be empty.");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Invalid email format.");
        }

        if (phone.length() != 10) {
            throw new IllegalArgumentException("Phone number must be 10 digits.");
        }

        String sql = "INSERT INTO customers (customer_name, email, phone, address) VALUES (?, ?, ?, ?)";
        
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, name);
        pstmt.setString(2, email);
        pstmt.setString(3, phone);
        pstmt.setString(4, address);

        pstmt.executeUpdate();
        pstmt.close();

        System.out.println("Customer registered successfully!");
    }

    public void updatePhone(Connection con, int customerId, String newPhone) throws SQLException {
        if (newPhone.length() != 10) {
            throw new IllegalArgumentException("Phone number must be exactly 10 digits.");
        }
        
        String sql = "UPDATE customers SET phone = ? WHERE customer_id = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, newPhone);
        pstmt.setInt(2, customerId);

        int rows = pstmt.executeUpdate();
        pstmt.close();
        
        if (rows > 0) {
            System.out.println("Phone number updated successfully.");
        } else {
            System.out.println("Customer ID not found.");
        }
    }
}