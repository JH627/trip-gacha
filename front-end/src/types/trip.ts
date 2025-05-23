// 추천 여행지 타입
export interface Destination {
  id: number
  name: string
  description: string
  img: string
}

// 여행 일정 정보
export interface ScheduleInfo {
  scheduleId: number
  title: string
  destination: string
  destinationImg: string
  startDate: string
  endDate: string
  createAt: string
}

// 여행지 정보
export interface SpotInfo {
  spotId: number
  destination: string
  name: string
  content: string
  img: string
  address: string
  likes: number
  stars: number
  category: string
  phone: string
  workTime: string
  marked: boolean
}

// 여행 일정 상세 정보
export interface ScheduleDetailItem {
  day: number
  spotInfo: SpotInfo
  order: number
}

// 여행 일정 상세 정보
export interface ScheduleDetail {
  title: string
  startDate: string
  endDate: string
  createAt: string
  scheduleDetailItems: ScheduleDetailItem[]
}

// 관광지 카테고리 타입
export const SPOT_CATEGORIES = [
  { value: 'ALLSPOT', label: '모든 관광지' },
  { value: 'ATTRACTION', label: '명소' },
  { value: 'RESTAURANT', label: '식당' },
  { value: 'CAFE', label: '카페' },
  { value: 'MARKED', label: '찜 목록' },
] as const

export type SpotCategory = 'ATTRACTION' | 'RESTAURANT' | 'CAFE' | 'MARKED' | 'ALLSPOT'

// 관광지 검색 파라미터 타입
export interface SpotParams {
  category: SpotCategory
  sort: 'LIKE' | 'STARS' | 'NAME'
  page: number
  destinationId?: number
  keyword?: string
}
