-- Thêm cột da_xoa vào bảng phong_chieu để hỗ trợ soft delete
ALTER TABLE phong_chieu 
ADD COLUMN da_xoa BOOLEAN DEFAULT FALSE;

-- Cập nhật tất cả phòng chiếu hiện tại thành chưa xóa
UPDATE phong_chieu 
SET da_xoa = FALSE 
WHERE da_xoa IS NULL;

-- Tạo index để tối ưu query
CREATE INDEX idx_phong_chieu_da_xoa ON phong_chieu(da_xoa);
CREATE INDEX idx_phong_chieu_id_rap_chieu_da_xoa ON phong_chieu(id_rap_chieu, da_xoa);
CREATE INDEX idx_phong_chieu_trang_thai_da_xoa ON phong_chieu(trang_thai_phong_chieu, da_xoa);

-- Kiểm tra kết quả
SELECT COUNT(*) as total_rooms, 
       SUM(CASE WHEN da_xoa = FALSE THEN 1 ELSE 0 END) as active_rooms,
       SUM(CASE WHEN da_xoa = TRUE THEN 1 ELSE 0 END) as deleted_rooms
FROM phong_chieu; 