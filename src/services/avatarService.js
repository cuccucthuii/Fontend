import api from './api'

// API endpoints for avatar management
const AVATAR_ENDPOINTS = {
  GET_AVATARS: '/api/v1/avatars',
  UPLOAD_AVATAR: '/api/v1/avatars/upload',
  SET_AVATAR: '/api/v1/avatars/set',
  DELETE_AVATAR: '/api/v1/avatars/delete'
}

// Get list of available avatars
export function getAvailableAvatars() {
  return api.get(AVATAR_ENDPOINTS.GET_AVATARS)
}

// Upload new avatar image
export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('avatar', file)
  
  return api.post(AVATAR_ENDPOINTS.UPLOAD_AVATAR, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// Set user's avatar
export function setUserAvatar(avatarId) {
  return api.post(AVATAR_ENDPOINTS.SET_AVATAR, { avatarId })
}

// Delete user's avatar
export function deleteUserAvatar(avatarId) {
  return api.delete(`${AVATAR_ENDPOINTS.DELETE_AVATAR}/${avatarId}`)
}

// Get user's current avatar
export function getUserAvatar() {
  return api.get('/api/v1/user/avatar')
}

// Default avatar URL
export const DEFAULT_AVATAR_URL = '/logo.png' // Using logo as default avatar

// Generate avatar URL from user info
export function getAvatarUrl(userInfo) {
  if (!userInfo) return DEFAULT_AVATAR_URL
  
  // If user has custom avatar
  if (userInfo.avatar && userInfo.avatar !== '') {
    // If it's a full URL, return as is
    if (userInfo.avatar.startsWith('http')) {
      return userInfo.avatar
    }
    // If it's a relative path, prepend API base URL
    return `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081'}${userInfo.avatar}`
  }
  
  // Return default avatar
  return DEFAULT_AVATAR_URL
}
