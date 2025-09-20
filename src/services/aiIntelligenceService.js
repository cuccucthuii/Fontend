import { fetchMovies } from './movieService'
import { fetchBranches } from './branchService'

/**
 * AI Intelligence Service - Xử lý các câu hỏi phức tạp và thông minh
 */

// Patterns để phát hiện câu hỏi phức tạp
const COMPLEX_QUESTION_PATTERNS = {
  // Gợi ý phim
  movieRecommendation: [
    'phim nào hay nhất', 'phim nào tốt nhất', 'phim nào đáng xem',
    'gợi ý phim', 'phim nào nên xem', 'phim hay', 'phim hot',
    'phim mới nhất', 'phim tuần này', 'phim tháng này'
  ],
  
  // Phim phù hợp
  movieSuitable: [
    'phim nào cho gia đình', 'phim cho trẻ em', 'phim cho trẻ con',
    'phim phù hợp', 'phim nào phù hợp', 'phim cho người lớn',
    'phim lãng mạn', 'phim hành động', 'phim hài', 'phim kinh dị'
  ],
  
  // So sánh phim
  movieComparison: [
    'so sánh', 'khác nhau', 'giống nhau', 'phim nào tốt hơn',
    'phim a vs phim b', 'phim nào hay hơn'
  ],
  
  // Câu hỏi thời gian
  timeBased: [
    'phim nào tuần này', 'phim nào hôm nay', 'phim nào ngày mai',
    'phim nào cuối tuần', 'phim nào mới nhất', 'phim nào sắp chiếu'
  ],
  
  // Câu hỏi địa điểm
  locationBased: [
    'rạp nào gần', 'rạp nào tốt nhất', 'rạp nào có phim',
    'rạp nào rẻ nhất', 'rạp nào đẹp nhất'
  ]
}

/**
 * Phát hiện câu hỏi phức tạp
 */
export function isComplexQuestion(userMessage) {
  const lower = userMessage.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '')
  
  for (const [category, patterns] of Object.entries(COMPLEX_QUESTION_PATTERNS)) {
    for (const pattern of patterns) {
      if (lower.includes(pattern)) {
        return { isComplex: true, category, pattern }
      }
    }
  }
  
  return { isComplex: false }
}

/**
 * Xử lý câu hỏi phức tạp với dữ liệu thật
 */
export async function handleComplexQuestion(userMessage, category) {
  try {
    switch (category) {
      case 'movieRecommendation':
        return await handleMovieRecommendation(userMessage)
      
      case 'movieSuitable':
        return await handleMovieSuitable(userMessage)
      
      case 'movieComparison':
        return await handleMovieComparison(userMessage)
      
      case 'timeBased':
        return await handleTimeBasedQuestion(userMessage)
      
      case 'locationBased':
        return await handleLocationBasedQuestion(userMessage)
      
      default:
        return null
    }
  } catch (error) {
    console.error('Error handling complex question:', error)
    return null
  }
}

/**
 * Xử lý gợi ý phim
 */
async function handleMovieRecommendation(userMessage) {
  const movies = await getMoviesData()
  if (!movies.length) return null
  
  const lower = userMessage.toLowerCase()
  
  // Phim hay nhất (dựa trên doPhoBien hoặc rating)
  if (lower.includes('hay nhất') || lower.includes('tốt nhất')) {
    const topMovies = movies
      .filter(m => m.doPhoBien === 'PHO_BIEN' || m.doPhoBien === 'RAT_PHO_BIEN')
      .slice(0, 3)
    
    if (topMovies.length) {
      return formatMovieRecommendationResponse(topMovies, '🎬 Top phim hay nhất hiện tại:')
    }
  }
  
  // Phim mới nhất
  if (lower.includes('mới nhất') || lower.includes('mới')) {
    const newMovies = movies
      .filter(m => isNewMovie(m.ngayPhatHanh))
      .sort((a, b) => new Date(b.ngayPhatHanh) - new Date(a.ngayPhatHanh))
      .slice(0, 3)
    
    if (newMovies.length) {
      return formatMovieRecommendationResponse(newMovies, '🆕 Phim mới nhất:')
    }
  }
  
  // Phim tuần này
  if (lower.includes('tuần này') || lower.includes('tuần')) {
    const weekMovies = movies
      .filter(m => isThisWeek(m.ngayPhatHanh))
      .slice(0, 3)
    
    if (weekMovies.length) {
      return formatMovieRecommendationResponse(weekMovies, '📅 Phim tuần này:')
    }
  }
  
  // Gợi ý chung
  const randomMovies = getRandomMovies(movies, 3)
  return formatMovieRecommendationResponse(randomMovies, '🎭 Gợi ý phim cho bạn:')
}

