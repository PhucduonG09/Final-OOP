# Habit Tracker App

> Ứng dụng theo dõi thói quen hàng ngày, giúp bạn xây dựng kỷ luật bản thân thông qua việc ghi nhận (Check-in) và thống kê trực quan.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-007396?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

## Demo Giao diện

| Dashboard Chính | Thống kê Hiệu suất |
<img width="995" height="783" alt="Screenshot 2025-12-07 105756" src="https://github.com/user-attachments/assets/e5d3c0e6-3aa8-4362-90b4-7bbfeee9ae7f" />


## Tính năng nổi bật

### 1. Quản lý Thói quen (CRUD)
- [x] Thêm thói quen mới (Có kiểm tra dữ liệu đầu vào).
- [x] Xóa thói quen (Có hộp thoại xác nhận an toàn).
- [x] Hiển thị danh sách trực quan với bảng dữ liệu.

### 2. Hệ thống Check-in & Streak 
- [x] **Check-in hàng ngày:** Ghi nhận hoàn thành công việc chỉ với 1 cú click.
- [x] **Thuật toán Streak:** Tự động tính chuỗi ngày liên tiếp (Streak).
    - Nếu làm liên tục: Streak tăng.
    - Nếu bỏ lỡ 1 ngày: Streak tự reset (logic thông minh).

### 3. Thống kê & Trực quan hóa dữ liệu 
- [x] **Biểu đồ tròn (Pie Chart):** Xem tỷ lệ hoàn thành vs. bỏ lỡ.
- [x] **Biểu đồ cột (Bar Chart):** Theo dõi tần suất thực hiện theo từng tháng trong năm.
- [x] **Giao diện Dashboard:** Tích hợp TabPane để chuyển đổi linh hoạt giữa các biểu đồ.

### 4. Kỹ thuật & Công nghệ
- **Mô hình MVC:** Tách biệt rõ ràng Model, View, Controller.
- **DAO Pattern:** Xử lý truy vấn Database chuyên nghiệp.
- **CSS Styling:** Tùy biến giao diện JavaFX hiện đại (Flat design).
- **Validation:** Chặn lỗi nhập liệu rỗng, giới hạn ký tự.

## Hướng phát triển (Future Roadmap)

- [ ] **Dark Mode:** Thêm chế độ giao diện tối.
- [ ] **Nhắc nhở:** Tích hợp Notification để nhắc người dùng check-in đúng giờ.
- [ ] **Export Data:** Xuất báo cáo ra file Excel/PDF.
- [ ] **User Login:** Hỗ trợ nhiều người dùng đăng nhập.

---

## 🛠️ Cài đặt & Hướng dẫn chạy

### 1. Yêu cầu hệ thống
- JDK 21 trở lên.
- MySQL Server (Khuyên dùng MySQL Workbench).
- IDE: VS Code hoặc IntelliJ IDEA

### 2. Cấu hình Database
Chạy script SQL sau trong MySQL Workbench để tạo CSDL:

```sql
CREATE DATABASE habit_tracker;
USE habit_tracker;

CREATE TABLE habits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL
);

CREATE TABLE habit_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    habit_id INT,
    date DATE,
    is_completed BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (habit_id) REFERENCES habits(id) ON DELETE CASCADE
);
```
### 3. Cập nhật kết nối
Mở file: src/main/java/util/DatabaseConnection.java thay đổi thông tin:
```java
private static final String user = "root";      // Tên đăng nhập MySQL của bạn
private static final String password = "YOUR_PASSWORD"; // Mật khẩu của bạn
```

### 4. Chạy ứng dụng
# Windows
.\gradlew clean (Xóa bản build cũ)
.\gradlew run hoặc .\gradlew run --no-configuration-cache

# MacOS / Linux
.\gradlew clean (Xóa bản build cũ)
.\gradlew run hoặc .\gradlew run --no-configuration-cache

---
*Cảm ơn thầy/cô và các bạn đã quan tâm đến dự án này!*
