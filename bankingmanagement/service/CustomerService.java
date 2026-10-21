package bankingmanagement.service;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class CustomerService {

    public void customerRegistration(Connection con, Scanner sc) throws SQLException {

        System.out.println("Enter customer name: ");
        String name = sc.next();

        System.out.println("Enter email: ");
        String email = sc.next();
        sc.nextLine();


        System.out.println("Enter phone number: ");
        String phone = sc.nextLine();

        System.out.println("Enter address: ");
        String address = sc.next();


        if (name.isEmpty() || address.isEmpty()) {
            throw new IllegalArgumentException("Name and address cannot be empty.");
        }

        if (!isEmailValid(email)) {
            throw new IllegalArgumentException("Invalid email address.");
        }

        if (!phone.matches("[0-9]{10}")) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
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

    public void updatePhone(Connection con, int customerId, String newPhone) throws SQLException {
        if (!newPhone.matches("[0-9]{10}")) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
        }
        String sql = "UPDATE customers SET phone = ? WHERE customer_id = ?";

        try(PreparedStatement statement = con.prepareStatement(sql)){
            statement.setString(1,newPhone);
            statement.setInt(2,customerId);



            int rows = statement.executeUpdate();
            if (rows == 0){
                throw new IllegalArgumentException("Invalid Customer ID");
            }
            else {
                System.out.println("Updated Successfully");
            }
        }
    }
    public boolean isEmailValid(String email) {
        return email != null
                && (email.endsWith("@gmail.com")
                || email.endsWith("@yahoo.com")
                || email.endsWith("@outlook.com"));
    }
}

