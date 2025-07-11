import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layout/MainLayout.vue'
import HomePage from '../views/customer/HomePage.vue'
import BookingPage from '../views/customer/BookingPage.vue'
import LandingPage from '../pages/LandingPage.vue'
import DashboardPage from '../pages/DashboardPage.vue'
import AccountPage from '../pages/AccountPage.vue'
import MoviePage from '../pages/MoviePage.vue'
import RoomPage from '../pages/RoomPage.vue'
import BranchPage from '../pages/BranchPage.vue'
import SchedulePage from '../pages/SchedulePage.vue'
import BillPage from '../pages/BillPage.vue'
import SeatPage from '../pages/SeatPage.vue'
import InvoicePage from '../pages/InvoicePage.vue'
import NewsPage from '../pages/USER/NewsPage.vue'
import TicketPricePage from '../pages/USER/TicketPricePage.vue'
import ShowtimesPage from '../pages/USER/ShowtimesPage.vue'
import PromotionsPage from '../pages/USER/PromotionsPage.vue'
import TermsPage from '../pages/TermsPage.vue'
import PrivacyPage from '../pages/PrivacyPage.vue'
import ContactPage from '../pages/ContactPage.vue'
import FeedbackPage from '../pages/FeedbackPage.vue'
import AdminAccount from '../pages/AdminAccount.vue'
import AdminLayout from '../layout/AdminLayout.vue'
import AdminAddEmployee from '../pages/admin/AdminAddEmployee.vue'

// POS Layout & Pages
import POSLayout from '../layout/POSLayout.vue'
import POSHomePage from '../pages/POS/POSHomePage.vue'
import ShowtimeSelectPage from '../pages/POS/ShowtimeSelectPage.vue'
import SeatSelectPage from '../pages/POS/SeatSelectPage.vue'
import PaymentPage from '../pages/POS/PaymentPage.vue'

const routes = [
  {
    path: '/',
    name: 'Landing',
    component: LandingPage
  },
  {
    path: '/home',
    name: 'Home',
    component: HomePage,
    meta: { requiresAuth: true }
  },
  {
    path: '/booking',
    name: 'Booking',
    component: BookingPage
  },
  {
    path: '/news',
    name: 'News',
    component: NewsPage
  },
  {
    path: '/prices',
    name: 'TicketPrice',
    component: TicketPricePage
  },
  {
    path: '/showtimes',
    name: 'Showtimes',
    component: ShowtimesPage
  },
  {
    path: '/promotions',
    name: 'Promotions',
    component: PromotionsPage
  },
  {
    path: '/terms',
    name: 'Terms',
    component: TermsPage
  },
  {
    path: '/privacy',
    name: 'Privacy',
    component: PrivacyPage
  },
  {
    path: '/contact',
    name: 'Contact',
    component: ContactPage
  },
  {
    path: '/feedback',
    name: 'Feedback',
    component: FeedbackPage
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('../pages/AboutPage.vue')
  },
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      { path: '', name: 'Dashboard', component: DashboardPage },
      { path: 'account', name: 'Account', component: AdminAccount },
      { path: 'movies', name: 'Movies', component: MoviePage },
      { path: 'rooms', name: 'Rooms', component: RoomPage },
      { path: 'branches', name: 'Branches', component: BranchPage },
      { path: 'schedule', name: 'Schedule', component: SchedulePage },
      { path: 'bills', name: 'Bills', component: BillPage },
      { path: 'seats', name: 'Seats', component: SeatPage },
      { path: 'invoices', name: 'Invoices', component: InvoicePage },
      { path: 'add-employee', name: 'AddEmployee', component: AdminAddEmployee },
    ]
  },
  // POS section
  {
    path: '/pos',
    component: POSLayout,
    children: [
      { path: '', name: 'POSHome', component: POSHomePage },
      { path: 'showtimes', name: 'POSShowtimes', component: ShowtimeSelectPage },
      { path: 'seats/:scheduleId', name: 'POSSeats', component: SeatSelectPage, props: true },
      { path: 'payment', name: 'POSPayment', component: PaymentPage },
    ]
  },
  {
    path: '/dashboard',
    component: MainLayout,
    meta: { requiresAuth: true }, // ✅ Tất cả children bên trong cần login
    children: [
      { path: '', name: 'Dashboard', component: DashboardPage },
      { path: 'account', name: 'Account', component: AccountPage },
      { path: 'movies', name: 'Movies', component: MoviePage },
      { path: 'rooms', name: 'Rooms', component: RoomPage },
      { path: 'branches', name: 'Branches', component: BranchPage },
      { path: 'schedule', name: 'Schedule', component: SchedulePage },
      { path: 'bills', name: 'Bills', component: BillPage },
      { path: 'seats', name: 'Seats', component: SeatPage },
      { path: 'invoices', name: 'Invoices', component: InvoicePage },
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../pages/Login.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// ✅ Navigation Guard of Login
router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
  const userInfo = localStorage.getItem('userInfo')
  let userRole = 'user'
  
  if (userInfo) {
    try {
      const parsed = JSON.parse(userInfo)
      userRole = parsed.role || 'user'
      console.log('User role from localStorage:', userRole)
    } catch (e) {
      console.error('Error parsing userInfo:', e)
    }
  }

  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
      } else if (to.path === '/login' && isLoggedIn) {
      // Chuyển hướng dựa vào role khi đã đăng nhập
      const normalizedRole = userRole?.toLowerCase?.() || 'user'
      console.log('Navigation guard - normalized role:', normalizedRole)
      
      if (normalizedRole === 'admin' || normalizedRole === 'administrator' || normalizedRole === 'quản trị viên' || normalizedRole === 'quản lý' || normalizedRole === 'quan ly') {
        console.log('Navigation guard - redirecting to /admin')
        next('/admin')
      } else {
        console.log('Navigation guard - redirecting to /home')
        next('/home')
      }
    } else {
      next()
    }
})


export default router

