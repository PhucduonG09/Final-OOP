package dao;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Habit;
import util.DatabaseConnection;

public class HabitDAOImpl implements HabitDAO {

    @Override
    public void addHabit(Habit habit) {
        String sql = "INSERT INTO habits (name, description, start_date) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, habit.getName());
            pstmt.setString(2, habit.getDescription());
            pstmt.setDate(3, Date.valueOf(habit.getStartDate()));

            pstmt.executeUpdate();

        } catch (SQLException e) {e.printStackTrace();}
    }

    @Override
    public List<Habit> getAllHabits() {
        List<Habit> habits = new ArrayList<>();
        String sql = "SELECT * FROM habits";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Habit habit = new Habit();
                habit.setId(rs.getInt("id"));
                habit.setName(rs.getString("name"));
                habit.setDescription(rs.getString("description"));
                habit.setStartDate(rs.getDate("start_date").toLocalDate());

                habits.add(habit);
            }
        } catch (SQLException e) {e.printStackTrace();}
        return habits;
    }

    @Override
    public void deleteHabit(int id) {
        String sql = "DELETE FROM habits WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public boolean checkInToday(int habitId, LocalDate date) {
        String checkSql = "SELECT COUNT(*) FROM habit_logs WHERE habit_id = ? AND date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, habitId);
            checkStmt.setDate(2, Date.valueOf(date));
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {return false;}
        } catch (SQLException e) { e.printStackTrace(); }

        String insertSql = "INSERT INTO habit_logs (habit_id, date, is_completed) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            
            pstmt.setInt(1, habitId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setBoolean(3, true);
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<LocalDate> getLogDates(int habitId) {
        List<LocalDate> dates = new ArrayList<>();
        String sql = "SELECT DISTINCT date FROM habit_logs WHERE habit_id = ? ORDER BY date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, habitId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                dates.add(rs.getDate("date").toLocalDate());
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return dates;
    }

    @Override
    public Map<Integer, Integer> getMonthlyStats(int habitId) {
        Map<Integer, Integer> stats = new HashMap<>();

        String sql = "SELECT MONTH(date) as month, COUNT(*) as count " +
                    "FROM habit_logs " +
                    "WHERE habit_id = ? AND YEAR(date) = YEAR(CURDATE()) " +
                    "GROUP BY MONTH(date)";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, habitId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                stats.put(rs.getInt("month"), rs.getInt("count"));
            }
        } catch (SQLException e) {e.printStackTrace();}
        return stats;
    }
}
