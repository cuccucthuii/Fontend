import { createRouter, createWebHistory } from "vue-router";
import MainLayout from "../layout/MainLayout.vue";
import HomePage from "../views/customer/HomePage.vue";
import BookingPage from "../views/customer/BookingPage.vue";
import LandingPage from "../pages/LandingPage.vue";
import DashboardPage from "../pages/DashboardPage.vue";
import AccountPage from "../pages/AccountPage.vue";
import MoviePage from "../pages/MoviePage.vue";
import AddMoviePage from "../pages/AddMoviePage.vue";
import EditMoviePage from "../pages/EditMoviePage.vue";
import MovieDetailPage from "../pages/MovieDetailPage.vue";
import MovieList from "../pages/MovieList.vue";
import MovieDetailAdmin from "../pages/admin/MovieDetailAdmin.vue";
import GenreManagementAdmin from "../pages/admin/GenreManagementAdmin.vue";
import RoomPage from "../pages/RoomPage.vue";
import BranchPage from "../pages/BranchPage.vue";
import SchedulePage from "../pages/SchedulePage.vue";
import BillPage from "../pages/BillPage.vue";
import SeatPage from "../pages/SeatPage.vue";
import InvoicePage from "../pages/InvoicePage.vue";
import InvoiceDetailPage from "../pages/InvoiceDetailPage.vue";
import PaymentSuccessPage from "../pages/PaymentSuccessPage.vue";
import NewsPage from "../pages/USER/NewsPage.vue";
import TicketPricePage from "../pages/USER/TicketPricePage.vue";
import ShowtimesPage from "../pages/USER/ShowtimesPage.vue";
import MovieTrashPage from "../pages/MovieTrashPage.vue";
import PromotionsPage from "../pages/USER/PromotionsPage.vue";
import TermsPage from "../pages/TermsPage.vue";
import PrivacyPage from "../pages/PrivacyPage.vue";
import ContactPage from "../pages/ContactPage.vue";
import FeedbackPage from "../pages/FeedbackPage.vue";
import AdminAccount from "../pages/AdminAccount.vue";
import AdminLayout from "../layout/AdminLayout.vue";
import AdminAddEmployee from "../pages/admin/AdminAddEmployee.vue";
import WelcomeAdmin from "../pages/admin/WelcomeAdmin.vue";
import WelcomeEmployee from "../pages/admin/WelcomeEmployee.vue";

// POS Layout & Pages
import POSLayout from "../layout/POSLayout.vue";
import POSHomePage from "../pages/POS/POSHomePage.vue";
import ShowtimeSelectPage from "../pages/POS/ShowtimeSelectPage.vue";
import SeatSelectPage from "../pages/POS/SeatSelectPage.vue";
import PaymentPage from "../pages/POS/PaymentPage.vue";

