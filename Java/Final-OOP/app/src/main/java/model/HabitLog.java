package model;

import java.time.LocalDate;

public class HabitLog {
    private int id;
    private int habitId;
    private LocalDate date;
    private boolean isCompleted;

    public HabitLog() {}
    
    public HabitLog(int id, int habitId, LocalDate date, boolean isCompleted) {
            this.id = id;
            this.habitId = habitId;
            this.date = date;
            this.isCompleted = isCompleted;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    public int getHabitId() { return habitId; }
    public void setHabitId(int habitId) { this.habitId = habitId; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }


    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean isCompleted) { this.isCompleted = isCompleted; }
}

    
