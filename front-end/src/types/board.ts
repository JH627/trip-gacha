// 게시글 목록(헤더) 타입
export interface BoardHeader {
  boardId: number
  title: string
  authorName: string
  createdAt: string
  viewCount: number
  likeCount: number
  commentCount: number
  category: string
}

// 검색 조건 타입 (프론트에서 사용)
export interface SearchBoardCondition {
  category?: string
  // page, size 등 필요시 추가
}

export interface SearchParams {
  type: string
  keyword: string
}

export type BoardView = 'list' | 'write' | 'detail'

export interface BoardListProps {
  searchType?: string
  searchKeyword?: string
}

export interface BoardDetailProps {
  boardId: number
}
