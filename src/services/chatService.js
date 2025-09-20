import api, { API_BASE_URL } from './api'

// Create or get chat room
export async function createRoom({ customerId, roomName }) {
  return api.post('/api/chat/rooms', { customerId, roomName })
}

export async function getCustomerRooms(customerId) {
  return api.get(`/api/chat/customer/rooms`, { params: { customerId } })
}

export async function getRoomMessages(roomId, params = {}) {
  return api.get(`/api/chat/rooms/${roomId}/messages`, { params })
}

export async function sendMessage({ roomId, messageContent, messageType = 'TEXT' }) {
  return api.post('/api/chat/messages', { roomId, messageContent, messageType })
}

// SSE stream for AI response
export function openAIStream({ roomId, messageContent, customerId }) {
  const url = `${API_BASE_URL}/api/chat/ai/stream?roomId=${encodeURIComponent(roomId)}&messageContent=${encodeURIComponent(messageContent)}&customerId=${encodeURIComponent(customerId)}`
  return new EventSource(url)
}

export async function assignStaff(roomId, staffId) {
  return api.post(`/api/chat/admin/rooms/${roomId}/assign`, null, { params: { staffId } })
}










