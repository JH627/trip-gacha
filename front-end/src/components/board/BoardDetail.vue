<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Typography, Modal, message } from 'ant-design-vue'
import { authApi } from '@/api/axios'
import { Comment, BoardDetail } from '@/types/board'
import BoardHeader from './BoardHeader.vue'
import BoardContent from './BoardContent.vue'
import BoardActions from './BoardActions.vue'
import CommentList from './CommentList.vue'
import CommentForm from './CommentForm.vue'
import BoardEdit from './BoardEdit.vue'

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
    await fetchBoardDetail()
    message.success('좋아요가 반영되었습니다.')
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
    await fetchBoardDetail()
    message.success('싫어요가 반영되었습니다.')
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
    <BoardHeader :board="board" />
    <BoardContent :board="board" @update="handleUpdate" />
    <BoardActions
      :board="board"
      @like="handleLike"
      @dislike="handleDislike"
      @report="handleReport"
      @edit="handleEdit"
      @delete="handleDelete"
    />

    <div class="comments-section">
      <Title :level="3">댓글</Title>
      <CommentForm @submit="handleCommentSubmit" />
      <CommentList :comments="comments" @delete="handleCommentDelete" />
    </div>
  </div>
</template>

<style scoped>
.detail-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px;
  background-color: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.comments-section {
  margin-top: 40px;
  padding-top: 32px;
  border-top: 2px solid #f0f0f0;
}
</style>