const routes = [
  {
    path: "/",
    name: "Landing",
    component: LandingPage,
  },
  {
    path: "/home",
    name: "Home",
    component: HomePage,
    meta: { requiresAuth: true },
  },
  {
    path: "/booking",
    name: "Booking",
    component: BookingPage,
  },
  {
    path: "/news",
    name: "News",
    component: NewsPage,
  },
  {
    path: "/prices",
    name: "TicketPrice",
    component: TicketPricePage,
  },
  {
    path: "/showtimes",
    name: "Showtimes",
    component: ShowtimesPage,
  },
  {
    path: "/movie/:id",
    name: "MovieDetail",
    component: MovieDetailPage,
    props: true,
  },
  {
    path: "/promotions",
    name: "Promotions",
    component: PromotionsPage,
  },
  {
    path: "/terms",
    name: "Terms",
    component: TermsPage,
  },
  {
    path: "/privacy",
    name: "Privacy",
    component: PrivacyPage,
  },
  {
    path: "/contact",
    name: "Contact",
    component: ContactPage,
  },
  {
    path: "/invoice-detail",
    name: "InvoiceDetail",
    component: InvoiceDetailPage,
  },
  {
    path: "/payment-success",
    name: "PaymentSuccess",
    component: PaymentSuccessPage,
  },
  {
    path: "/feedback",
    name: "Feedback",
    component: FeedbackPage,
  },
  {
    path: "/about",
    name: "About",
    component: () => import("../pages/AboutPage.vue"),
  },
  {
    path: "/admin",
    component: AdminLayout,
    children: [
      { path: "employee", name: "WelcomeEmployee", component: WelcomeEmployee },
      { path: "", name: "AdminWelcome", component: WelcomeAdmin },
      { path: "dashboard", name: "Dashboard", component: DashboardPage },
      { path: "account", name: "Account", component: AdminAccount },
      { path: "movies", name: "Movies", component: MoviePage },
      { path: "movies/add", name: "AddMovie", component: AddMoviePage },
      {
        path: "movies/edit/:id",
        name: "EditMovie",
        component: EditMoviePage,
        props: true,
      },
      {
        path: "movies/:id",
        name: "MovieDetail",
        component: MovieDetailPage,
        props: true,
      },
      { path: "movies/trash", name: "MovieTrash", component: MovieTrashPage },
      {
        path: "genres",
        name: "AdminMovieGenres",
        component: GenreManagementAdmin,
      },
      { path: "rooms", name: "Rooms", component: RoomPage },
      { path: "branches", name: "Branches", component: BranchPage },
      { path: "schedule", name: "Schedule", component: SchedulePage },
      { path: "bills", name: "Bills", component: BillPage },
      { path: "seats", name: "Seats", component: SeatPage },
      { path: "invoices", name: "Invoices", component: InvoicePage },
      {
        path: "add-employee",
        name: "AddEmployee",
        component: AdminAddEmployee,
      },
    ],
  },
  // POS section
  {
    path: "/pos",
    component: POSLayout,
    children: [
      { path: "", name: "POSHome", component: POSHomePage },
      {
        path: "showtimes",
        name: "POSShowtimes",
        component: ShowtimeSelectPage,
      },
      {
        path: "seats/:scheduleId",
        name: "POSSeats",
        component: SeatSelectPage,
        props: true,
      },
      { path: "payment", name: "POSPayment", component: PaymentPage },
    ],
  },
  {
    path: "/demo/showtime-modal",
    name: "ShowtimeModalDemo",
    component: () => import("../pages/ShowtimeModalDemo.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// ✅ Navigation Guard of Login
// router.beforeEach((to, from, next) => {
//   const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
//   const userInfo = localStorage.getItem('userInfo')
//   let userRole = 'user'

//   if (userInfo) {
//     try {
//       const parsed = JSON.parse(userInfo)
//       userRole = parsed.role || 'user'
//       console.log('User role from localStorage:', userRole)
//     } catch (e) {
//       console.error('Error parsing userInfo:', e)
//     }
//   }

//   if (to.meta.requiresAuth && !isLoggedIn) {
//     next('/login')
//   } else if (to.path === '/login' && isLoggedIn) {
//     // Chuyển hướng dựa vào role khi đã đăng nhập
//     const normalizedRole = userRole?.toLowerCase?.() || 'user'
//     console.log('Navigation guard - normalized role:', normalizedRole)

//     if (
//       normalizedRole === 'admin' ||
//       normalizedRole === 'administrator' ||
//       normalizedRole === 'quản trị viên' ||
//       normalizedRole === 'quản lý' ||
//       normalizedRole === 'quan ly' ||
//       normalizedRole === 'employees' ||
//       normalizedRole === 'staff' ||
//       normalizedRole === 'nhân viên bán vé'
//     ) {
//       next('/admin')
//     } else {
//       next('/home')
//     }
//   } else {
//     next()
//   }

router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem("isLoggedIn") === "true";
  const userInfo = localStorage.getItem("userInfo");
  let userRole = "user";

  if (userInfo) {
    try {
      const parsed = JSON.parse(userInfo);
      userRole = parsed.role || "user";
    } catch (e) {}
  }

  function removeVietnameseTones(str) {
    return str
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "")
      .replace(/đ/g, "d")
      .replace(/Đ/g, "D");
  }

  // Nếu chưa đăng nhập mà vào route cần đăng nhập thì chuyển về login
  if (to.meta.requiresAuth && !isLoggedIn) {
    next("/login");
    return;
  }

  // Nếu đã đăng nhập và đang ở trang login thì chuyển hướng theo role
  if (to.path === "/login" && isLoggedIn) {
    const normalizedRole = removeVietnameseTones(
      userRole?.toLowerCase?.() || "user"
    );
    // Nếu là admin hoặc nhân viên thì vào /admin
    if (
      normalizedRole.includes("admin") ||
      normalizedRole.includes("quan tri") ||
      normalizedRole.includes("quan ly") ||
      normalizedRole.includes("employee") ||
      normalizedRole.includes("staff") ||
      normalizedRole.includes("nhan vien")
    ) {
      next("/admin");
      return;
    } else {
      next("/home");
      return;
    }
  }

  // Nếu đã đăng nhập, vào /admin, kiểm tra role, nếu không phải admin/nhân viên thì đá ra /home
  if (to.path.startsWith("/admin") && isLoggedIn) {
    const normalizedRole = removeVietnameseTones(
      userRole?.toLowerCase?.() || "user"
    );
    if (
      normalizedRole.includes("admin") ||
      normalizedRole.includes("quan tri") ||
      normalizedRole.includes("quan ly") ||
      normalizedRole.includes("employee") ||
      normalizedRole.includes("staff") ||
      normalizedRole.includes("nhan vien")
    ) {
      next();
      return;
    } else {
      next("/home");
      return;
    }
  }

  // Nếu đã đăng nhập, vào /home, nhưng là admin/nhân viên thì đá về /admin
  if (to.path === "/home" && isLoggedIn) {
    const normalizedRole = removeVietnameseTones(
      userRole?.toLowerCase?.() || "user"
    );
    if (
      normalizedRole.includes("admin") ||
      normalizedRole.includes("quan tri") ||
      normalizedRole.includes("quan ly") ||
      normalizedRole.includes("employee") ||
      normalizedRole.includes("staff") ||
      normalizedRole.includes("nhan vien")
    ) {
      next("/admin");
      return;
    }
  }

  next();
});

export default router;
