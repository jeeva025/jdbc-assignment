package studentmanagement;

import studentmanagement.service.*;

import java.sql.*;
import java.util.Scanner;

public class Main {


    static final String url = "jdbc:mysql://localhost:3306/student_management";
    static final String user = "root";
    static final String pass = "root";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection con = DriverManager.getConnection(url, user, pass);
                 Scanner sc = new Scanner(System.in)) {

                String studentSQL = """
                    CREATE TABLE IF NOT EXISTS students (
                        student_id INT PRIMARY KEY AUTO_INCREMENT,
                        student_name VARCHAR(100) NOT NULL,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        phone VARCHAR(15) UNIQUE CHECK(LENGTH(phone) = 10)
                    )
                    """;

                String courseSQL = """
                    CREATE TABLE IF NOT EXISTS courses (
                        course_id INT PRIMARY KEY AUTO_INCREMENT,
                        course_name VARCHAR(30) NOT NULL,
                        duration INT NOT NULL CHECK (duration > 0)
                    )
                    """;

                String enrollmentSQL = """
                    CREATE TABLE IF NOT EXISTS enrollments (
                        enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
                        student_id INT NOT NULL,
                        course_id INT NOT NULL,
                        enrollment_date DATE NOT NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                        FOREIGN KEY (student_id)
                            REFERENCES students(student_id),
                        FOREIGN KEY (course_id)
                            REFERENCES courses(course_id),
                        UNIQUE (student_id, course_id)
                    )
                    """;

                String attendanceSQL = """
                    CREATE TABLE IF NOT EXISTS attendance (
                        attendance_id INT PRIMARY KEY AUTO_INCREMENT,
                        enrollment_id INT NOT NULL,
                        attendance_date DATE NOT NULL,
                        attendance_status VARCHAR(10) NOT NULL,
                        FOREIGN KEY (enrollment_id)
                            REFERENCES enrollments(enrollment_id),
                        UNIQUE (enrollment_id, attendance_date),
                        CHECK (attendance_status IN ('PRESENT', 'ABSENT'))
                    )
                    """;

                String markSQL = """
                    CREATE TABLE IF NOT EXISTS marks (
                        marks_id INT PRIMARY KEY AUTO_INCREMENT,
                        enrollment_id INT NOT NULL,
                        assessment_name VARCHAR(20) NOT NULL,
                        marks_obtained INT NOT NULL CHECK (marks_obtained >= 0),
                        max_marks INT NOT NULL CHECK (max_marks > 0),
                        assessment_date DATE NOT NULL,
                        FOREIGN KEY (enrollment_id)
                            REFERENCES enrollments(enrollment_id),
                        CHECK (marks_obtained <= max_marks),
                        UNIQUE (enrollment_id, assessment_name)
                    )
                    """;

                createTable(con, studentSQL, "Students");
                createTable(con, courseSQL, "Courses");
                createTable(con, enrollmentSQL, "Enrollments");
                createTable(con, attendanceSQL, "Attendance");
                createTable(con, markSQL, "Marks");

                StudentService studentService = new StudentService();
                CourseService courseService = new CourseService();
                EnrollmentService enrollmentService = new EnrollmentService();
                MarksService marksService = new MarksService();
                AttendanceService attendanceService = new AttendanceService();

                while (true) {

                    System.out.println("\n========== STUDENT MANAGEMENT SYSTEM ==========");
                    System.out.println("1. Student Management");
                    System.out.println("2. Course Management");
                    System.out.println("3. Enrollment Management");
                    System.out.println("4. Attendance Management");
                    System.out.println("5. Marks and Result Management");
                    System.out.println("6. Exit");
                    System.out.print("Enter your choice: ");

                    int choice = readInt(sc);

                    try {
                        switch (choice) {

                            case 1 -> studentMenu(
                                    con, sc, studentService);

                            case 2 -> courseMenu(
                                    con, sc, courseService);

                            case 3 -> enrollmentMenu(
                                    con, sc, enrollmentService);

                            case 4 -> attendanceMenu(
                                    con, sc, attendanceService);

                            case 5 -> marksMenu(
                                    con, sc, marksService);

                            case 6 -> {
                                System.out.println("Thank you for using the application!");
                                return;
                            }

                            default -> System.out.println("Invalid choice. Try again.");
                        }

                    } catch (SQLException e) {
                        System.out.println("Database error: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Application error: " + e.getMessage());
        }
    }

// STUDENT MENU

    private static void studentMenu(Connection con, Scanner sc,
                                    StudentService service) throws SQLException {

        while (true) {
            System.out.println("\n--- STUDENT MANAGEMENT ---");
            System.out.println("1. Add Student");
            System.out.println("2. Display All Students");
            System.out.println("3. Search Student by ID");
            System.out.println("4. Search Student by Name");
            System.out.println("5. Update Email");
            System.out.println("6. Update Phone");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = readInt(sc);

            switch (choice) {
                case 1 -> service.addStudent(con, sc);

                case 2 -> service.fetchAllStudent(con);

                case 3 -> {
                    System.out.print("Enter Student ID: ");
                    int id = readInt(sc);
                    service.fetchById(con, id);
                }

                case 4 -> {
                    System.out.print("Enter Student Name: ");
                    String name = sc.nextLine();
                    service.fetchByName(con, name);
                }

                case 5 -> {
                    System.out.print("Enter Student ID: ");
                    int id = readInt(sc);
                    readInt(sc);

                    System.out.print("Enter New Email: ");
                    String email = sc.nextLine();

                    service.updateEmail(con, email, id);
                }

                case 6 -> {
                    System.out.print("Enter Student ID: ");
                    int id = readInt(sc);

                    System.out.print("Enter New Phone: ");
                    String phone = sc.nextLine();

                    service.updatePhone(con, phone, id);
                }

                case 7 -> {
                    return;
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

// COURSE MENU

    private static void courseMenu(Connection con, Scanner sc,
                                   CourseService service) throws SQLException {

        while (true) {
            System.out.println("\n--- COURSE MANAGEMENT ---");
            System.out.println("1. Add Course");
            System.out.println("2. Display All Courses");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = readInt(sc);

            switch (choice) {
                case 1 -> service.addCourse(con, sc);

                case 2 -> service.fetchAllCourses(con);

                case 3 ->{
                    return;
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

// ENROLLMENT MENU

    private static void enrollmentMenu(Connection con, Scanner sc,
                                       EnrollmentService service) throws SQLException {

        while (true) {
            System.out.println("\n--- ENROLLMENT MANAGEMENT ---");
            System.out.println("1. Enroll Student");
            System.out.println("2. Display All Enrollments");
            System.out.println("3. Display Enrollments by Student");
            System.out.println("4. Update Enrollment Status");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = readInt(sc);

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Student ID: ");
                    int studentId = readInt(sc);

                    System.out.print("Enter Course ID: ");
                    int courseId = readInt(sc);

                    service.enrollStudent(con, studentId, courseId);
                }

                case 2 -> service.fetchAllEnrollments(con);

                case 3 -> {
                    System.out.print("Enter Student ID: ");
                    int studentId = readInt(sc);

                    service.fetchEnrollmentsByStudent(con, studentId);
                }

                case 4 -> {
                    System.out.print("Enter Enrollment ID: ");
                    int enrollmentId = readInt(sc);

                    System.out.print("Enter Status (ACTIVE/COMPLETED/INACTIVE): ");
                    String status = sc.nextLine();

                    if (!status.equalsIgnoreCase("ACTIVE")
                            && !status.equalsIgnoreCase("COMPLETED")
                            && !status.equalsIgnoreCase("INACTIVE")) {
                        System.out.println("Invalid status.");
                        break;
                    }

                    service.updateEnrollmentStatus(con, enrollmentId, status);
                }

                case 5 -> {
                    return;
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

// ATTENDANCE MENU

    private static void attendanceMenu(Connection con, Scanner sc,
                                       AttendanceService service) throws SQLException {

        while (true) {
            System.out.println("\n--- ATTENDANCE MANAGEMENT ---");
            System.out.println("1. Mark Attendance");
            System.out.println("2. View Attendance by Enrollment");
            System.out.println("3. View All Attendance");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = readInt(sc);

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Enrollment ID: ");
                    int enrollmentId = readInt(sc);

                    System.out.print("Enter Status (PRESENT/ABSENT): ");
                    String status = sc.nextLine();

                    if (!status.equalsIgnoreCase("PRESENT")
                            && !status.equalsIgnoreCase("ABSENT")) {
                        System.out.println("Invalid attendance status.");
                        break;
                    }

                    service.markAttendance(con, enrollmentId, status);
                }

                case 2 -> {
                    System.out.print("Enter Enrollment ID: ");
                    int enrollmentId = readInt(sc);

                    service.fetchAttendanceByEnrollment(con, enrollmentId);
                }

                case 3 -> service.fetchAllAttendance(con);

                case 4 -> {
                    return;
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

// MARKS MENU

    private static void marksMenu(Connection con, Scanner sc,
                                  MarksService service) throws SQLException {

        while (true) {
            System.out.println("\n--- MARKS AND RESULT MANAGEMENT ---");
            System.out.println("1. Add Marks");
            System.out.println("2. View Marks by Enrollment");
            System.out.println("3. View All Marks");
            System.out.println("4. Calculate Result");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = readInt(sc);

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter Enrollment ID: ");
                    int enrollmentId = readInt(sc);

                    System.out.print("Enter Assessment Name: ");
                    String assessmentName = sc.nextLine();

                    System.out.print("Enter Marks Obtained: ");
                    int obtained = readInt(sc);

                    System.out.print("Enter Maximum Marks: ");
                    int maximum = readInt(sc);

                    service.addMarks(
                            con, enrollmentId, assessmentName, obtained, maximum);
                }

                case 2 -> {
                    System.out.print("Enter Enrollment ID: ");
                    int enrollmentId = readInt(sc);

                    service.fetchMarksByEnrollment(con, enrollmentId);
                }

                case 3 -> service.fetchAllMarks(con);

                case 4 -> {
                    System.out.print("Enter Enrollment ID: ");
                    int enrollmentId = readInt(sc);

                    service.calculateResult(con, enrollmentId);
                }

                case 5 -> {
                    return;
                }

                default -> System.out.println("Invalid choice.");
            }
        }
    }

// INTEGER INPUT HELPER

    private static int readInt(Scanner sc) {
        while (true) {
            String input = sc.nextLine();

            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid number: ");
            }
        }
    }

// TABLE CREATION

    private static void createTable(Connection con, String sql,
                                    String tableName) throws SQLException {

        try (Statement statement = con.createStatement()) {
            statement.execute(sql);
            System.out.println(tableName + " table successfully created.");
        }
    }


}
