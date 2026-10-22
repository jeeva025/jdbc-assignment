package studentmanagement.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MarksService {

    public void addMarks(Connection con, int enrollmentId, String assessmentName, int marksObtained, int maxMarks) {
        try {
            if (marksObtained < 0 || maxMarks <= 0 || marksObtained > maxMarks) {
                System.out.println("Invalid marks entered.");
                return;
            }

            String sql = "INSERT INTO marks (enrollment_id, assessment_name, marks_obtained, max_marks, assessment_date) " +
                         "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, enrollmentId);
            ps.setString(2, assessmentName);
            ps.setInt(3, marksObtained);
            ps.setInt(4, maxMarks);
            ps.setDate(5, new Date(System.currentTimeMillis()));

            int rows = ps.executeUpdate();
            ps.close();

            if (rows > 0) {
                System.out.println("Marks added successfully!");
            }
        } catch (Exception e) {
            System.out.println("Error adding marks: " + e.getMessage());
        }
    }

    public void fetchMarksByEnrollment(Connection con, int enrollmentId) {
        try {
            String sql = "SELECT marks_id, assessment_name, marks_obtained, max_marks, assessment_date " +
                         "FROM marks WHERE enrollment_id = ? ORDER BY assessment_date";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println("Marks ID: " + rs.getInt("marks_id") + 
                                   " | Assessment: " + rs.getString("assessment_name") + 
                                   " | Marks: " + rs.getInt("marks_obtained") + "/" + rs.getInt("max_marks") + 
                                   " | Date: " + rs.getDate("assessment_date"));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void fetchAllMarks(Connection con) {
        try {
            String sql = "SELECT s.student_name, c.course_name, m.assessment_name, m.marks_obtained, m.max_marks, m.assessment_date " +
                         "FROM marks m " +
                         "JOIN enrollments e ON m.enrollment_id = e.enrollment_id " +
                         "JOIN students s ON e.student_id = s.student_id " +
                         "JOIN courses c ON e.course_id = c.course_id " +
                         "ORDER BY s.student_name";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                System.out.println("Student: " + rs.getString("student_name") + 
                                   " | Course: " + rs.getString("course_name") + 
                                   " | Assessment: " + rs.getString("assessment_name") + 
                                   " | Marks: " + rs.getInt("marks_obtained") + "/" + rs.getInt("max_marks") + 
                                   " | Date: " + rs.getDate("assessment_date"));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void calculateResult(Connection con, int enrollmentId) {
        try {
            String sql = "SELECT SUM(marks_obtained) AS total_obtained, SUM(max_marks) AS total_maximum " +
                         "FROM marks WHERE enrollment_id = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int totalObtained = rs.getInt("total_obtained");
                int totalMaximum = rs.getInt("total_maximum");

                if (totalMaximum == 0) {
                    System.out.println("No marks available for this student.");
                } else {
                    double percentage = (double) totalObtained / totalMaximum * 100;
                    System.out.println("Total Marks: " + totalObtained + "/" + totalMaximum);
                    System.out.println("Percentage: " + String.format("%.2f", percentage) + "%");
                }
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}