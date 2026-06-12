const USER_KEY = 'admission_user'
const TOKEN_KEY = 'admission_token'

export function getUser() {
  try {
    const user = localStorage.getItem(USER_KEY)
    return user ? JSON.parse(user) : null
  } catch {
    return null
  }
}

export function setUser(user) {
  // 分离存储 token 和用户信息
  if (user.token) {
    localStorage.setItem(TOKEN_KEY, user.token)
    delete user.token
  }
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function removeUser() {
  localStorage.removeItem(USER_KEY)
  localStorage.removeItem(TOKEN_KEY)
}

export function isAuthenticated() {
  return getToken() !== null || getUser() !== null
}
