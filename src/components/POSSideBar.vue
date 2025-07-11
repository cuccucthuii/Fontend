<template>
    <nav class="sidebar-menu">
      <ul class="menu-list">
        <li>
          <router-link to="/pos" exact-active-class="active">
            🏠 Trang chính POS
          </router-link>
        </li>
        <li>
          <router-link to="/pos/showtimes" active-class="active">
            🗓️ Chọn suất chiếu
          </router-link>
        </li>
        <!-- <li>
          <router-link to="/pos" active-class="active">
            💺 Chọn ghế
          </router-link>
        </li>
        <li>
          <router-link to="/pos" active-class="active">
            💰 Thanh toán
          </router-link>
        </li>
        <li>
          <router-link to="/pos" active-class="active">
            🎫 ...
          </router-link>
        </li> -->
      </ul>
      <div class="logout-section">
        <hr />
        <button class="signout" @click="signOut">🚪 Đăng xuất</button>
      </div>
    </nav>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router'
  
  const router = useRouter()
  
  function signOut() {
    localStorage.clear()
    router.push('/login')
  }
  </script>
  
  <style scoped>
  .sidebar-menu {
    display: flex;
    flex-direction: column;
    height: 100%;
    padding: 16px 20px;
    box-sizing: border-box;
    overflow-y: auto;
    background-color: #2c3e50;
    color: #ecf0f1;
  }
  
  /* Menu list */
  .menu-list {
    list-style: none;
    padding: 0;
    margin: 0;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
  }
  
  /* Card style for each item */
  .menu-list li {
    background-color: rgba(255, 255, 255, 0.07);
    border-radius: 10px;
    margin-bottom: 14px;
    padding: 14px 18px;
    transition: background-color 0.2s ease;
    cursor: pointer;
    width: 100%;
  }
  
  .menu-list li:hover {
    background-color: rgba(255, 255, 255, 0.15);
  }
  
  .menu-list a {
    color: #ecf0f1;
    text-decoration: none;
    display: block;
    width: 100%;
    font-size: 17px;
  }
  
  .menu-list a.active {
    font-weight: bold;
    text-decoration: underline;
  }
  
  /* Logout section at bottom */
  .logout-section {
    margin-top: auto;
    border-top: 1px solid #7f8c8d;
    padding-top: 16px;
  }
  
  /* Logout button styled as card */
  .signout {
    background-color: rgba(255, 255, 255, 0.07);
    border: none;
    border-radius: 10px;
    color: #ecf0f1;
    padding: 14px 18px;
    width: 100%;
    text-align: left;
    cursor: pointer;
    font-size: 17px;
    transition: background-color 0.2s ease;
  }
  
  .signout:hover {
    background-color: rgba(255, 255, 255, 0.15);
    text-decoration: underline;
  }
  </style>
  