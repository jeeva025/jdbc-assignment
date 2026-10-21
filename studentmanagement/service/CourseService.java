package studentmanagement.service;


import javax.xml.transform.Result;
import java.sql.*;
import java.util.Scanner;

public class CourseService {
    public void addCourse(Connection con, Scanner sc) throws SQLException {


        System.out.print("Enter Course Name : ");
        String courseName = sc.nextLine();

        System.out.print("Enter Course Duration in Months : ");
        int duration = sc.nextInt();

        if (isDurationValid(duration)){
            String sql = "INSERT INTO courses (course_name, duration) VALUES (?, ?)";

            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1,courseName);
            statement.setInt(2,duration);

            statement.executeUpdate();

            System.out.println("Inserted");
        }
        else {
            System.out.println("Invalid Duration. Duration must between 3 to 10 months");
        }
    }

    public void fetchAllCourses(Connection con) {
        String sql = "SELECT * FROM courses";
        try( Statement statement = con.createStatement()){

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()){
                System.out.println("Course ID : "+resultSet.getInt("course_id") + " | Course Name : "+resultSet.getString("course_name") + " | duration : "+resultSet.getInt("duration"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public  void displayDetails(ResultSet result) throws SQLException {
        boolean found = false;

        while (result.next()){
            found = true;
            System.out.println("Course ID : "+result.getInt("course_id") + " | "+ "Course Name : "+result.getString("course_name"));
        }

        if (!found){
            System.out.println("No students found");
        }
    }


    public boolean isDurationValid(int months) {
        return months >= 3 && months <= 10;
    }


}

