package controller; 

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn; 
import javafx.scene.control.TableView;   
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleObjectProperty; 
import javafx.scene.control.ButtonType;
import java.util.Optional;

import model.Habit;
import service.StreakService;
import dao.HabitDAO;
import dao.HabitDAOImpl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class MainController {

    @FXML private TableView<Habit> habitTable;
    @FXML private TableColumn<Habit, Integer> idColumn;
    @FXML private TableColumn<Habit, String> nameColumn;
    @FXML private TableColumn<Habit, String> descColumn;
    @FXML private TableColumn<Habit, LocalDate> dateColumn;
    @FXML private TableColumn<Habit, Integer> streakColumn;

    @FXML private TextField nameInput;
    @FXML private TextField descInput;

    @FXML private PieChart pieChart;
    @FXML private Label lblAnalysis;
    @FXML private TabPane chartContainer;
    @FXML private Label placeholderLabel;
    @FXML private SplitPane mainSplitPane;
    @FXML private StackPane rightPane;  
    @FXML private ToggleButton btnToggleStats;
    @FXML private BarChart<String, Number> barChart;
    
    private HabitDAO habitDAO = new HabitDAOImpl();
    
    private ObservableList<Habit> habitList;

    
    @FXML
    public void initialize() {
        try {
            String cssPath = getClass().getResource("/decor.css").toExternalForm();
            pieChart.getStylesheets().add(cssPath);
        } catch (Exception e) {e.printStackTrace();}

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateColumn.setCellFactory(column -> new TableCell<Habit, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(formatter.format(date));
                }
            }
        });

        streakColumn.setCellValueFactory(data -> {
        Habit habit = data.getValue();
        int streak = habit.getCurrentStreak();
        
        return new SimpleObjectProperty<>(streak);
        });
        loadData();

        chartContainer.setVisible(false);
        placeholderLabel.setVisible(true);

        habitTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    updateChart(newSelection);
                    chartContainer.setVisible(true);
                    placeholderLabel.setVisible(false);
                } else {
                    chartContainer.setVisible(false);
                    placeholderLabel.setVisible(true);
                }
            }
        );
        mainSplitPane.getItems().remove(rightPane);
    }

    private void loadData() {
        List<Habit> list = habitDAO.getAllHabits();
        for (Habit h : list) {
            List<LocalDate> logs = habitDAO.getLogDates(h.getId());
            int streak = StreakService.calculateStreak(logs);
            h.setCurrentStreak(streak);
        }
        
        habitList = FXCollections.observableArrayList(list);
        habitTable.setItems(habitList);
    }

    @FXML
    public void handleAddHabit() {
        String name = nameInput.getText().trim();
        String desc = descInput.getText().trim();

        if (name.isEmpty()) {
            showAlert("Lỗi", "Tên thói quen không được để trống!");
            return;
        }

        if (name.length() > 30) {
            showAlert("Lỗi", "Tên thói quen quá dài, tối đa 30 ký tự!");
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
        
        if (selected == null) {
            showAlert("Cảnh báo", "Vui lòng chọn thói quen cần xóa!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa thói quen");
        alert.setHeaderText("Bạn có chắc chắn muốn xóa: " + selected.getName() + "?");
        alert.setContentText("Lưu ý: Mọi lịch sử check-in của thói quen này cũng sẽ bị mất vĩnh viễn.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            habitDAO.deleteHabit(selected.getId());
            loadData();
            if (habitList.isEmpty()) {
                chartContainer.setVisible(false);
                placeholderLabel.setVisible(true);
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

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

    private void updateChart(Habit selected) {
        if (selected == null) return;

        List<LocalDate> logs = habitDAO.getLogDates(selected.getId());
        int completedDays = logs.size();

        long totalDays = ChronoUnit.DAYS.between(selected.getStartDate(), LocalDate.now()) + 1;
        if (totalDays < 1) totalDays = 1;

        long missedDays = totalDays - completedDays;
        if (missedDays < 0) missedDays = 0;

        PieChart.Data dataCompleted = new PieChart.Data("Đã làm (" + completedDays + ")", completedDays);
        PieChart.Data dataMissed = new PieChart.Data("Bỏ lỡ (" + missedDays + ")", missedDays);

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(dataCompleted, dataMissed);

        pieChart.setData(pieData);

        double rate = (double) completedDays / totalDays * 100;
        String msg = String.format("Bạn đã hoàn thành %.1f%% mục tiêu.", rate);
        lblAnalysis.setText(msg);

        updateMonthlyChart(selected);
    }

    @FXML
    public void handleToggleStats() {
        if (btnToggleStats.isSelected()) {
            btnToggleStats.setText("Ẩn Thống Kê");
            
            if (!mainSplitPane.getItems().contains(rightPane)) {
                mainSplitPane.getItems().add(rightPane);
                mainSplitPane.setDividerPositions(0.6);
            }
        } else {
            btnToggleStats.setText("Hiện Thống Kê");
            mainSplitPane.getItems().remove(rightPane);
        }
    }

    @FXML
    private void updateMonthlyChart(Habit selected) {
        barChart.getData().clear();
        Map<Integer, Integer> stats = habitDAO.getMonthlyStats(selected.getId());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tháng");

        for (int i = 1; i <= 12; i++) {
            String monthLabel = "T" + i;
            int count = stats.getOrDefault(i, 0);
            series.getData().add(new XYChart.Data<>(monthLabel, count));
        }
        barChart.getData().add(series);
    }
}