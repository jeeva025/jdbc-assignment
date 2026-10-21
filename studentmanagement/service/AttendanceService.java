package studentmanagement.service;


import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class AttendanceService {

    public void markAttendance(Connection con, int enrollmentId,
                               String status) throws SQLException {

        String sql = """
            INSERT INTO attendance
            (enrollment_id, attendance_date, attendance_status)
            VALUES (?, ?, ?)
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setString(3, status.toUpperCase());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Attendance recorded successfully");
            }
        }
    }

    public void fetchAttendanceByEnrollment(Connection con, int enrollmentId)
            throws SQLException {

        String sql = """
            SELECT attendance_id, attendance_date, attendance_status
            FROM attendance
            WHERE enrollment_id = ?
            ORDER BY attendance_date
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    System.out.println(
                            "Attendance ID: " + rs.getInt("attendance_id")
                                    + " | Date: " + rs.getDate("attendance_date")
                                    + " | Status: " + rs.getString("attendance_status")
                    );
                }
            }
        }
    }

    public void fetchAllAttendance(Connection con) throws SQLException {

        String sql = """
            SELECT a.attendance_id, s.student_name, c.course_name,
                   a.attendance_date, a.attendance_status
            FROM attendance a
            JOIN enrollments e ON a.enrollment_id = e.enrollment_id
            JOIN students s ON e.student_id = s.student_id
            JOIN courses c ON e.course_id = c.course_id
            ORDER BY a.attendance_date
            """;

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                        "Attendance ID: " + rs.getInt("attendance_id")
                                + " | Student: " + rs.getString("student_name")
                                + " | Course: " + rs.getString("course_name")
                                + " | Date: " + rs.getDate("attendance_date")
                                + " | Status: " + rs.getString("attendance_status")
                );
            }
        }
    }
}

