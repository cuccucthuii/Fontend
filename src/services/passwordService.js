import api from './api'

export const passwordService = {
  // Đổi mật khẩu
  async changePassword(passwordData) {
    try {
      const response = await api.post('/api/user/change-password', passwordData)
      return response.data
    } catch (error) {
      console.error('Error changing password:', error)
      throw error
    }
  },

  // Kiểm tra mật khẩu hiện tại
  async verifyCurrentPassword(password) {
    try {
      const response = await api.post('/api/auth/verify-password', { password })
      return response.data
    } catch (error) {
      console.error('Error verifying password:', error)
      throw error
    }
  },

  // Gửi email reset mật khẩu
  async requestPasswordReset(email) {
    try {
      const response = await api.post('/api/auth/forgot-password', { email })
      return response.data
    } catch (error) {
      console.error('Error requesting password reset:', error)
      throw error
    }
  },

  // Reset mật khẩu với token
  async resetPassword(token, newPassword) {
    try {
      const response = await api.post('/api/auth/reset-password', {
        token,
        password: newPassword
      })
      return response.data
    } catch (error) {
      console.error('Error resetting password:', error)
      throw error
    }
  }
}

export default passwordService
