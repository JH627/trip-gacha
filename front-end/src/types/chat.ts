export interface ChatMessage {
  text: string
  isUser: boolean
}

export interface ChatRequest {
  content: string
}

export interface ChatResponse {
  result: string
} 