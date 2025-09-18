import api from './api'

export function fetchBranches() {
  return api.get('/api/rap_chieu')
}

export function deleteBranch(id) {
  return api.delete(`/api/rap_chieu/${id}`)
}

export function updateBranch(id, updatedData) {
  return api.put(`/api/rap_chieu/${id}`, updatedData)
}

export function createBranch(data) {
  return api.post('/api/rap_chieu', data)
}

export function getNearbyBranches({ lat, lng, radiusKm = 25, limit = 50 } = {}) {
  return api.get('/api/rap_chieu/nearby', {
    params: { lat, lng, radiusKm, limit }
  })
}
