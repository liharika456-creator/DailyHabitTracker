 import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        DatabaseManager.addHabit(4, "Meditation", "2026-10-03");

        ArrayList<Habit> habits = DatabaseManager.getAllHabits();

        for (Habit habit : habits) {
            System.out.println(
                habit.getHabitId() + " - " +
                habit.getHabitName()
            );
        }
    }
}