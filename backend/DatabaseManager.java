 import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class DatabaseManager {

    private static final String URL =
            "jdbc:mysql://localhost:3306/DailyHabitTracker";

    private static final String USER =
            "root";

    private static final String PASSWORD =
        System.getenv("DB_PASSWORD");


    // ==============================
    // DATABASE CONNECTION
    // ==============================

    public static Connection getConnection()
            throws Exception {

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }


    // ==============================
    // GET ALL HABITS
    // ==============================

    public static ArrayList<Habit> getAllHabits() {

        ArrayList<Habit> habits =
                new ArrayList<>();


        String query =
                "SELECT h.habit_id, " +
                "h.habit_name, " +
                "h.created_date, " +
                "l.completion_date, " +
                "COALESCE(l.is_completed, FALSE) " +
                "AS is_completed " +
                "FROM Habits h " +
                "LEFT JOIN Habit_Logs l " +
                "ON h.habit_id = l.habit_id " +
                "AND DATE(l.completion_date) = CURDATE()";


        try {

            Connection connection =
                    getConnection();


            Statement statement =
                    connection.createStatement();


            ResultSet resultSet =
                    statement.executeQuery(query);


            while (resultSet.next()) {

                int id =
                        resultSet.getInt(
                                "habit_id"
                        );


                String name =
                        resultSet.getString(
                                "habit_name"
                        );


                String createdDate =
                        resultSet.getString(
                                "created_date"
                        );


                String completionDate =
                        resultSet.getString(
                                "completion_date"
                        );


                boolean completed =
                        resultSet.getBoolean(
                                "is_completed"
                        );


                Habit habit =
                        new Habit(
                                id,
                                name,
                                completed,
                                createdDate,
                                completionDate
                        );


                habits.add(habit);

            }


            connection.close();

        }

        catch (Exception e) {

            e.printStackTrace();

        }


        return habits;

    }


    // ==============================
    // ADD HABIT
    // ==============================

    public static void addHabit(
            int id,
            String name,
            String createdDate) {


        String query =
                "INSERT INTO Habits " +
                "(habit_id, habit_name, created_date) " +
                "VALUES (?, ?, ?)";


        try {

            Connection connection =
                    getConnection();


            PreparedStatement statement =
                    connection.prepareStatement(
                            query
                    );


            statement.setInt(
                    1,
                    id
            );


            statement.setString(
                    2,
                    name
            );


            statement.setString(
                    3,
                    createdDate
            );


            statement.executeUpdate();


            System.out.println(
                    "Habit added successfully!"
            );


            connection.close();

        }

        catch (Exception e) {

            e.printStackTrace();

        }

    }


    // ==============================
    // COMPLETE HABIT
    // ==============================

    public static void completeHabit(
            int habitId,
            String completionDate) {


        try {

            Connection connection =
                    getConnection();


            String checkQuery =
                    "SELECT log_id " +
                    "FROM Habit_Logs " +
                    "WHERE habit_id = ? " +
                    "AND DATE(completion_date) = CURDATE()";


            PreparedStatement checkStatement =
                    connection.prepareStatement(
                            checkQuery
                    );


            checkStatement.setInt(
                    1,
                    habitId
            );


            ResultSet resultSet =
                    checkStatement.executeQuery();


            if (resultSet.next()) {

                System.out.println(
                        "Habit already completed today!"
                );


                connection.close();

                return;

            }


            int newLogId = 1;


            String idQuery =
                    "SELECT COALESCE(MAX(log_id), 0) + 1 " +
                    "FROM Habit_Logs";


            Statement idStatement =
                    connection.createStatement();


            ResultSet idResult =
                    idStatement.executeQuery(
                            idQuery
                    );


            if (idResult.next()) {

                newLogId =
                        idResult.getInt(1);

            }


            String insertQuery =
                    "INSERT INTO Habit_Logs " +
                    "(log_id, habit_id, completion_date, is_completed) " +
                    "VALUES (?, ?, ?, ?)";


            PreparedStatement statement =
                    connection.prepareStatement(
                            insertQuery
                    );


            statement.setInt(
                    1,
                    newLogId
            );


            statement.setInt(
                    2,
                    habitId
            );


            statement.setString(
                    3,
                    completionDate
            );


            statement.setBoolean(
                    4,
                    true
            );


            statement.executeUpdate();


            System.out.println(
                    "Habit completed successfully!"
            );


            connection.close();

        }

        catch (Exception e) {

            e.printStackTrace();

        }

    }


    // ==============================
    // GET COMPLETED HISTORY
    // ==============================

    public static String getCompletedHistory() {

        StringBuilder history =
                new StringBuilder();


        String query =
                "SELECT h.habit_name, " +
                "l.completion_date " +
                "FROM Habit_Logs l " +
                "JOIN Habits h " +
                "ON l.habit_id = h.habit_id " +
                "WHERE l.is_completed = TRUE " +
                "AND DATE(l.completion_date) < CURDATE() " +
                "ORDER BY l.completion_date DESC";


        try {

            Connection connection =
                    getConnection();


            PreparedStatement statement =
                    connection.prepareStatement(
                            query
                    );


            ResultSet resultSet =
                    statement.executeQuery();


            while (resultSet.next()) {

                String habitName =
                        resultSet.getString(
                                "habit_name"
                        );


                String completionDate =
                        resultSet.getString(
                                "completion_date"
                        );


                history.append(
                        habitName
                )
                .append("|")
                .append(
                        completionDate
                )
                .append("\n");

            }


            connection.close();

        }

        catch (Exception e) {

            e.printStackTrace();

        }


        return history.toString();

    }


    // ==============================
    // CLEAR ALL COMPLETED HABITS
    // ==============================

    public static void deleteCompletedHistory() {

        String query =
                "DELETE FROM Habit_Logs " +
                "WHERE is_completed = TRUE";


        try {

            Connection connection =
                    getConnection();


            PreparedStatement statement =
                    connection.prepareStatement(
                            query
                    );


            statement.executeUpdate();


            System.out.println(
                    "All completed habits deleted!"
            );


            connection.close();

        }

        catch (Exception e) {

            e.printStackTrace();

        }

    }


    // ==============================
    // DELETE HABIT
    // ==============================

    public static void deleteHabit(
            int habitId) {


        try {

            Connection connection =
                    getConnection();


            String logQuery =
                    "DELETE FROM Habit_Logs " +
                    "WHERE habit_id = ?";


            PreparedStatement logStatement =
                    connection.prepareStatement(
                            logQuery
                    );


            logStatement.setInt(
                    1,
                    habitId
            );


            logStatement.executeUpdate();


            String habitQuery =
                    "DELETE FROM Habits " +
                    "WHERE habit_id = ?";


            PreparedStatement habitStatement =
                    connection.prepareStatement(
                            habitQuery
                    );


            habitStatement.setInt(
                    1,
                    habitId
            );


            habitStatement.executeUpdate();


            System.out.println(
                    "Habit deleted successfully!"
            );


            connection.close();

        }

        catch (Exception e) {

            e.printStackTrace();

        }

    }

}