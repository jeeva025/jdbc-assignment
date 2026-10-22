package studentmanagement.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class CourseService {
    
    public void addCourse(Connection con, Scanner sc) {
        try {
            System.out.print("Enter Course Name: ");
            String courseName = sc.nextLine();

            System.out.print("Enter Course Duration in Months: ");
            int duration = Integer.parseInt(sc.nextLine());

            if (duration >= 3 && duration <= 10) {
                String sql = "INSERT INTO courses (course_name, duration) VALUES (?, ?)";
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setString(1, courseName);
                pstmt.setInt(2, duration);

                pstmt.executeUpdate();
                pstmt.close();

                System.out.println("Course Added Successfully!");
            } else {
                System.out.println("Invalid Duration. Duration must be between 3 and 10 months.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void fetchAllCourses(Connection con) {
        try {
            String sql = "SELECT * FROM courses";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.println("Course ID: " + rs.getInt("course_id") + 
                                   " | Course Name: " + rs.getString("course_name") + 
                                   " | Duration: " + rs.getInt("duration") + " months");
            }
            
            if(!found) {
                System.out.println("No courses found.");
            }
            
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}