# TripGacha (트립가챠)

여행 계획을 더 쉽고 재미있게 만들어주는 여행 플래너 서비스입니다.

### [구현 페이지 확인](https://trail-nail-193.notion.site/2000407f978780d2a7cfd5776cc8998f?pvs=4)

## 서비스 소개

### 여행 계획하기
- 여행지 선택부터 일정 관리까지 한 번에
- 친구들과 함께 실시간으로 여행 계획 세우기
- 카카오맵으로 여행지 둘러보기

### 여행지 둘러보기
- 카카오맵으로 여행지 위치 확인
- 숙소, 관광지, 식당, 카페 정보 한눈에 보기
- 평점을 통해 인기 장소 확인
- 마음에 드는 장소 찜하기

### 여행 계획 결정하기
- 친구들과 함께 실시간으로 장소 선택
- 재미있는 게임으로 여행지 정하기
  - FastClick: 누가 더 빠르게 선택하나
  - 룰렛: 운명의 장소 선택
  - 코인 토스: 간단한 선택
  - 악어 입에 손 넣기: 긴장감 넘치는 선택

### 여행 정보 공유하기
- 다른 여행자들과 여행 관련 후기 공유하기

### AI 여행 도우미
- 여행 관련 질문 답변
- 맞춤형 여행지 추천
- 일정 계획 도움

### 내 여행 관리하기
- 이메일로 간편 가입
- 프로필 이미지와 닉네임 설정
- 찜한 장소 모아보기
- 작성한 여행 후기 관리
- 활동 통계 확인

## 시작하기

### 회원가입
1. 이메일 인증으로 가입
2. 프로필 설정
3. 여행 시작하기

### 여행 계획 시작
1. 방 생성하기
1. 여행지 선택
2. 여행 기간 설정
3. 같이 여행할 친구들을 초대
4. 게임과 함께 즐거운 계획 세우기

### 여행지 둘러보기
1. 관광지 둘러보기 페이지에서 관광지 확인
2. 장소 정보 살펴보기
3. 마음에 드는 곳 찜하기

## 역할 분배
1. 박상찬
- BE, FE, Socket
2. 홍정훈
- BE, FE

## 기술 스택

### Backend
- Java 17
- Spring Boot 3.x
- Spring Security
- JWT Authentication
- Redis
- MySQL
- AWS S3
- Swagger
- WebSocket

### Frontend
- TypeScript
- Vue.js 3
- Vite
- Pinia
- Vue Router
- Ant Design Vue
- Kakao Maps API
- WebSocket Client

## 프로젝트 구조
```
├── back-end/          # Spring Boot 백엔드
├── front-end/         # Vue.js 프론트엔드
├── socket/            # WebSocket 서버
└── etc/               # 기타 프로젝트 진행시 발생한 산출물
```

## 프로젝트 실행하기

### 백엔드 실행
1. Java 17 설치
2. MySQL 설치 및 실행
3. Redis 설치 및 실행
4. `.env` 파일 설정 (socket/ , back-end/ 총 두 위치에 설정)
5. jwt key 설정 (src/main/resources)   
5-1. private_key.pem, private_key_pkcs8.pem, public_key.pem
6. schema 생성 (src/resources/data.sql)
7. Data import (src/resources/Dumpdata.sql)
8. 서버 실행    

9. 회원가입 없이 사용가능한 계정    
9-1. ID: string@test.com, PW: string    
9-2. ID: string@string.com, PW: String12!    
10. 회원가입 시에는      
10-1. 이메일 인증 필요    
10-2. 비밀번호(8자 이상, 대소문자, 숫자, 특수문자) 설정 필요    

- env 파일 내용
```
# 이메일 발송 설정 (구글 이메일, 구글 APP 비밀번호)
MAIL_USERNAME=your_email@example.com
MAIL_PASSWORD=your_email_password_or_app_key

# AWS S3 설정
S3_BUCKET_NAME=your_s3_bucket_name
S3_ACCESS_KEY=your_s3_access_key
S3_SECRET_KEY=your_s3_secret_key
S3_REGION=your_s3_region (예: ap-northeast-2)

# OpenAI API 키 (GPT API Key)
OPEN_API_KEY=your_openai_api_key

# 데이터베이스 설정 (MySQL)
DB_URL=your_db_url
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# Redis 설정
REDIS_DB_URL=redis_db_url
REDIS_DB_PORT=6379

```

- jwy key 생성
```
1. RSA 개인 키 생성 (PKCS#1 형식)
openssl genrsa -out private_key.pem 2048

2. 개인 키를 PKCS#8 형식으로 변환
openssl pkcs8 -topk8 -inform PEM -in private_key.pem -outform PEM -nocrypt -out private_key_pkcs8.pem

3. 공개 키 추출
openssl rsa -in private_key.pem -pubout -out public_key.pem
```


### 프론트엔드 실행
1. Node.js 설치
2. 프로젝트 루트 디렉토리에서:
```bash
cd front-end
npm install
npm run dev
```
3. `.env` 파일 설정
4. 서버 실행

- env 파일 내용
```
# kakao developer API (애플리케이션 등록 - 플랫폼 Web (http://localhost:5173) 등록 - 카카오맵 활성화 설정 상태 ON)
# 앱 키 (JavaScript 키)
VITE_KAKAO_MAP_API_KEY=api_key
```