/**
 * Xử lý phim phù hợp
 */
async function handleMovieSuitable(userMessage) {
  const movies = await getMoviesData()
  if (!movies.length) return null
  
  const lower = userMessage.toLowerCase()
  
  // Phim cho gia đình
  if (lower.includes('gia đình') || lower.includes('trẻ em') || lower.includes('trẻ con')) {
    const familyMovies = movies.filter(m => 
      m.tuoiGioiHan === 'P' || m.tuoiGioiHan === 'T13' ||
      (m.theLoai && m.theLoai.some(genre => 
        ['Gia đình', 'Hoạt hình', 'Hài hước'].includes(genre)
      ))
    ).slice(0, 3)
    
    if (familyMovies.length) {
      return formatMovieRecommendationResponse(familyMovies, '👨‍👩‍👧‍👦 Phim phù hợp cho gia đình:')
    }
  }
  
  // Phim lãng mạn
  if (lower.includes('lãng mạn') || lower.includes('tình cảm')) {
    const romanceMovies = movies.filter(m => 
      m.theLoai && m.theLoai.some(genre => 
        ['Lãng mạn', 'Tình cảm', 'Tâm lý'].includes(genre)
      )
    ).slice(0, 3)
    
    if (romanceMovies.length) {
      return formatMovieRecommendationResponse(romanceMovies, '💕 Phim lãng mạn:')
    }
  }
  
  // Phim hành động
  if (lower.includes('hành động') || lower.includes('action')) {
    const actionMovies = movies.filter(m => 
      m.theLoai && m.theLoai.some(genre => 
        ['Hành động', 'Phiêu lưu', 'Khoa học viễn tưởng'].includes(genre)
      )
    ).slice(0, 3)
    
    if (actionMovies.length) {
      return formatMovieRecommendationResponse(actionMovies, '💥 Phim hành động:')
    }
  }
  
  return null
}

/**
 * Xử lý so sánh phim
 */
async function handleMovieComparison(userMessage) {
  const movies = await getMoviesData()
  if (!movies.length) return null
  
  // Tìm tên phim trong câu hỏi
  const movieNames = extractMovieNames(userMessage, movies)
  
  if (movieNames.length >= 2) {
    const movie1 = movies.find(m => m.tenPhim === movieNames[0])
    const movie2 = movies.find(m => m.tenPhim === movieNames[1])
    
    if (movie1 && movie2) {
      return formatMovieComparisonResponse(movie1, movie2)
    } else {
      // Nếu không tìm thấy đủ 2 phim, trả về thông báo hữu ích
      const foundMovies = [movie1, movie2].filter(Boolean)
      const notFoundMovies = movieNames.filter(name => 
        !movies.some(m => m.tenPhim === name)
      )
      
      if (foundMovies.length === 1) {
        return `🔍 <strong>Thông tin so sánh:</strong>\n\n` +
               `✅ Tìm thấy: <strong>${foundMovies[0].tenPhim}</strong>\n` +
               `❌ Không tìm thấy: ${notFoundMovies.join(', ')}\n\n` +
               `📽️ <strong>Thông tin phim ${foundMovies[0].tenPhim}:</strong>\n` +
               `${formatSingleMovieInfo(foundMovies[0])}\n\n` +
               `💡 Bạn có thể xem danh sách phim đang chiếu tại <a href="/movies" class="link">Trang phim</a> để tìm phim khác!`
      } else if (foundMovies.length === 0) {
        return `❌ <strong>Không tìm thấy phim:</strong> ${notFoundMovies.join(', ')}\n\n` +
               `💡 Bạn có thể xem danh sách phim đang chiếu tại <a href="/movies" class="link">Trang phim</a>!\n\n` +
               `🎬 <strong>Một số phim đang chiếu:</strong>\n` +
               `${movies.slice(0, 3).map(m => `• ${m.tenPhim}`).join('\n')}`
      }
    }
  } else if (movieNames.length === 1) {
    const movie = movies.find(m => m.tenPhim === movieNames[0])
    if (movie) {
      return `🔍 <strong>Thông tin phim ${movie.tenPhim}:</strong>\n\n` +
             `${formatSingleMovieInfo(movie)}\n\n` +
             `💡 Bạn muốn so sánh với phim nào? Hãy xem danh sách phim tại <a href="/movies" class="link">Trang phim</a>!`
    }
  }
  
  return null
}

