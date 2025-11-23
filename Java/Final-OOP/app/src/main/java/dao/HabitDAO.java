package dao;


import java.time.LocalDate;
import java.util.List;

import model.Habit;

public interface HabitDAO {
    void addHabit(Habit habit);
    List<Habit> getAllHabits();
    void updateHabit(Habit habit);
    void deleteHabit(int id);
    boolean checkInToday(int habitId, LocalDate date);
    List<LocalDate> getLogDates(int habitId);
}
