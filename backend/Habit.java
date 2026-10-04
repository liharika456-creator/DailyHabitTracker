 public class Habit {

    private int habitId;
    private String habitName;
    private boolean completed;
    private String createdDate;
    private String completionDate;


    public Habit(
            int habitId,
            String habitName,
            boolean completed,
            String createdDate,
            String completionDate) {

        this.habitId = habitId;
        this.habitName = habitName;
        this.completed = completed;
        this.createdDate = createdDate;
        this.completionDate = completionDate;
    }


    public int getHabitId() {
        return habitId;
    }


    public String getHabitName() {
        return habitName;
    }


    public boolean isCompleted() {
        return completed;
    }


    public String getCreatedDate() {
        return createdDate;
    }


    public String getCompletionDate() {
        return completionDate;
    }


    public void markCompleted() {
        completed = true;
    }


    public void markIncomplete() {
        completed = false;
    }
}