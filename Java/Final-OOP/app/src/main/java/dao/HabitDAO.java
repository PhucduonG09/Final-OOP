package dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import model.Habit;

public interface HabitDAO {
    void addHabit(Habit habit);
    List<Habit> getAllHabits();
    void deleteHabit(int id);
    boolean checkInToday(int habitId, LocalDate date);
    List<LocalDate> getLogDates(int habitId);
    Map<Integer, Integer> getMonthlyStats(int habitId);
}
