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
