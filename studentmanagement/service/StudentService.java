package studentmanagement.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class StudentService {
    
    public void addStudent(Connection con, Scanner sc) {
        try {
            System.out.print("Enter Student Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Student Email: ");
            String email = sc.nextLine();

            System.out.print("Enter Student Phone: ");
            String phone = sc.nextLine();

            if (!email.contains("@") || !email.contains(".")) {
                System.out.println("Invalid email format.");
                return;
            }
            
            if (phone.length() != 10) {
                System.out.println("Phone number must be 10 digits.");
                return;
            }

            String sql = "INSERT INTO students (student_name, email, phone) VALUES (?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);

            pstmt.executeUpdate();
            pstmt.close();

            System.out.println("---------------Inserted Successfully------------------");
            
        } catch (Exception e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }
    
    public void fetchAllStudent(Connection con) {
        try {
            String sql = "SELECT * FROM students";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            displayDetails(rs);
            
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void fetchByName(Connection con, String name) {
        try {
            String sql = "SELECT * FROM students WHERE student_name = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, name);

            ResultSet rs = pstmt.executeQuery();
            displayDetails(rs);
            
            rs.close();
            pstmt.close();
        } catch (Exception e) {
             System.out.println("Error: " + e.getMessage());
        }
    }

    public void fetchById(Connection con, int id) {
        try {
            String sql = "SELECT * FROM students WHERE student_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            displayDetails(rs);
            
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void updateEmail(Connection con, String newEmail, int id) {
        try {
            String sql = "UPDATE students SET email = ? WHERE student_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, newEmail);
            pstmt.setInt(2, id);
            
            int rows = pstmt.executeUpdate();
            pstmt.close();
            
            if (rows == 0) {
                System.out.println("No Record Found");
            } else {
                System.out.println("Email Updated Successfully");
            }
        } catch (Exception e) {
             System.out.println("Error: " + e.getMessage());
        }
    }

    public void updatePhone(Connection con, String phone, int id) {
        try {
            String sql = "UPDATE students SET phone = ? WHERE student_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, phone);
            pstmt.setInt(2, id);
            
            int rows = pstmt.executeUpdate();
            pstmt.close();

            if (rows == 0){
                System.out.println("No Record Found");
            } else {
                System.out.println("Phone updated successfully");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void displayDetails(ResultSet rs) throws Exception {
        boolean found = false;
        while (rs.next()) {
            found = true;
            System.out.println("Student ID: " + rs.getInt("student_id") + 
                               " | Name: " + rs.getString("student_name") + 
                               " | Email: " + rs.getString("email") + 
                               " | Phone: " + rs.getString("phone"));
        }

        if (!found){
            System.out.println("No students found.");
        }
    }
}