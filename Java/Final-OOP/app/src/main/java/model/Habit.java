package model;

import java.time.LocalDate;

public class Habit {
    private int id;
    private String name;
    private String description;
    private LocalDate startDate; 
    private int currentStreak;

    public Habit() {}

    public Habit(int id, String name, String description, LocalDate startDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
    }

    
    public Habit(String name, String description, LocalDate startDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
    }

    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
}
