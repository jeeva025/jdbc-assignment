package studentmanagement.service;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.Scanner;

public class StudentService {
    public void addStudent(Connection con, Scanner sc) throws SQLException {



        System.out.print("Enter Student Name : ");
        String name = sc.nextLine();

        System.out.print("Enter Student Email : ");
        String email = sc.nextLine();

        System.out.print("Enter Student Phone : ");
        String phone = sc.nextLine();

        String sql = "INSERT INTO students (student_name, email, phone) VALUES (?, ?, ?)";;

        PreparedStatement statement = con.prepareStatement(sql);
        statement.setString(1,name);
        statement.setString(2,email);
        statement.setString(3,phone);

        statement.executeUpdate();

        System.out.println("---------------Inserted Successfully------------------");
    }
    public void fetchAllStudent(Connection con) throws SQLException {
        String sql = "SELECT * FROM students";
        Statement statement = con.createStatement();

        try(ResultSet result = statement.executeQuery(sql)){
            displayDetails(result);

        }

    }

    public void fetchByName(Connection con, String name) throws SQLException {
        String sql = "SELECT * FROM students where student_name = ?";
        try(PreparedStatement statement = con.prepareStatement(sql)){
            statement.setString(1,name);

            try(ResultSet result = statement.executeQuery()){
                displayDetails(result);

            }
        }
    }

    public void fetchById(Connection con, int id) throws SQLException {

        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (PreparedStatement statement = con.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                displayDetails(result);
            }
        }
    }

    public void updateEmail(Connection con, String newEmail, int id)throws SQLException {
        String sql = "UPDATE students set email = ? where student_id = ?";
        int rows = 0;
        try(PreparedStatement statement = con.prepareStatement(sql)){
            statement.setString(1,newEmail);
            statement.setInt(2,id);
             rows = statement.executeUpdate();
        }

        if (rows == 0){
            System.out.println("No Record Found");
        }
        else {
            System.out.println("Email Updated");
        }
    }

    public void updatePhone(Connection con, String phone, int id)throws SQLException {
        String sql = "UPDATE students set phone = ? where student_id = ?";
        int rows = 0;
        try(PreparedStatement statement = con.prepareStatement(sql)){
            statement.setString(1,phone);
            statement.setInt(2,id);
            rows = statement.executeUpdate();
        }


        if (rows == 0){
            System.out.println("No Record Found");
        } else {
            System.out.println("Phone updated successfully");
        }
    }


    public  void displayDetails(ResultSet result) throws SQLException {
        boolean found = false;

        while (result.next()){
            found = true;
            System.out.println("Student ID : "+result.getInt("student_id") + " | "+ "Student Name : "+result.getString("student_name") + " | "+"Email : "+result.getString("email")+ " | "+"Phone : "+result.getString("phone"));
        }

        if (!found){
            System.out.println("No students found");
        }
    }


    public boolean isEmailValid(String email) {
        return email != null
                && (email.endsWith("@gmail.com")
                || email.endsWith("@yahoo.com")
                || email.endsWith("@outlook.com"));
    }
}
