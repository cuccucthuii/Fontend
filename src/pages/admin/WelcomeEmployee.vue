<template>
    <div class="employee-welcome">
        <div class="welcome-card">
            <div class="welcome-icon">
                <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <circle cx="32" cy="32" r="32" fill="url(#grad)" />
                    <g>
                        <path d="M32 18a10 10 0 1 1 0 20a10 10 0 0 1 0-20z" fill="#fff" />
                        <path d="M16 48c0-8.84 7.16-16 16-16s16 7.16 16 16" fill="#fff" />
                    </g>
                    <defs>
                        <linearGradient id="grad" x1="0" y1="0" x2="64" y2="64" gradientUnits="userSpaceOnUse">
                            <stop stop-color="#48dbfb" />
                            <stop offset="1" stop-color="#bfa2db" />
                        </linearGradient>
                    </defs>
                </svg>
            </div>
            <h1>Chào mừng Nhân viên!</h1>
            <p class="subtitle">Bạn đã đăng nhập vào hệ thống quản lý rạp chiếu phim <b>DEV CINEMA</b> với quyền nhân
                viên.</p>
            <div class="quick-actions">
                <router-link to="/admin/movies" class="quick-btn">
                    <span>🎬</span> Quản lý phim
                </router-link>
                <router-link to="/admin/rooms" class="quick-btn">
                    <span>🏢</span> Quản lý phòng
                </router-link>
                <router-link to="/admin/seats" class="quick-btn">
                    <span>💺</span> Quản lý ghế ngồi
                </router-link>
                <router-link to="/admin/schedule" class="quick-btn">
                    <span>🗓️</span> Quản lý lịch chiếu
                </router-link>
                <router-link to="/admin/bills" class="quick-btn">
                    <span>🎟️</span> Quản lý vé phim
                </router-link>
                <router-link to="/admin/invoices" class="quick-btn">
                    <span>🧾</span> Quản lý hóa đơn
                </router-link>
            </div>
            <div class="desc">Hãy chọn chức năng bên trái hoặc sử dụng các nút nhanh bên dưới để bắt đầu công việc của
                bạn.</div>
        </div>
    </div>
</template>

<style scoped>
.employee-welcome {
    min-height: 80vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #e0f7fa 0%, #f8fafc 100%);
}

.welcome-card {
    background: #fff;
    border-radius: 24px;
    box-shadow: 0 8px 40px rgba(72, 219, 251, 0.13), 0 2px 8px rgba(44, 62, 80, 0.08);
    padding: 48px 36px 40px 36px;
    max-width: 480px;
    width: 100%;
    text-align: center;
    position: relative;
    animation: popIn 0.7s cubic-bezier(.4, 2, .6, 1);
}

@keyframes popIn {
    from {
        transform: scale(0.92);
        opacity: 0;
    }

    to {
        transform: scale(1);
        opacity: 1;
    }
}

.welcome-icon {
    margin-bottom: 18px;
    display: flex;
    justify-content: center;
    align-items: center;
    animation: bounceIn 1.2s;
}

@keyframes bounceIn {
    0% {
        transform: scale(0.7);
        opacity: 0;
    }

    60% {
        transform: scale(1.15);
        opacity: 1;
    }

    100% {
        transform: scale(1);
    }
}

h1 {
    font-size: 2.2rem;
    font-weight: 900;
    background: linear-gradient(90deg, #48dbfb 10%, #bfa2db 90%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin-bottom: 10px;
    letter-spacing: 1px;
}

.subtitle {
    font-size: 1.1rem;
    color: #5f5f5f;
    margin-bottom: 28px;
}

.quick-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    justify-content: center;
    margin-bottom: 24px;
}

.quick-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    background: linear-gradient(135deg, #48dbfb 0%, #bfa2db 100%);
    color: #fff;
    font-weight: 700;
    font-size: 1rem;
    border: none;
    border-radius: 12px;
    padding: 12px 22px;
    box-shadow: 0 2px 12px rgba(72, 219, 251, 0.10);
    text-decoration: none;
    transition: background 0.2s, transform 0.2s;
}

.quick-btn:hover {
    background: linear-gradient(135deg, #bfa2db 0%, #48dbfb 100%);
    transform: translateY(-2px) scale(1.04);
}

.desc {
    color: #7f8c8d;
    font-size: 1rem;
    margin-top: 10px;
}

@media (max-width: 600px) {
    .welcome-card {
        padding: 24px 8px 24px 8px;
        max-width: 98vw;
    }

    h1 {
        font-size: 1.3rem;
    }

    .quick-btn {
        font-size: 0.95rem;
        padding: 10px 10px;
    }
}
</style>
