 import java.util.ArrayList;

public class HabitManager {

    private ArrayList<Habit> habits = new ArrayList<>();

    public void addHabit(Habit habit) {
        habits.add(habit);
    }

    public void showHabits() {
        for (Habit habit : habits) {
            System.out.println(
                habit.getHabitId() + " - " +
                habit.getHabitName() + " - Completed: " +
                habit.isCompleted()
            );
        }
    }
}