/**
 * Xử lý câu hỏi thời gian
 */
async function handleTimeBasedQuestion(userMessage) {
  const movies = await getMoviesData()
  if (!movies.length) return null
  
  const lower = userMessage.toLowerCase()
  
  if (lower.includes('hôm nay') || lower.includes('ngày mai')) {
    const todayMovies = movies.filter(m => isToday(m.ngayPhatHanh))
    if (todayMovies.length) {
      return formatMovieRecommendationResponse(todayMovies, '📅 Phim hôm nay:')
    }
  }
  
  if (lower.includes('sắp chiếu')) {
    const comingSoon = movies.filter(m => isComingSoon(m.ngayPhatHanh))
    if (comingSoon.length) {
      return formatMovieRecommendationResponse(comingSoon, '🔮 Phim sắp chiếu:')
    }
  }
  
  return null
}

/**
 * Xử lý câu hỏi địa điểm
 */
async function handleLocationBasedQuestion(userMessage) {
  const branches = await getBranchesData()
  if (!branches.length) return null
  
  const lower = userMessage.toLowerCase()
  
  if (lower.includes('gần') || lower.includes('nearby') || lower.includes('gần tôi') || lower.includes('gần nhất')) {
    // Thử lấy vị trí người dùng
    try {
      const userLocation = await getUserLocation()
      if (userLocation) {
        return await findNearestCinemas(userLocation, branches)
      }
    } catch (error) {
      console.error('Error getting user location:', error)
    }
    
    // Fallback: hiển thị danh sách rạp và hướng dẫn
    return `🏢 <strong>Danh sách rạp chiếu:</strong>\n\n${branches.slice(0, 5).map(b => 
      `• ${b.tenRapChieu || b.tenRap || b.ten || b.name}\n  📍 ${b.diaChi || b.address || 'Địa chỉ chưa cập nhật'}`
    ).join('\n\n')}\n\n📍 <strong>Để tìm rạp gần nhất:</strong>\n1. Cho phép truy cập vị trí của bạn\n2. Hoặc bấm nút "Gần bạn" trên <a href="/" class="link">Trang chủ</a>\n3. Hoặc cho mình biết bạn ở khu vực nào nhé!`
  }
  
  return null
}

// Helper functions
async function getMoviesData() {
  try {
    const res = await fetchMovies()
    let movies = Array.isArray(res?.data) ? res.data : (res?.data?.content || [])
    
    // Thêm một số phim mẫu nếu database trống
    if (movies.length === 0) {
      movies = [
        {
          idPhim: 1,
          tenPhim: "Nhà Bà Nữ",
          moTa: "Một bộ phim tâm lý hài hước về cuộc sống gia đình Việt Nam",
          thoiLuong: 120,
          ngayPhatHanh: "2025-09-05",
          trangThai: "DANG_CHIEU",
          dinhDang: "2D",
          tuoiGioiHan: "T13",
          namSanXuat: 2025,
          doPhoBien: "PHO_BIEN",
          giaVeCoBan: 50000.00,
          theLoai: ["Tâm lý", "Gia đình", "Hài hước"],
          daoDien: ["Trần Ngọc Giàu"],
          dienVien: ["Trấn Thành"]
        },
        {
          idPhim: 2,
          tenPhim: "Frozen 2",
          moTa: "Cuộc phiêu lưu mới của Elsa và Anna trong vùng đất bí ẩn",
          thoiLuong: 103,
          ngayPhatHanh: "2025-08-20",
          trangThai: "DANG_CHIEU",
          dinhDang: "3D",
          tuoiGioiHan: "P",
          namSanXuat: 2025,
          doPhoBien: "RAT_PHO_BIEN",
          giaVeCoBan: 80000.00,
          theLoai: ["Hoạt hình", "Gia đình", "Phiêu lưu"],
          daoDien: ["Chris Buck", "Jennifer Lee"],
          dienVien: ["Idina Menzel", "Kristen Bell"]
        },
        {
          idPhim: 3,
          tenPhim: "Avengers: Endgame",
          moTa: "Trận chiến cuối cùng của các siêu anh hùng để cứu vũ trụ",
          thoiLuong: 181,
          ngayPhatHanh: "2025-07-15",
          trangThai: "DANG_CHIEU",
          dinhDang: "3D",
          tuoiGioiHan: "T16",
          namSanXuat: 2025,
          doPhoBien: "PHO_BIEN",
          giaVeCoBan: 80000.00,
          theLoai: ["Hành động", "Khoa học viễn tưởng", "Phiêu lưu"],
          daoDien: ["Anthony Russo", "Joe Russo"],
          dienVien: ["Robert Downey Jr.", "Chris Evans", "Mark Ruffalo"]
        }
      ]
    }
    
    return movies
  } catch (error) {
    console.error('Error fetching movies:', error)
    return []
  }
}

