import api from '@/services/api';
import { CINEMA_ENDPOINTS } from "../constants/api";

export const getAllCinemas = async () => {
  try {
    console.log("🎬 Fetching cinemas from API:", CINEMA_ENDPOINTS.GET_ALL);
    const response = await api.get(CINEMA_ENDPOINTS.GET_ALL);
    console.log("🎬 API Response:", response.data);
    
    // Xử lý dữ liệu từ API
    const cinemas = Array.isArray(response.data) ? response.data : [];
    console.log("🎬 Processed cinemas:", cinemas.length, "items");
    
    return cinemas;
  } catch (error) {
    console.error("❌ Error fetching cinemas from API, using mock data:", error);
    
    // Fallback to mock data khi API không hoạt động
    const mockCinemas = [
      {
        idRapChieu: 1,
        tenRapChieu: "Dev Cinemas Xuân Thủy",
        diaChi: "Xuân Thủy, Cầu Giấy, Hà Nội",
        soPhong: 8,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 2,
        tenRapChieu: "Dev Cinemas Tây Sơn",
        diaChi: "Tây Sơn, Đống Đa, Hà Nội",
        soPhong: 6,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 3,
        tenRapChieu: "Dev Cinemas Vĩnh Yên",
        diaChi: "Vĩnh Yên, Vĩnh Phúc",
        soPhong: 7,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 4,
        tenRapChieu: "Dev Cinemas Ung Văn Khiêm",
        diaChi: "Ung Văn Khiêm, Bình Thạnh, TP.HCM",
        soPhong: 5,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 5,
        tenRapChieu: "Dev Cinemas Lào Cai",
        diaChi: "Lào Cai, Lào Cai",
        soPhong: 4,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 6,
        tenRapChieu: "Dev Cinemas Trần Quang Khải",
        diaChi: "Trần Quang Khải, Hoàn Kiếm, Hà Nội",
        soPhong: 6,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 7,
        tenRapChieu: "Dev Cinemas TRMall Phú Quốc",
        diaChi: "Phú Quốc, Kiên Giang",
        soPhong: 5,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 8,
        tenRapChieu: "Dev Cinemas Empire Bình Dương",
        diaChi: "Bình Dương, Bình Dương",
        soPhong: 7,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 9,
        tenRapChieu: "Dev Cinemas Quang Trung",
        diaChi: "Quang Trung, Hà Đông, Hà Nội",
        soPhong: 6,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 10,
        tenRapChieu: "Dev Cinemas Giải Phóng",
        diaChi: "Giải Phóng, Hai Bà Trưng, Hà Nội",
        soPhong: 8,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 11,
        tenRapChieu: "Dev Cinemas Thanh Xuân",
        diaChi: "Thanh Xuân, Hà Nội",
        soPhong: 5,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 12,
        tenRapChieu: "Dev Cinemas Mỹ Đình",
        diaChi: "Mỹ Đình, Nam Từ Liêm, Hà Nội",
        soPhong: 7,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 13,
        tenRapChieu: "Dev Cinemas Đan Phượng",
        diaChi: "Đan Phượng, Hà Nội",
        soPhong: 4,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 14,
        tenRapChieu: "Dev Cinemas Thái Nguyên",
        diaChi: "Thái Nguyên, Thái Nguyên",
        soPhong: 6,
        trangThai: "HOAT_DONG"
      },
      {
        idRapChieu: 15,
        tenRapChieu: "Dev Cinemas Thanh Hóa",
        diaChi: "Thanh Hóa, Thanh Hóa",
        soPhong: 5,
        trangThai: "HOAT_DONG"
      }
    ];
    
    return mockCinemas;
  }
};
