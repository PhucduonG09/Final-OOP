package dao;


import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Habit;
import util.DatabaseConnection;

public class HabitDAOImpl implements HabitDAO {

    @Override
    public void addHabit(Habit habit) {
        String sql = "INSERT INTO habits (name, description, start_date) VALUES (?, ?, ?)"; //  ? de tranh loi SQL Injection

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, habit.getName());
            pstmt.setString(2, habit.getDescription());
            pstmt.setDate(3, Date.valueOf(habit.getStartDate()));

            pstmt.executeUpdate(); // Thực thi lệnh INSERT
            System.out.println("Đã thêm thói quen: " + habit.getName());

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return habits;
    }

    // Các hàm update, delete em có thể để trống tạm thời
    @Override
    public void updateHabit(Habit habit) {
        // Logic: Cập nhật tên, mô tả, ngày dựa theo ID
        String sql = "UPDATE habits SET name = ?, description = ?, start_date = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, habit.getName());
            pstmt.setString(2, habit.getDescription());
            pstmt.setDate(3, Date.valueOf(habit.getStartDate()));
            
            // Quan trọng: ID là điều kiện WHERE, nằm ở tham số thứ 4
            pstmt.setInt(4, habit.getId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Đã cập nhật Habit ID: " + habit.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteHabit(int id) {
        // Logic: Xóa dòng có ID tương ứng
        String sql = "DELETE FROM habits WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Đã xóa Habit ID: " + id);

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
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Hôm nay đã check-in rồi!");
                return false;
            }
        } catch (SQLException e) { e.printStackTrace(); }

        String insertSql = "INSERT INTO habit_logs (habit_id, date, is_completed) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            
            pstmt.setInt(1, habitId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setBoolean(3, true);
            
            pstmt.executeUpdate();
            System.out.println("Check-in thành công cho Habit ID: " + habitId);
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
}
