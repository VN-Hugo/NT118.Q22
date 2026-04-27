1. Module Xác thực & Quản lý Tài khoản (Auth & User Profile)

Sử dụng Firebase Authentication & Cloud Firestore.

    Đăng ký / Đăng nhập cơ bản: Bằng Email và Mật khẩu.

    Đăng nhập Mạng xã hội (Bổ sung): Đăng nhập bằng Google / Facebook (Rất quan trọng để tăng tỷ lệ chuyển đổi user).

    Quên mật khẩu: Gửi email reset password.

    Đăng xuất.

    Quản lý hồ sơ (User Profile): Xem và cập nhật thông tin cá nhân (Tên, Avatar, Số điện thoại, Bio). Lưu ý: Avatar cần dùng thêm Firebase Cloud Storage.

    Phân quyền người dùng (Role Management): Xử lý logic chia role (User thường, Host/Partner, Admin) trực tiếp trên Firestore rules hoặc Custom Claims.

    Cài đặt ứng dụng (Bổ sung): Đổi ngôn ngữ, chế độ Dark/Light mode, Cài đặt thông báo.

2. Module Khám phá & Địa điểm (Discovery & Places)

Trọng tâm xử lý query trên Firestore. Cần cẩn thận cấu trúc data để không bị tính phí read/write quá cao.

    Danh sách địa điểm du lịch: Hiển thị theo dạng feed (Phổ biến, Đề xuất, Mới nhất).

    Phân loại địa điểm (Categories): Biển, Núi, Văn hóa, Ẩm thực, Nghỉ dưỡng...

    Tìm kiếm & Lọc (Search & Filter): * Tìm kiếm theo text (Tên địa điểm).

        Bổ sung: Lọc theo khu vực/tỉnh thành, đánh giá sao, mức giá.

    Xem chi tiết địa điểm: Thông tin, mô tả, địa chỉ, giờ mở cửa, giá vé.

    Tích hợp Bản đồ (Bổ sung rất cần thiết): Hiển thị vị trí địa điểm trên Google Maps SDK và tính khoảng cách từ user đến địa điểm.

    Lưu địa điểm yêu thích (Wishlist): Nút thả tim/bookmark để lưu lại.

3. Module Tương tác Cộng đồng (Social & Reviews)

Module này giúp giữ chân người dùng. Xử lý logic sub-collection trên Firestore.

    Đánh giá sao (Rating): Cho phép rate 1-5 sao đối với địa điểm / khách sạn / quán ăn. Cập nhật điểm trung bình (Average Rating) cho địa điểm.

    Viết bình luận (Text Review): Viết trải nghiệm thực tế.

    Upload hình ảnh trải nghiệm: Đính kèm ảnh vào bài review (Sử dụng Firebase Storage để lưu ảnh và lưu URL vào Firestore).

    Xem đánh giá của người khác: Hiển thị danh sách review trong trang chi tiết địa điểm (phân trang/lazy load để tối ưu hiệu năng).

    Báo cáo vi phạm (Bổ sung): Report các bình luận hoặc hình ảnh không phù hợp.

4. Module Lập Lịch Trình (Trip Itinerary Planning)

Đây là "linh hồn" của app. Logic ở đây khá phức tạp vì dính đến ngày giờ (Date/Time) và sắp xếp.

    Quản lý chuyến đi: Tạo chuyến đi mới, Chỉnh sửa thông tin (Tên chuyến, Ngày đi, Ngày về), Xóa chuyến đi.

    Ghi chú lịch trình theo ngày: Tạo các "Day 1, Day 2..." trong chuyến đi.

    Thêm địa điểm vào lịch trình: Kéo/chọn các địa điểm (từ danh sách app hoặc wishlist) bỏ vào từng ngày cụ thể.

    Gán khách sạn & Nơi ở: Tách riêng logic chọn nơi lưu trú cho chuyến đi.

    Chỉnh sửa thứ tự (Bổ sung): Cho phép kéo thả (Drag & Drop) để thay đổi thứ tự các điểm đến trong một ngày.

    Chế độ Offline (Bổ sung): Cache lại dữ liệu lịch trình (Firestore có hỗ trợ Offline Persistence) để user có thể xem khi đang đi rừng/đảo mất mạng.

5. Module Dành cho Host / Đối tác (Host Portal)

Dành cho người tạo nội dung hoặc chủ doanh nghiệp.

    Đăng ký làm Host: Form duyệt thông tin.

    Đăng tải địa điểm du lịch: Form nhập thông tin chi tiết, upload bộ ảnh, set tọa độ GPS, set giá tham khảo.

    Quản lý danh sách địa điểm của Host: Sửa, xóa, ẩn/hiện địa điểm đã đăng.

    Xem thống kê cơ bản (Bổ sung): Xem có bao nhiêu lượt lưu, lượt xem địa điểm của mình.

6. Module Nâng cao & Tích hợp (Advanced Features)

Nên để ở các Phase sau khi core app đã ổn định.

    Thanh toán (Payment Gateway): Tích hợp SDK thanh toán (VNPay, MoMo, ZaloPay hoặc Stripe) nếu app có bán vé hoặc thu phí booking khách sạn/tour.

    AI Gợi ý lịch trình: Tích hợp API (như Gemini API hoặc OpenAI) để user nhập "Tôi muốn đi Đà Lạt 3 ngày 2 đêm, thích thiên nhiên", AI sẽ trả về một lịch trình tự động mapping với database của app.

    Push Notifications (Bổ sung): Dùng Firebase Cloud Messaging (FCM) để nhắc nhở: "Ngày mai chuyến đi của bạn bắt đầu", hoặc báo khi có người reply comment.