async function getBranchesData() {
  try {
    const res = await fetchBranches()
    let branches = Array.isArray(res?.data) ? res.data : (res?.data?.content || [])
    
    // Thêm một số rạp mẫu với tọa độ nếu database trống
    if (branches.length === 0) {
      branches = [
        {
          id: 1,
          tenRapChieu: "DevCinema 1",
          diaChi: "123 Nguyễn Huệ, Quận 1, TP.HCM",
          latitude: 10.7769,
          longitude: 106.7009
        },
        {
          id: 2,
          tenRapChieu: "DevCinema 2", 
          diaChi: "456 Lê Lợi, Quận 3, TP.HCM",
          latitude: 10.7829,
          longitude: 106.7009
        },
        {
          id: 3,
          tenRapChieu: "DevCinema 3",
          diaChi: "789 Võ Văn Tần, Quận 1, TP.HCM", 
          latitude: 10.7769,
          longitude: 106.6909
        },
        {
          id: 4,
          tenRapChieu: "DevCinema 4",
          diaChi: "321 Điện Biên Phủ, Quận Bình Thạnh, TP.HCM",
          latitude: 10.8019,
          longitude: 106.7109
        },
        {
          id: 5,
          tenRapChieu: "DevCinema 5",
          diaChi: "654 Cách Mạng Tháng 8, Quận 10, TP.HCM",
          latitude: 10.7729,
          longitude: 106.6609
        }
      ]
    }
    
    return branches
  } catch (error) {
    console.error('Error fetching branches:', error)
    return []
  }
}

function formatMovieRecommendationResponse(movies, title) {
  const movieList = movies.map(movie => {
    const genres = movie.theLoai ? movie.theLoai.join(', ') : 'Chưa phân loại'
    const releaseDate = formatDate(movie.ngayPhatHanh)
    const ageRating = movie.tuoiGioiHan || 'T13'
    
    return `🎬 <strong>${movie.tenPhim}</strong>\n` +
           `📅 ${releaseDate} • ⏱️ ${movie.thoiLuong} phút • 🎭 ${ageRating}\n` +
           `🎭 Thể loại: ${genres}\n` +
           `📝 ${movie.moTa || 'Mô tả chưa có sẵn'}`
  }).join('\n\n')
  
  return `${title}\n\n${movieList}\n\n💡 Bạn có thể xem chi tiết và đặt vé tại <a href="/movies" class="link">Trang phim</a>!`
}

function formatMovieComparisonResponse(movie1, movie2) {
  const formatMovie = (movie) => {
    const genres = movie.theLoai ? movie.theLoai.join(', ') : 'Chưa phân loại'
    const releaseDate = formatDate(movie.ngayPhatHanh)
    
    return `🎬 <strong>${movie.tenPhim}</strong>\n` +
           `📅 ${releaseDate} • ⏱️ ${movie.thoiLuong} phút • 🎭 ${movie.tuoiGioiHan || 'T13'}\n` +
           `🎭 Thể loại: ${genres}\n` +
           `📝 ${movie.moTa || 'Mô tả chưa có sẵn'}`
  }
  
  const similarities = findSimilarities(movie1, movie2)
  const differences = findDifferences(movie1, movie2)
  
  let response = `🔍 <strong>So sánh phim:</strong>\n\n`
  response += `📽️ <strong>Phim 1:</strong>\n${formatMovie(movie1)}\n\n`
  response += `📽️ <strong>Phim 2:</strong>\n${formatMovie(movie2)}\n\n`
  
  if (similarities.length) {
    response += `✅ <strong>Điểm tương đồng:</strong>\n${similarities.join('\n')}\n\n`
  }
  
  if (differences.length) {
    response += `❌ <strong>Điểm khác biệt:</strong>\n${differences.join('\n')}\n\n`
  }
  
  response += `💡 <strong>Gợi ý:</strong> Cả hai phim đều có những điểm thú vị riêng. Bạn có thể xem trailer và đọc review để quyết định nhé!`
  
  return response
}

