# 🏨 TravelApp - Tổng hợp Chức năng Đồ án

Hệ thống quản lý đặt phòng khách sạn thông minh sử dụng **Jetpack Compose** và **Firebase**.

---

## 👤 1. Phân hệ Người dùng (Traveler)

### 🔐 Xác thực & Hồ sơ
*   **Đăng ký/Đăng nhập**: Hỗ trợ đăng ký tài khoản mới và đăng nhập bằng Email/Password.
*   **Quản lý hồ sơ cá nhân**: 
    *   Xem và cập nhật thông tin (Họ tên, Số điện thoại).
    *   **Thay đổi ảnh đại diện**: Tích hợp nén ảnh (`ImageUtils`) và lưu trữ đám mây qua **Cloudinary**.
    *   Đăng xuất an toàn.

### 🔍 Khám phá & Tìm kiếm
*   **Explore**: Danh sách khách sạn du lịch được hiển thị thời gian thực.
*   **Bộ lọc thông minh**: Lọc theo danh mục Khách sạn và tìm kiếm theo tên 
    *   Thả tim lưu lại địa điểm yêu thích.
    *   Xem danh sách các địa điểm đã lưu riêng biệt.
    *   Đồng bộ số lượng yêu thích trực tiếp vào trang Profile.

### 📅 Đặt phòng (Booking Flow)
*   **Xem chi tiết**: Hiển thị ảnh, mô tả, tiện ích và rating của khách sạn.
*   **Chọn lịch**: Bộ chọn ngày (Date Range Picker) hiện đại.
*   **Chọn hạng phòng**: Hiển thị danh sách các loại phòng hiện có.
*   **Tính tiền tự động**: Tự động tính tổng thanh toán dựa trên: `Giá phòng x Số đêm x Số lượng`.
*   **Kiểm tra phòng trống**: Tự động đối soát dữ liệu trên Firebase để đảm bảo không bị đặt trùng hoặc quá số lượng cho phép.

### ✈️ Quản lý hành trình
*   **Lịch sử chuyến đi**: Phân loại rõ ràng: *Sắp tới*, *Đã đi*, và *Đã hủy*.
*   **Hủy đặt chỗ**: Cho phép khách hàng hủy đơn khi đơn vẫn đang ở trạng thái chờ duyệt.

---

## 🏢 2. Phân hệ Chủ khách sạn (Hotel Owner)

### 🏗️ Quản lý cơ sở kinh doanh
*   **Đăng ký Khách sạn**: 
    *   Form nhập liệu hỗ trợ tiếng Việt mượt mà.
    *   Bộ chọn 34 tỉnh thành Việt Nam (Dropdown).
    *   Upload nhiều ảnh cùng lúc qua Cloudinary.
*   **Quản lý hạng phòng**: 
    *   Thêm/Sửa/Xóa các loại phòng (Deluxe, Standard...).
    *   **Ràng buộc nghiệp vụ**: Khách sạn phải có ít nhất 1 loại phòng mới được hoạt động.
    *   Chọn tiện ích phòng từ danh sách (Filter Chips).
*   **Luồng Phê duyệt**: Khách sạn mới tạo hoặc sửa thông tin quan trọng sẽ ở trạng thái **PENDING** để Admin duyệt.

### 📋 Quản lý Đơn hàng (Bookings)
*   **Lọc đa tầng**: Xem đơn hàng của tất cả khách sạn hoặc lọc theo từng khách sạn/phòng cụ thể.
*   **Duyệt đơn**: Hệ thống xử lý Duyệt (Confirm) hoặc Từ chối (Reject) khách đặt phòng.

### 📅 Quản lý Lịch phòng (Inventory)
*   **Lịch trạng thái**: Xem tờ lịch tháng trực quan.
*   **Chỉ báo màu sắc**: 
    *   **Xanh**: Còn phòng (Hiển thị số lượng còn trống).
    *   **Đỏ (FULL)**: Đã hết phòng.
    *   Giúp Owner chủ động trong việc duyệt đơn của khách.

---

## 🛠️ 3. Công nghệ & Kiến trúc (Tech Stack)

*   **UI Framework**: Jetpack Compose (100% Declarative UI).
*   **Architecture**: Simplified Clean Architecture (Data Layer gộp Logic, ViewModel xử lý State).
*   **Dependency Injection**: Hilt (Quản lý tập trung Repositories và Firebase Instances).
*   **Backend**: 
    *   **Firebase Auth**: Quản lý người dùng.
    *   **Cloud Firestore**: Cơ sở dữ liệu thời gian thực (Real-time).
    *   **Cloudinary**: Lưu trữ và tối ưu hóa hình ảnh.
*   **Tối ưu hiệu năng**:
    *   Nén ảnh Bitmap trước khi upload để tránh Time-out.
    *   Sử dụng Flow & SnapshotListener để cập nhật UI tức thì.
    *   Firestore Indexing cho các truy vấn phức tạp.

---
*Cập nhật lần cuối: 2024-12-14*
