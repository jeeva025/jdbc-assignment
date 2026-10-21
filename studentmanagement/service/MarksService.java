package studentmanagement.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class MarksService {


    public void addMarks(Connection con, int enrollmentId,
                         String assessmentName, int marksObtained,
                         int maxMarks) throws SQLException {

        if (marksObtained < 0 || maxMarks <= 0
                || marksObtained > maxMarks) {
            System.out.println("Invalid marks");
            return;
        }

        String sql = """
            INSERT INTO marks
            (enrollment_id, assessment_name, marks_obtained,
             max_marks, assessment_date)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setString(2, assessmentName);
            ps.setInt(3, marksObtained);
            ps.setInt(4, maxMarks);
            ps.setDate(5, Date.valueOf(LocalDate.now()));

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Marks added successfully");
            }
        }
    }

    public void fetchMarksByEnrollment(Connection con, int enrollmentId)
            throws SQLException {

        String sql = """
            SELECT marks_id, assessment_name, marks_obtained,
                   max_marks, assessment_date
            FROM marks
            WHERE enrollment_id = ?
            ORDER BY assessment_date
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    System.out.println(
                            "Marks ID: " + rs.getInt("marks_id")
                                    + " | Assessment: " + rs.getString("assessment_name")
                                    + " | Marks: " + rs.getInt("marks_obtained")
                                    + "/" + rs.getInt("max_marks")
                                    + " | Date: " + rs.getDate("assessment_date")
                    );
                }
            }
        }
    }

    public void fetchAllMarks(Connection con) throws SQLException {

        String sql = """
            SELECT s.student_name, c.course_name, m.assessment_name,
                   m.marks_obtained, m.max_marks, m.assessment_date
            FROM marks m
            JOIN enrollments e ON m.enrollment_id = e.enrollment_id
            JOIN students s ON e.student_id = s.student_id
            JOIN courses c ON e.course_id = c.course_id
            ORDER BY s.student_name
            """;

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.println(
                        "Student: " + rs.getString("student_name")
                                + " | Course: " + rs.getString("course_name")
                                + " | Assessment: " + rs.getString("assessment_name")
                                + " | Marks: " + rs.getInt("marks_obtained")
                                + "/" + rs.getInt("max_marks")
                                + " | Date: " + rs.getDate("assessment_date")
                );
            }
        }
    }

    public void calculateResult(Connection con, int enrollmentId)
            throws SQLException {

        String sql = """
            SELECT SUM(marks_obtained) AS total_obtained,
                   SUM(max_marks) AS total_maximum
            FROM marks
            WHERE enrollment_id = ?
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    int totalObtained = rs.getInt("total_obtained");
                    int totalMaximum = rs.getInt("total_maximum");

                    if (rs.wasNull() || totalMaximum == 0) {
                        System.out.println("No marks available");
                        return;
                    }

                    double percentage =
                            (totalObtained * 100.0) / totalMaximum;

                    System.out.println("Total Marks: "
                            + totalObtained + "/" + totalMaximum);

                    System.out.printf("Percentage: %.2f%%%n", percentage);
                }
            }
        }
    }


}