function findSimilarities(movie1, movie2) {
  const similarities = []
  
  if (movie1.dinhDang === movie2.dinhDang) {
    similarities.push(`• Cùng định dạng: ${movie1.dinhDang}`)
  }
  
  if (movie1.tuoiGioiHan === movie2.tuoiGioiHan) {
    similarities.push(`• Cùng độ tuổi: ${movie1.tuoiGioiHan}`)
  }
  
  if (movie1.namSanXuat === movie2.namSanXuat) {
    similarities.push(`• Cùng năm sản xuất: ${movie1.namSanXuat}`)
  }
  
  const commonGenres = movie1.theLoai?.filter(g => movie2.theLoai?.includes(g)) || []
  if (commonGenres.length) {
    similarities.push(`• Thể loại chung: ${commonGenres.join(', ')}`)
  }
  
  return similarities
}

function findDifferences(movie1, movie2) {
  const differences = []
  
  if (movie1.dinhDang !== movie2.dinhDang) {
    differences.push(`• Định dạng: ${movie1.dinhDang} vs ${movie2.dinhDang}`)
  }
  
  if (movie1.tuoiGioiHan !== movie2.tuoiGioiHan) {
    differences.push(`• Độ tuổi: ${movie1.tuoiGioiHan} vs ${movie2.tuoiGioiHan}`)
  }
  
  if (movie1.thoiLuong !== movie2.thoiLuong) {
    differences.push(`• Thời lượng: ${movie1.thoiLuong} phút vs ${movie2.thoiLuong} phút`)
  }
  
  return differences
}

function extractMovieNames(userMessage, movies) {
  const movieNames = []
  const lower = userMessage.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '')
  
  // Tìm phim theo tên chính xác
  for (const movie of movies) {
    const movieNameLower = movie.tenPhim.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '')
    if (lower.includes(movieNameLower)) {
      movieNames.push(movie.tenPhim)
    }
  }
  
  // Nếu không tìm thấy, thử tìm theo từ khóa phổ biến
  if (movieNames.length === 0) {
    const keywords = {
      'nha ba nu': 'Nhà Bà Nữ',
      'frozen': 'Frozen 2',
      'avengers': 'Avengers: Endgame',
      'joker': 'Joker',
      'spider': 'Spider-Man',
      'batman': 'Batman',
      'superman': 'Superman'
    }
    
    for (const [keyword, movieName] of Object.entries(keywords)) {
      if (lower.includes(keyword)) {
        // Kiểm tra xem phim có trong database không
        const foundMovie = movies.find(m => m.tenPhim === movieName)
        if (foundMovie) {
          movieNames.push(movieName)
        }
      }
    }
  }
  
  return movieNames
}

function isNewMovie(releaseDate) {
  if (!releaseDate) return false
  const release = new Date(releaseDate)
  const now = new Date()
  const diffTime = Math.abs(now - release)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays <= 30 // Phim mới trong 30 ngày
}

function isThisWeek(releaseDate) {
  if (!releaseDate) return false
  const release = new Date(releaseDate)
  const now = new Date()
  const startOfWeek = new Date(now.setDate(now.getDate() - now.getDay()))
  const endOfWeek = new Date(now.setDate(now.getDate() - now.getDay() + 6))
  return release >= startOfWeek && release <= endOfWeek
}

function isToday(releaseDate) {
  if (!releaseDate) return false
  const release = new Date(releaseDate)
  const today = new Date()
  return release.toDateString() === today.toDateString()
}

function isComingSoon(releaseDate) {
  if (!releaseDate) return false
  const release = new Date(releaseDate)
  const today = new Date()
  return release > today
}

function getRandomMovies(movies, count) {
  const shuffled = [...movies].sort(() => 0.5 - Math.random())
  return shuffled.slice(0, count)
}

