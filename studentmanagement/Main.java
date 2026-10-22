package studentmanagement;

import studentmanagement.service.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class Main {

    static final String DB_URL = "jdbc:mysql://localhost:3306/student_management";
    static final String USER = "root";
    static final String PASS = "root";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(DB_URL, USER, PASS);
            Scanner sc = new Scanner(System.in);
            

            StudentService studentService = new StudentService();
            CourseService courseService = new CourseService();
            EnrollmentService enrollmentService = new EnrollmentService();
            MarksService marksService = new MarksService();
            AttendanceService attendanceService = new AttendanceService();

            boolean isRunning = true;

            while (isRunning) {
                System.out.println("\n------------------- STUDENT MANAGEMENT SYSTEM --------------");
                System.out.println("1. Student Management");
                System.out.println("2. Course Management");
                System.out.println("3. Enrollment Management");
                System.out.println("4. Attendance Management");
                System.out.println("5. Marks and Result Management");
                System.out.println("6. Exit");
                System.out.print("Enter your choice: ");

                try {
                    int choice = Integer.parseInt(sc.nextLine());

                    switch (choice) {
                        case 1:
                            studentMenu(con, sc, studentService);
                            break;
                        case 2:
                            courseMenu(con, sc, courseService);
                            break;
                        case 3:
                            enrollmentMenu(con, sc, enrollmentService);
                            break;
                        case 4:
                            attendanceMenu(con, sc, attendanceService);
                            break;
                        case 5:
                            marksMenu(con, sc, marksService);
                            break;
                        case 6:
                            System.out.println("Thank you for using the application!");
                            isRunning = false;
                            break;
                        default:
                            System.out.println("Invalid choice. Try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid number.");
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            
            sc.close();
            con.close();

        } catch (Exception e) {
            System.out.println("Application error...");
            e.printStackTrace();
        }
    }

    private static void studentMenu(Connection con, Scanner sc, StudentService service) throws Exception {
        boolean back = false;
        while (!back) {
            System.out.println("\n------------ STUDENT MANAGEMENT ---------------");
            System.out.println("1. Add Student");
            System.out.println("2. Display All Students");
            System.out.println("3. Search Student by ID");
            System.out.println("4. Search Student by Name");
            System.out.println("5. Update Email");
            System.out.println("6. Update Phone");
            System.out.println("7. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    service.addStudent(con, sc);
                    break;
                case 2:
                    service.fetchAllStudent(con);
                    break;
                case 3:
                    System.out.print("Enter Student ID: ");
                    int id = Integer.parseInt(sc.nextLine());
                    service.fetchById(con, id);
                    break;
                case 4:
                    System.out.print("Enter Student Name: ");
                    String name = sc.nextLine();
                    service.fetchByName(con, name);
                    break;
                case 5:
                    System.out.print("Enter Student ID: ");
                    int updateId = Integer.parseInt(sc.nextLine());
                    System.out.print("Enter New Email: ");
                    String email = sc.nextLine();
                    service.updateEmail(con, email, updateId);
                    break;
                case 6:
                    System.out.print("Enter Student ID: ");
                    int pId = Integer.parseInt(sc.nextLine());
                    System.out.print("Enter New Phone: ");
                    String phone = sc.nextLine();
                    service.updatePhone(con, phone, pId);
                    break;
                case 7:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void courseMenu(Connection con, Scanner sc, CourseService service) throws Exception {
        boolean back = false;
        while (!back) {
            System.out.println("\n------------ COURSE MANAGEMENT ------------");
            System.out.println("1. Add Course");
            System.out.println("2. Display All Courses");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    service.addCourse(con, sc);
                    break;
                case 2:
                    service.fetchAllCourses(con);
                    break;
                case 3:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void enrollmentMenu(Connection con, Scanner sc, EnrollmentService service) throws Exception {
        boolean back = false;
        while (!back) {
            System.out.println("\n------------- ENROLLMENT MANAGEMENT --------------");
            System.out.println("1. Enroll Student");
            System.out.println("2. Display All Enrollments");
            System.out.println("3. Display Enrollments by Student");
            System.out.println("4. Update Enrollment Status");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("Enter Student ID: ");
                    int studentId = Integer.parseInt(sc.nextLine());

                    System.out.print("Enter Course ID: ");
                    int courseId = Integer.parseInt(sc.nextLine());

                    service.enrollStudent(con, studentId, courseId);
                    break;
                case 2:
                    service.fetchAllEnrollments(con);
                    break;
                case 3:
                    System.out.print("Enter Student ID: ");
                    int sId = Integer.parseInt(sc.nextLine());
                    service.fetchEnrollmentsByStudent(con, sId);
                    break;
                case 4:
                    System.out.print("Enter Enrollment ID: ");
                    int enrollId = Integer.parseInt(sc.nextLine());

                    System.out.print("Enter Status (ACTIVE/COMPLETED/INACTIVE): ");
                    String status = sc.nextLine().toUpperCase();

                    service.updateEnrollmentStatus(con, enrollId, status);
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void attendanceMenu(Connection con, Scanner sc, AttendanceService service) throws Exception {
        boolean back = false;
        while (!back) {
            System.out.println("\n---------- ATTENDANCE MANAGEMENT -------------");
            System.out.println("1. Mark Attendance");
            System.out.println("2. View Attendance by Enrollment");
            System.out.println("3. View All Attendance");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("Enter Enrollment ID: ");
                    int enrollId = Integer.parseInt(sc.nextLine());

                    System.out.print("Enter Status (PRESENT/ABSENT): ");
                    String status = sc.nextLine().toUpperCase();

                    service.markAttendance(con, enrollId, status);
                    break;
                case 2:
                    System.out.print("Enter Enrollment ID: ");
                    int eId = Integer.parseInt(sc.nextLine());

                    service.fetchAttendanceByEnrollment(con, eId);
                    break;
                case 3:
                    service.fetchAllAttendance(con);
                    break;
                case 4:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void marksMenu(Connection con, Scanner sc, MarksService service) throws Exception {
        boolean back = false;
        while (!back) {
            System.out.println("\n------------- MARKS AND RESULT MANAGEMENT -------------");
            System.out.println("1. Add Marks");
            System.out.println("2. View Marks by Enrollment");
            System.out.println("3. View All Marks");
            System.out.println("4. Calculate Result");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("Enter Enrollment ID: ");
                    int enrollId = Integer.parseInt(sc.nextLine());

                    System.out.print("Enter Assessment Name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter Marks Obtained: ");
                    int obtained = Integer.parseInt(sc.nextLine());

                    System.out.print("Enter Maximum Marks: ");
                    int max = Integer.parseInt(sc.nextLine());
                    
                    service.addMarks(con, enrollId, name, obtained, max);
                    break;
                case 2:
                    System.out.print("Enter Enrollment ID: ");
                    int eId = Integer.parseInt(sc.nextLine());
                    service.fetchMarksByEnrollment(con, eId);
                    break;
                case 3:
                    service.fetchAllMarks(con);
                    break;
                case 4:
                    System.out.print("Enter Enrollment ID: ");
                    int calcId = Integer.parseInt(sc.nextLine());
                    service.calculateResult(con, calcId);
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

     
}