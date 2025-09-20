import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import axios from "axios";
import "./style.css";
import "./assets/global-fonts.css";
import Antd from "ant-design-vue";
import "ant-design-vue/dist/reset.css";

// ✅ Cấu hình axios defaults
axios.defaults.headers.common["ngrok-skip-browser-warning"] = "true"; // Fix lỗi ngrok 6024

// ✅ Gán sẵn token nếu có
const token = localStorage.getItem("token");
if (token) {
  axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
}

const app = createApp(App);
app.use(router);
app.use(Antd);
app.mount("#app");
