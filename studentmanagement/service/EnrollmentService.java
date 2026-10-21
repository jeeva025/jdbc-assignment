package studentmanagement.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class EnrollmentService {

    public void enrollStudent(Connection con, int studentId, int courseId)
            throws SQLException {

        String sql = """
            INSERT INTO enrollments
            (student_id, course_id, enrollment_date)
            VALUES (?, ?, ?)
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, courseId);
            ps.setDate(3, Date.valueOf(LocalDate.now()));

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Student enrolled successfully");
            }
        }
    }

    public void fetchAllEnrollments(Connection con) throws SQLException {

        String sql = """
            SELECT e.enrollment_id, s.student_name, c.course_name,
                   e.enrollment_date, e.status
            FROM enrollments e
            JOIN students s ON e.student_id = s.student_id
            JOIN courses c ON e.course_id = c.course_id
            """;

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                        "Enrollment ID: " + rs.getInt("enrollment_id")
                                + " | Student: " + rs.getString("student_name")
                                + " | Course: " + rs.getString("course_name")
                                + " | Date: " + rs.getDate("enrollment_date")
                                + " | Status: " + rs.getString("status")
                );
            }
        }
    }

    public void fetchEnrollmentsByStudent(Connection con, int studentId)
            throws SQLException {

        String sql = """
            SELECT e.enrollment_id, c.course_name,
                   e.enrollment_date, e.status
            FROM enrollments e
            JOIN courses c ON e.course_id = c.course_id
            WHERE e.student_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    System.out.println(
                            "Enrollment ID: " + rs.getInt("enrollment_id")
                                    + " | Course: " + rs.getString("course_name")
                                    + " | Date: " + rs.getDate("enrollment_date")
                                    + " | Status: " + rs.getString("status")
                    );
                }
            }
        }
    }

    public void fetchStudentsByCourse(Connection con, int courseId)
            throws SQLException {

        String sql = """
            SELECT e.enrollment_id, s.student_name, s.email
                   e.enrollment_date, e.status
            FROM enrollments e
            JOIN students s ON e.student_id = s.student_id
            WHERE e.course_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, courseId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    System.out.println(
                            "Enrollment ID: " + rs.getInt("enrollment_id")
                                    + " | StudentName: " + rs.getString("student_name")
                                    + " | Phone : " + rs.getString("phone")
                                    + " | Date: " + rs.getDate("enrollment_date")
                                    + " | Status: " + rs.getString("status")
                    );
                }
            }
        }
    }

    public void updateEnrollmentStatus(Connection con, int enrollmentId,
                                       String status) throws SQLException {

        String sql = """
            UPDATE enrollments
            SET status = ?
            WHERE enrollment_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status.toUpperCase());
            ps.setInt(2, enrollmentId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Enrollment status updated successfully");
            } else {
                System.out.println("Enrollment not found");
            }
        }
    }

}
