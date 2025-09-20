import api from './api'

// Social Authentication Service
export const socialAuthService = {
  // Google OAuth Login
  async loginWithGoogle(googleToken) {
    try {
      const response = await api.post('/api/auth/social/google', {
        token: googleToken
      })
      return response.data
    } catch (error) {
      console.error('Google login error:', error)
      throw error
    }
  },

  // Facebook OAuth Login
  async loginWithFacebook(facebookToken) {
    try {
      const response = await api.post('/api/auth/social/facebook', {
        token: facebookToken
      })
      return response.data
    } catch (error) {
      console.error('Facebook login error:', error)
      throw error
    }
  },

  // Link social account to existing user
  async linkSocialAccount(provider, token, userId) {
    try {
      const response = await api.post('/api/auth/social/link', {
        provider,
        token,
        userId
      })
      return response.data
    } catch (error) {
      console.error('Link social account error:', error)
      throw error
    }
  },

  // Get linked social accounts
  async getLinkedAccounts(userId) {
    try {
      const response = await api.get(`/api/auth/social/linked-accounts?userId=${userId}`)
      return response.data
    } catch (error) {
      console.error('Get linked accounts error:', error)
      throw error
    }
  },

  // Unlink social account
  async unlinkSocialAccount(provider, userId) {
    try {
      const response = await api.delete(`/api/auth/social/link`, {
        data: { provider, userId }
      })
      return response.data
    } catch (error) {
      console.error('Unlink social account error:', error)
      throw error
    }
  }
}

// Google OAuth Helper
export const googleAuthHelper = {
  // Load Google API
  loadGoogleAPI(clientId) {
    return new Promise((resolve, reject) => {
      if (window.google) {
        resolve()
        return
      }

      const script = document.createElement('script')
      script.src = 'https://accounts.google.com/gsi/client'
      script.onload = () => {
        resolve()
      }
      script.onerror = () => reject(new Error('Failed to load Google API'))
      document.head.appendChild(script)
    })
  },

  // Initialize Google OAuth and get token
  initGoogleAuth(clientId) {
    return new Promise((resolve, reject) => {
      if (!window.google) {
        reject(new Error('Google API not loaded'))
        return
      }

      const tokenClient = window.google.accounts.oauth2.initTokenClient({
        client_id: clientId,
        scope: 'email profile',
        callback: (response) => {
          if (response.error) {
            reject(new Error(response.error))
          } else {
            resolve(response.access_token)
          }
        }
      })

      // Request access token
      tokenClient.requestAccessToken()
    })
  }
}

// Facebook OAuth Helper
export const facebookAuthHelper = {
  // Initialize Facebook SDK
  initFacebookSDK(appId) {
    return new Promise((resolve, reject) => {
      if (window.FB) {
        resolve()
        return
      }

      window.fbAsyncInit = function() {
        window.FB.init({
          appId: appId,
          cookie: true,
          xfbml: true,
          version: 'v18.0'
        })
        resolve()
      }

      const script = document.createElement('script')
      script.src = 'https://connect.facebook.net/en_US/sdk.js'
      script.async = true
      script.defer = true
      script.onerror = () => reject(new Error('Failed to load Facebook SDK'))
      document.head.appendChild(script)
    })
  },

  // Facebook Login
  facebookLogin() {
    return new Promise((resolve, reject) => {
      if (!window.FB) {
        reject(new Error('Facebook SDK not loaded'))
        return
      }

      window.FB.login((response) => {
        if (response.authResponse) {
          resolve(response.authResponse.accessToken)
        } else {
          reject(new Error('Facebook login failed'))
        }
      }, { scope: 'email,public_profile' })
    })
  },

  // Get Facebook User Info
  getFacebookUserInfo(accessToken) {
    return new Promise((resolve, reject) => {
      if (!window.FB) {
        reject(new Error('Facebook SDK not loaded'))
        return
      }

      window.FB.api('/me', { fields: 'id,name,email,picture' }, (response) => {
        if (response.error) {
          reject(new Error(response.error.message))
        } else {
          resolve(response)
        }
      })
    })
  }
}

// Check if credentials are configured
export const checkCredentials = {
  isGoogleConfigured() {
    const googleId = import.meta.env.VITE_GOOGLE_CLIENT_ID || '130831958540-2v6n7uvig4etm5inr8s4qtqm369sib91.apps.googleusercontent.com'
    return !!googleId && googleId !== 'your-google-client-id-here'
  },

  isFacebookConfigured() {
    const facebookId = import.meta.env.VITE_FACEBOOK_APP_ID || 'your-facebook-app-id-here'
    return !!facebookId && facebookId !== 'your-facebook-app-id-here'
  },

  getCredentials() {
    const googleId = import.meta.env.VITE_GOOGLE_CLIENT_ID || '130831958540-2v6n7uvig4etm5inr8s4qtqm369sib91.apps.googleusercontent.com'
    const facebookId = import.meta.env.VITE_FACEBOOK_APP_ID || 'your-facebook-app-id-here'
    
    return {
      google: (googleId && googleId !== 'your-google-client-id-here') ? 'Configured' : 'Not configured',
      facebook: (facebookId && facebookId !== 'your-facebook-app-id-here') ? 'Configured' : 'Not configured'
    }
  }
}

export default socialAuthService
