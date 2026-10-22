package studentmanagement.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AttendanceService {

    public void markAttendance(Connection con, int enrollmentId, String status) {
        try {
            if (!status.equals("PRESENT") && !status.equals("ABSENT")) {
                System.out.println("Invalid status. Must be PRESENT or ABSENT.");
                return;
            }

            String sql = "INSERT INTO attendance (enrollment_id, attendance_date, attendance_status) VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, enrollmentId);
            ps.setDate(2, new Date(System.currentTimeMillis()));
            ps.setString(3, status);

            int rows = ps.executeUpdate();
            ps.close();

            if (rows > 0) {
                System.out.println("Attendance recorded successfully.");
            }
        } catch (Exception e) {
            System.out.println("Error recording attendance: " + e.getMessage());
        }
    }

    public void fetchAttendanceByEnrollment(Connection con, int enrollmentId) {
        try {
            String sql = "SELECT attendance_id, attendance_date, attendance_status " +
                         "FROM attendance WHERE enrollment_id = ? ORDER BY attendance_date DESC";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                System.out.println("Attendance ID: " + rs.getInt("attendance_id") + 
                                   " | Date: " + rs.getDate("attendance_date") + 
                                   " | Status: " + rs.getString("attendance_status"));
            }
            
            if(!hasRecords) System.out.println("No attendance records found.");
            
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void fetchAllAttendance(Connection con) {
        try {
            String sql = "SELECT a.attendance_id, s.student_name, c.course_name, a.attendance_date, a.attendance_status " +
                         "FROM attendance a " +
                         "JOIN enrollments e ON a.enrollment_id = e.enrollment_id " +
                         "JOIN students s ON e.student_id = s.student_id " +
                         "JOIN courses c ON e.course_id = c.course_id " +
                         "ORDER BY a.attendance_date DESC";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                System.out.println("Attendance ID: " + rs.getInt("attendance_id") + 
                                   " | Student: " + rs.getString("student_name") + 
                                   " | Course: " + rs.getString("course_name") + 
                                   " | Date: " + rs.getDate("attendance_date") + 
                                   " | Status: " + rs.getString("attendance_status"));
            }
            
            rs.close();
            st.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}