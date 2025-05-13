import axios from 'axios'
import { setupInterceptors } from '@/api/interceptors'

const BASE_URL = 'http://localhost:8080/api'

export const authApi = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
})

export const defaultApi = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
})

setupInterceptors()