function formatSingleMovieInfo(movie) {
  const genres = movie.theLoai ? movie.theLoai.join(', ') : 'Chưa phân loại'
  const releaseDate = formatDate(movie.ngayPhatHanh)
  const ageRating = movie.tuoiGioiHan || 'T13'
  
  return `📅 ${releaseDate} • ⏱️ ${movie.thoiLuong} phút • 🎭 ${ageRating}\n` +
         `🎭 Thể loại: ${genres}\n` +
         `📝 ${movie.moTa || 'Mô tả chưa có sẵn'}`
}

function formatDate(dateStr) {
  if (!dateStr) return 'Chưa xác định'
  try {
    const date = new Date(dateStr)
    return date.toLocaleDateString('vi-VN')
  } catch {
    return dateStr
  }
}

/**
 * Lấy vị trí người dùng
 */
function getUserLocation() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error('Geolocation is not supported'))
      return
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        resolve({
          lat: position.coords.latitude,
          lng: position.coords.longitude
        })
      },
      (error) => {
        reject(error)
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 300000 // 5 minutes
      }
    )
  })
}

/**
 * Tìm rạp gần nhất
 */
async function findNearestCinemas(userLocation, branches) {
  try {
    // Thử dùng API backend trước
    const { getNearbyBranches } = await import('./branchService')
    const nearbyRes = await getNearbyBranches({
      lat: userLocation.lat,
      lng: userLocation.lng,
      radiusKm: 25,
      limit: 5
    })
    
    if (nearbyRes?.data?.length > 0) {
      return formatNearbyCinemasResponse(nearbyRes.data, userLocation)
    }
  } catch (error) {
    console.error('Backend nearby API failed:', error)
  }
  
  // Fallback: tính toán client-side
  const branchesWithDistance = branches
    .filter(b => b.latitude && b.longitude)
    .map(branch => ({
      ...branch,
      distance: calculateDistance(
        userLocation.lat, userLocation.lng,
        parseFloat(branch.latitude), parseFloat(branch.longitude)
      )
    }))
    .sort((a, b) => a.distance - b.distance)
    .slice(0, 5)
  
  if (branchesWithDistance.length > 0) {
    return formatNearbyCinemasResponse(branchesWithDistance, userLocation)
  }
  
  return `📍 <strong>Không tìm thấy rạp gần bạn</strong>\n\n` +
         `💡 <strong>Gợi ý:</strong>\n` +
         `• Kiểm tra lại vị trí của bạn\n` +
         `• Thử tìm rạp theo khu vực\n` +
         `• Xem danh sách tất cả rạp tại <a href="/" class="link">Trang chủ</a>`
}

/**
 * Tính khoảng cách giữa 2 điểm (Haversine formula)
 */
function calculateDistance(lat1, lng1, lat2, lng2) {
  const R = 6371 // Bán kính Trái Đất (km)
  const dLat = (lat2 - lat1) * Math.PI / 180
  const dLng = (lng2 - lng1) * Math.PI / 180
  const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
    Math.sin(dLng/2) * Math.sin(dLng/2)
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
  return R * c
}

/**
 * Format response cho rạp gần nhất
 */
function formatNearbyCinemasResponse(nearbyBranches, userLocation) {
  const cinemaList = nearbyBranches.map((branch, index) => {
    const distance = branch.distanceKm || branch.distance || 0
    const name = branch.tenRapChieu || branch.tenRap || branch.ten || branch.name
    const address = branch.diaChi || branch.address || 'Địa chỉ chưa cập nhật'
    
    return `${index + 1}. 🏢 <strong>${name}</strong>\n` +
           `   📍 ${address}\n` +
           `   📏 Cách bạn ${distance.toFixed(1)} km`
  }).join('\n\n')
  
  return `📍 <strong>Rạp gần bạn nhất:</strong>\n\n${cinemaList}\n\n` +
         `🎬 <strong>Gợi ý:</strong>\n` +
         `• Rạp gần nhất: ${nearbyBranches[0].tenRapChieu || nearbyBranches[0].tenRap || nearbyBranches[0].ten || nearbyBranches[0].name}\n` +
         `• Cách bạn ${(nearbyBranches[0].distanceKm || nearbyBranches[0].distance || 0).toFixed(1)} km\n\n` +
         `💡 Xem lịch chiếu và đặt vé tại <a href="/showtimes" class="link">Lịch chiếu</a>!`
}

