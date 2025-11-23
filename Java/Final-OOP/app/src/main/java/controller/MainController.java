package controller; 

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn; 
import javafx.scene.control.TableView;   
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleObjectProperty; 

import model.Habit;
import service.StreakService;
import dao.HabitDAO;
import dao.HabitDAOImpl;

import java.time.LocalDate;
import java.util.List;

public class MainController {

    @FXML private TableView<Habit> habitTable;
    @FXML private TableColumn<Habit, Integer> idColumn;
    @FXML private TableColumn<Habit, String> nameColumn;
    @FXML private TableColumn<Habit, String> descColumn;
    @FXML private TableColumn<Habit, LocalDate> dateColumn;
    @FXML private TableColumn<Habit, Integer> steakColumn;

    @FXML private TextField nameInput;
    @FXML private TextField descInput;

    
    private HabitDAO habitDAO = new HabitDAOImpl();
    
    private ObservableList<Habit> habitList;

    
    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        streakColumn.setCellValueFactory(data -> {
        Habit habit = data.getValue();
        int streak = habit.getCurrentStreak();
        
        return new SimpleObjectProperty<>(streak);
        });
        loadData();
    }

    private void loadData() {
        List<Habit> list = habitDAO.getAllHabits();
        for (Habit h : list) {
            List<LocalDate> logs = habitDAO.getLogDates(h.getId());

            // System.out.println("Đang tính Streak cho: " + h.getName());
            // System.out.println("Dữ liệu lấy được từ DB: " + logs);
            int streak = StreakService.calculateStreak(logs);
            // System.out.println("=> Kết quả Streak: " + streak);
            h.setCurrentStreak(streak);
        }
        
        habitList = FXCollections.observableArrayList(list);
        habitTable.setItems(habitList);
    }

    @FXML
    public void handleAddHabit() {
        String name = nameInput.getText();
        String desc = descInput.getText();

        if (name.isEmpty()) {
            showAlert("Lỗi", "Tên thói quen không được để trống!");
            return;
        }

        Habit newHabit = new Habit(name, desc, LocalDate.now());
        habitDAO.addHabit(newHabit); 
        
        loadData(); 
        nameInput.clear();
        descInput.clear();
    }
    
    @FXML
    public void handleDeleteHabit() {
        Habit selected = habitTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            habitDAO.deleteHabit(selected.getId());
            loadData();
        } else {
            showAlert("Cảnh báo", "Vui lòng chọn thói quen cần xóa!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private TableColumn<Habit, Integer> streakColumn; 


    @FXML
    public void handleCheckIn() {
        Habit selected = habitTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Thông báo", "Vui lòng chọn một thói quen để Check-in!");
            return;
        }

        boolean success = habitDAO.checkInToday(selected.getId(), LocalDate.now());
        
        if (success) {
            showAlert("Thành công", "Tuyệt vời! Bạn đã hoàn thành mục tiêu hôm nay.");
            loadData(); 
        } else {
            showAlert("Thông báo", "Hôm nay bạn đã check-in thói quen này rồi.");
        }
    }
}