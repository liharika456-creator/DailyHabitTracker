 import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import com.sun.net.httpserver.HttpServer;

public class HabitServer {

    public static void main(String[] args) throws Exception {

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(8082), 0
                );


        // ==============================
        // HABITS
        // ==============================

        server.createContext("/habits", exchange -> {

            exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Origin", "*"
            );

            String method =
                    exchange.getRequestMethod();


            // ==============================
            // ADD HABIT
            // ==============================

            if (method.equalsIgnoreCase("POST")) {

                String query =
                        exchange.getRequestURI().getQuery();

                String habitName = null;


                if (query != null &&
                        query.startsWith("name=")) {

                    habitName = URLDecoder.decode(
                            query.substring(5),
                            StandardCharsets.UTF_8
                    );
                }


                if (habitName == null ||
                        habitName.trim().isEmpty()) {

                    String response =
                            "Habit name is required";

                    byte[] bytes =
                            response.getBytes();

                    exchange.sendResponseHeaders(
                            400, bytes.length
                    );

                    exchange.getResponseBody()
                            .write(bytes);

                    exchange.getResponseBody().close();

                    return;
                }


                // Generate new ID
                int newId = 1;


                for (Habit habit :
                        DatabaseManager.getAllHabits()) {

                    if (habit.getHabitId() >= newId) {

                        newId =
                                habit.getHabitId() + 1;
                    }
                }


                // Save current date and time
                DatabaseManager.addHabit(
                        newId,
                        habitName,
                        LocalDateTime.now().toString()
                );


                String response =
                        "Habit added successfully!";

                byte[] bytes =
                        response.getBytes();


                exchange.sendResponseHeaders(
                        200, bytes.length
                );

                exchange.getResponseBody()
                        .write(bytes);

                exchange.getResponseBody().close();

                return;
            }


            // ==============================
            // GET HABITS
            // ==============================

            String response = "";


            for (Habit habit :
                    DatabaseManager.getAllHabits()) {

                response +=
                        habit.getHabitId()
                        + "|"
                        + habit.getHabitName()
                        + "|"
                        + habit.getCreatedDate()
                        + "|"
                        + habit.isCompleted()
                        + "|"
                        + (
                            habit.getCompletionDate() == null
                            ? ""
                            : habit.getCompletionDate()
                          )
                        + "\n";
            }


            byte[] bytes =
                    response.getBytes();


            exchange.sendResponseHeaders(
                    200, bytes.length
            );

            exchange.getResponseBody()
                    .write(bytes);

            exchange.getResponseBody().close();
        });


        // ==============================
        // COMPLETE HABIT
        // ==============================

        server.createContext("/complete", exchange -> {

            exchange.getResponseHeaders().add(
                    "Access-Control-Allow-Origin", "*"
            );


            String query =
                    exchange.getRequestURI().getQuery();

            int habitId = 0;


            if (query != null &&
                    query.startsWith("id=")) {

                habitId =
                        Integer.parseInt(
                                query.substring(3)
                        );
            }


            if (habitId == 0) {

                String response =
                        "Habit ID is required";

                byte[] bytes =
                        response.getBytes();

                exchange.sendResponseHeaders(
                        400, bytes.length
                );

                exchange.getResponseBody()
                        .write(bytes);

                exchange.getResponseBody().close();

                return;
            }


            DatabaseManager.completeHabit(
                    habitId,
                    LocalDateTime.now().toString()
            );


            String response =
                    "Habit completed successfully!";

            byte[] bytes =
                    response.getBytes();


            exchange.sendResponseHeaders(
                    200, bytes.length
            );

            exchange.getResponseBody()
                    .write(bytes);

            exchange.getResponseBody().close();
        });

// ==============================
// COMPLETED HISTORY
// ==============================

server.createContext("/history", exchange -> {

    exchange.getResponseHeaders().add(
            "Access-Control-Allow-Origin", "*"
    );

    String response =
            DatabaseManager.getCompletedHistory();

    byte[] bytes =
            response.getBytes();


    exchange.sendResponseHeaders(
            200,
            bytes.length
    );

    exchange.getResponseBody()
            .write(bytes);

    exchange.getResponseBody().close();
});


// ==============================
// DELETE HABIT
// ==============================

server.createContext("/delete", exchange -> {

    exchange.getResponseHeaders().add(
            "Access-Control-Allow-Origin", "*"
    );


    String query =
            exchange.getRequestURI().getQuery();


    int habitId = 0;


    if (query != null &&
            query.startsWith("id=")) {

        habitId =
                Integer.parseInt(
                        query.substring(3)
                );
    }


    if (habitId == 0) {

        String response =
                "Habit ID is required";

        byte[] bytes =
                response.getBytes();


        exchange.sendResponseHeaders(
                400,
                bytes.length
        );


        exchange.getResponseBody()
                .write(bytes);

        exchange.getResponseBody()
                .close();

        return;
    }


    DatabaseManager.deleteHabit(
            habitId
    );


    String response =
            "Habit deleted successfully!";

    byte[] bytes =
            response.getBytes();


    exchange.sendResponseHeaders(
            200,
            bytes.length
    );


    exchange.getResponseBody()
            .write(bytes);

    exchange.getResponseBody()
            .close();
});
        // ==============================
        // START SERVER
        // ==============================



        // ==============================
// CLEAR COMPLETED HISTORY
// ==============================

server.createContext("/clear-history", exchange -> {

    exchange.getResponseHeaders().add(
            "Access-Control-Allow-Origin", "*"
    );


    DatabaseManager.deleteCompletedHistory();


    String response =
            "Completed history deleted successfully!";

    byte[] bytes =
            response.getBytes();


    exchange.sendResponseHeaders(
            200,
            bytes.length
    );


    exchange.getResponseBody()
            .write(bytes);

    exchange.getResponseBody()
            .close();
});
        server.start();


        System.out.println(
                "Server started at http://localhost:8082"
        );
    }
}