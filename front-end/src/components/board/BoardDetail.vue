<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Button, Space, Typography, Input, List, Modal, message } from 'ant-design-vue'
import { authApi } from '@/api/axios'

interface Comment {
  commentId: number
  content: string
  writer: string
  createdAt: string
}

interface BoardDetail {
  boardId: number
  title: string
  content: string
  writer: string
  createdAt: string
  viewCount: number
  likeCount: number
  isLiked: boolean
  isDisliked: boolean
}

const props = defineProps<{
  boardId: number
}>()

const emit = defineEmits(['back'])

const { Title, Text } = Typography

const board = ref<BoardDetail | null>(null)
const comments = ref<Comment[]>([])
const newComment = ref('')
const isEditing = ref(false)
const editedContent = ref('')

const fetchBoardDetail = async () => {
  try {
    const response = await authApi.get(`/board/${props.boardId}`)
    console.log(response.data.result)
    board.value = response.data.result
  } catch (error) {
    console.error('Failed to fetch board detail:', error)
    message.error('게시글을 불러오는데 실패했습니다.')
  }
}

const fetchComments = async () => {
  try {
    const response = await authApi.get('/board/comments', {
      params: {
        boardId: props.boardId,
      },
    })
    comments.value = response.data.data
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
    message.error('신고 처리에 실패했습니다.')
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
  if (!board.value) return
  editedContent.value = board.value.content
  isEditing.value = true
}

const handleUpdate = async () => {
  try {
    await authApi.patch('/board', {
      boardId: props.boardId,
      title: board.value?.title,
      content: editedContent.value,
    })
    isEditing.value = false
    await fetchBoardDetail()
    message.success('게시글이 수정되었습니다.')
  } catch (error) {
    console.error('Failed to update board:', error)
    message.error('게시글 수정에 실패했습니다.')
  }
}

const handleCommentSubmit = async () => {
  if (!newComment.value.trim()) {
    message.warning('댓글 내용을 입력해주세요.')
    return
  }

  try {
    await authApi.post('/board/comment', {
      boardId: props.boardId,
      content: newComment.value,
    })
    newComment.value = ''
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
  <div class="detail-container" v-if="board">
    <div class="board-header">
      <Title :level="2">{{ board.title }}</Title>
      <Space class="board-info">
        <Text>작성자: {{ board.writer }}</Text>
        <Text>작성일: {{ new Date(board.createdAt).toLocaleDateString() }}</Text>
        <Text>조회수: {{ board.viewCount }}</Text>
      </Space>
    </div>

    <div class="board-content">
      <div v-if="!isEditing" class="content-text">
        {{ board.content }}
      </div>
      <div v-else class="edit-area">
        <Input.TextArea v-model:value="editedContent" :rows="10" />
        <Space class="edit-buttons">
          <Button type="primary" @click="handleUpdate">수정완료</Button>
          <Button @click="isEditing = false">취소</Button>
        </Space>
      </div>
    </div>

    <Space class="board-actions">
      <Button :type="board.isLiked ? 'primary' : 'default'" @click="handleLike">
        좋아요 ({{ board.likeCount }})
      </Button>
      <Button :type="board.isDisliked ? 'primary' : 'default'" @click="handleDislike">
        싫어요
      </Button>
      <Button @click="handleReport">신고</Button>
      <Button @click="handleEdit">수정</Button>
      <Button danger @click="handleDelete">삭제</Button>
    </Space>

    <div class="comments-section">
      <Title :level="3">댓글</Title>
      <div class="comment-form">
        <Input.TextArea v-model:value="newComment" placeholder="댓글을 입력하세요" :rows="3" />
        <Button type="primary" @click="handleCommentSubmit" style="margin-top: 8px">
          댓글 작성
        </Button>
      </div>

      <List class="comments-list" :data-source="comments" :row-key="(record) => record.commentId">
        <template #renderItem="{ item }">
          <List.Item>
            <div class="comment">
              <div class="comment-header">
                <Text strong>{{ item.writer }}</Text>
                <Text type="secondary">{{ new Date(item.createdAt).toLocaleDateString() }}</Text>
              </div>
              <div class="comment-content">{{ item.content }}</div>
              <Button type="text" danger @click="handleCommentDelete(item.commentId)">
                삭제
              </Button>
            </div>
          </List.Item>
        </template>
      </List>
    </div>
  </div>
</template>

<style scoped>
.detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.board-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.board-info {
  margin-top: 8px;
}

.board-content {
  margin-bottom: 24px;
  padding: 24px;
  background-color: #fafafa;
  border-radius: 4px;
  min-height: 200px;
}

.edit-area {
  margin-bottom: 16px;
}

.edit-buttons {
  margin-top: 16px;
  justify-content: flex-end;
}

.board-actions {
  margin-bottom: 32px;
}

.comments-section {
  margin-top: 32px;
}

.comment-form {
  margin-bottom: 24px;
}

.comment {
  width: 100%;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.comment-content {
  margin-bottom: 8px;
}
</style>
