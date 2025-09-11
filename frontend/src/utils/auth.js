import Cookies from 'js-cookie'
import { store } from 'lucas-my-form'

const TokenKey = 'magic-crm-token'

export function getToken() {
  const token = Cookies.get(TokenKey)

  if(store.JWTToken!=token){
    store.JWTToken=token;
  }
  return token;
}

export function setToken(token) {
  store.JWTToken=token;
  return Cookies.set(TokenKey, token, { expires: 7 })
}

export function removeToken() {
  store.JWTToken = null;
  return Cookies.remove(TokenKey)
}