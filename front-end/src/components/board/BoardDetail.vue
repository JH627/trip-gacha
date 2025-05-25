<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Typography, Modal, message, Button } from 'ant-design-vue'
import { authApi } from '@/api/axios'
import type { Comment, BoardDetail } from '@/types/board'
import BoardHeader from './BoardHeader.vue'
import BoardContent from './BoardContent.vue'
import BoardActions from './BoardActions.vue'
import CommentList from './CommentList.vue'
import CommentForm from './CommentForm.vue'
import BoardEdit from './BoardEdit.vue'
import { ArrowLeftOutlined } from '@ant-design/icons-vue'

const props = defineProps<{
  boardId: number
}>()

const emit = defineEmits(['back'])

const { Title } = Typography

const board = ref<BoardDetail | null>(null)
const comments = ref<Comment[]>([])
const isEditing = ref(false)

const fetchBoardDetail = async () => {
  try {
    const response = await authApi.get(`/board/${props.boardId}`)
    board.value = response.data.result
  } catch (error) {
    console.error('Failed to fetch board detail:', error)
    message.error('게시글을 불러오는데 실패했습니다.')
  }
}

const fetchComments = async () => {
  try {
    const response = await authApi.get('/board/comments?page=1&offset=10', {
      params: {
        boardId: props.boardId,
      },
    })
    comments.value = response.data.result
  } catch (error) {
    console.error('Failed to fetch comments:', error)
    message.error('댓글을 불러오는데 실패했습니다.')
  }
}

const handleLike = async () => {
  try {
    await authApi.post('/board/like', null, {
      params: { boardId: props.boardId },
    })
    if (board.value) {
      // 좋아요 상태 토글
      board.value.isLiked = !board.value.isLiked
      // 좋아요 개수 업데이트
      board.value.likeCount += board.value.isLiked ? 1 : -1
      // 싫어요가 눌려있었다면 해제
      if (board.value.isDisliked) {
        board.value.isDisliked = false
      }
    }
    message.success(board.value?.isLiked ? '좋아요가 반영되었습니다.' : '좋아요가 취소되었습니다.')
  } catch (error) {
    console.error('Failed to like board:', error)
    message.error('좋아요 처리에 실패했습니다.')
  }
}

const handleDislike = async () => {
  try {
    await authApi.post('/board/dislike', null, {
      params: { boardId: props.boardId },
    })
    if (board.value) {
      // 싫어요 상태 토글
      board.value.isDisliked = !board.value.isDisliked
      // 좋아요가 눌려있었다면 해제
      if (board.value.isLiked) {
        board.value.isLiked = false
        board.value.likeCount -= 1
      }
    }
    message.success(board.value?.isDisliked ? '싫어요가 반영되었습니다.' : '싫어요가 취소되었습니다.')
  } catch (error) {
    console.error('Failed to dislike board:', error)
    message.error('싫어요 처리에 실패했습니다.')
  }
}

const handleReport = async () => {
  try {
    await authApi.post('/board/report', null, {
      params: { boardId: props.boardId },
    })
    message.success('신고가 접수되었습니다.')
  } catch (error) {
    console.error('Failed to report board:', error)
    message.error('이미 신고한 게시글입니다.')
  }
}

const handleDelete = () => {
  Modal.confirm({
    title: '게시글 삭제',
    content: '정말로 이 게시글을 삭제하시겠습니까?',
    okText: '삭제',
    okType: 'danger',
    cancelText: '취소',
    onOk: async () => {
      try {
        await authApi.delete(`/board/${props.boardId}`)
        message.success('게시글이 삭제되었습니다.')
        emit('back')
      } catch (error) {
        console.error('Failed to delete board:', error)
        message.error('게시글 삭제에 실패했습니다.')
      }
    },
  })
}

const handleEdit = () => {
  isEditing.value = true
}

const handleUpdate = () => {
  isEditing.value = false
  fetchBoardDetail()
}

const handleCommentSubmit = async (content: string) => {
  try {
    await authApi.post('/board/comment', {
      boardId: props.boardId,
      content,
    })
    await fetchComments()
    message.success('댓글이 작성되었습니다.')
  } catch (error) {
    console.error('Failed to add comment:', error)
    message.error('댓글 작성에 실패했습니다.')
  }
}

const handleCommentDelete = (commentId: number) => {
  Modal.confirm({
    title: '댓글 삭제',
    content: '이 댓글을 삭제하시겠습니까?',
    okText: '삭제',
    okType: 'danger',
    cancelText: '취소',
    onOk: async () => {
      try {
        await authApi.delete(`/board/comment/${commentId}`)
        await fetchComments()
        message.success('댓글이 삭제되었습니다.')
      } catch (error) {
        console.error('Failed to delete comment:', error)
        message.error('댓글 삭제에 실패했습니다.')
      }
    },
  })
}

onMounted(() => {
  fetchBoardDetail()
  fetchComments()
})
</script>

<template>
  <div v-if="isEditing">
    <BoardEdit :board-id="boardId" @back="isEditing = false" @update="handleUpdate" />
  </div>
  <div v-else class="detail-container" v-if="board">
    <div class="back-button-container">
      <Button @click="emit('back')" type="link" class="back-button">
        <template #icon><ArrowLeftOutlined /></template>
        목록으로 돌아가기
      </Button>
    </div>
    <div class="content-wrapper">
      <div class="board-card">
        <BoardHeader :board="board" />
        <div class="divider"></div>
        <BoardContent :board="board" @update="handleUpdate" />
        <div class="divider"></div>
        <BoardActions
          :board="board"
          @like="handleLike"
          @dislike="handleDislike"
          @report="handleReport"
          @edit="handleEdit"
          @delete="handleDelete"
        />
      </div>

      <div class="comments-section">
        <div class="comments-header">
          <Title :level="3" class="comments-title">댓글</Title>
          <span class="comment-count">{{ comments.length }}개의 댓글</span>
        </div>
        <CommentForm @submit="handleCommentSubmit" />
        <CommentList :comments="comments" @delete="handleCommentDelete" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.detail-container {
  width: 100%;
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.back-button-container {
  margin-bottom: 16px;
}

.back-button {
  font-size: 14px;
  color: #666;
  transition: all 0.2s;
  padding: 8px 16px;
}

.back-button:hover {
  color: #1890ff;
  background-color: rgba(24, 144, 255, 0.1);
  border-radius: 4px;
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.board-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 16px;
  transition: box-shadow 0.2s;
}

.board-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.divider {
  height: 1px;
  background-color: #f0f0f0;
  margin: 16px 0;
}

.comments-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 16px;
}

.comments-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.comments-title {
  margin: 0 !important;
  font-size: 16px;
  font-weight: bold;
  color: #222;
}

.comment-count {
  font-size: 13px;
  color: #888;
}

:deep(.ant-typography) {
  color: #222;
}

:deep(.ant-btn-link) {
  height: auto;
  padding: 4px 8px;
}

:deep(.ant-btn-link:hover) {
  background-color: rgba(24, 144, 255, 0.1);
  border-radius: 4px;
}

:deep(.ant-btn-danger) {
  color: #fff;
  border-color: #ff4d4f;
  background: #ff4d4f;
}

:deep(.ant-btn-danger:hover) {
  color: #fff;
  border-color: #ff4d4f;
  background: #ff4d4f;
}

@media (max-width: 768px) {
  .detail-container {
    padding: 16px;
  }

  .board-card,
  .comments-section {
    padding: 12px;
  }
}
</style>
