package studentmanagement.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class EnrollmentService {

    public void enrollStudent(Connection con, int studentId, int courseId) {
        try {
            String sql = "INSERT INTO enrollments (student_id, course_id, enrollment_date) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.setDate(3, new Date(System.currentTimeMillis()));

            int rows = ps.executeUpdate();
            ps.close();

            if (rows > 0) {
                System.out.println("Student enrolled successfully.");
            }
        } catch (Exception e) {
            System.out.println("Failed to enroll: " + e.getMessage());
        }
    }

    public void fetchAllEnrollments(Connection con) {
        try {
            String sql = "SELECT e.enrollment_id, s.student_name, c.course_name, e.enrollment_date, e.status FROM enrollments e " +
                         "JOIN students s ON e.student_id = s.student_id " +
                         "JOIN courses c ON e.course_id = c.course_id";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                System.out.println("Enrollment ID: " + rs.getInt("enrollment_id") + 
                                   " | Student: " + rs.getString("student_name") + 
                                   " | Course: " + rs.getString("course_name") + 
                                   " | Date: " + rs.getDate("enrollment_date") + 
                                   " | Status: " + rs.getString("status"));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void fetchEnrollmentsByStudent(Connection con, int studentId) {
        try {
            String sql = "SELECT e.enrollment_id, c.course_name, e.enrollment_date, e.status FROM enrollments e " +
                         "JOIN courses c ON e.course_id = c.course_id " +
                         "WHERE e.student_id = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.println("Enrollment ID: " + rs.getInt("enrollment_id") + 
                                   " | Course: " + rs.getString("course_name") + 
                                   " | Date: " + rs.getDate("enrollment_date") + 
                                   " | Status: " + rs.getString("status"));
            }
            
            if(!hasData) System.out.println("No enrollments found for this student.");
            
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void updateEnrollmentStatus(Connection con, int enrollmentId, String status) {
        try {
            if (!status.equals("ACTIVE") && !status.equals("COMPLETED") && !status.equals("INACTIVE")) {
                System.out.println("Invalid status. Must be ACTIVE, COMPLETED, or INACTIVE.");
                return;
            }

            String sql = "UPDATE enrollments SET status = ? WHERE enrollment_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, enrollmentId);

            int rows = ps.executeUpdate();
            ps.close();

            if (rows > 0) {
                System.out.println("Enrollment status updated successfully");
            } else {
                System.out.println("Enrollment not found");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}