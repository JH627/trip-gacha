INSERT INTO spot_categories (name) VALUES
('ACCOMMODATION'),
('ATTRACTION'),
('RESTAURANT'),
('CAFE');

INSERT INTO users (email, password, nickname, profile_img, role, created_at)
VALUES
('user1@example.com', 'pass123', 'user1', NULL, 0, NOW()),
('user2@example.com', 'pass123', 'user2', NULL, 0, NOW()),
('user3@example.com', 'pass123', 'user3', NULL, 0, NOW()),
('user4@example.com', 'pass123', 'user4', NULL, 0, NOW()),
('user5@example.com', 'pass123', 'user5', NULL, 0, NOW()),
('user6@example.com', 'pass123', 'user6', NULL, 0, NOW()),
('user7@example.com', 'pass123', 'user7', NULL, 0, NOW()),
('user8@example.com', 'pass123', 'user8', NULL, 0, NOW()),
('user9@example.com', 'pass123', 'user9', NULL, 0, NOW()),
('user10@example.com', 'pass123', 'user10', NULL, 0, NOW()),
('user11@example.com', 'pass123', 'user11', NULL, 0, NOW()),
('user12@example.com', 'pass123', 'user12', NULL, 0, NOW()),
('user13@example.com', 'pass123', 'user13', NULL, 0, NOW()),
('user14@example.com', 'pass123', 'user14', NULL, 0, NOW()),
('user15@example.com', 'pass123', 'user15', NULL, 0, NOW()),
('user16@example.com', 'pass123', 'user16', NULL, 0, NOW()),
('user17@example.com', 'pass123', 'user17', NULL, 0, NOW()),
('user18@example.com', 'pass123', 'user18', NULL, 0, NOW()),
('user19@example.com', 'pass123', 'user19', NULL, 0, NOW()),
('user20@example.com', 'pass123', 'user20', NULL, 0, NOW());

INSERT INTO destinations (name, country, img) VALUES
('Seoul',     'South Korea', 'https://picsum.photos/seed/Seoul/1200/800'),
('Busan',     'South Korea', 'https://picsum.photos/seed/Busan/1200/800'),
('Jeju',      'South Korea', 'https://picsum.photos/seed/Jeju/1200/800'),
('Tokyo',     'Japan',        'https://picsum.photos/seed/Tokyo/1200/800'),
('Osaka',     'Japan',        'https://picsum.photos/seed/Osaka/1200/800'),
('Kyoto',     'Japan',        'https://picsum.photos/seed/Kyoto/1200/800'),
('New York',  'USA',          'https://picsum.photos/seed/New%20York/1200/800'),
('Los Angeles','USA',         'https://picsum.photos/seed/Los%20Angeles/1200/800'),
('Paris',     'France',       'https://picsum.photos/seed/Paris/1200/800'),
('London',    'UK',           'https://picsum.photos/seed/London/1200/800'),
('Bangkok',   'Thailand',     'https://picsum.photos/seed/Bangkok/1200/800'),
('Singapore', 'Singapore',    'https://picsum.photos/seed/Singapore/1200/800'),
('Sydney',    'Australia',    'https://picsum.photos/seed/Sydney/1200/800'),
('Toronto',   'Canada',       'https://picsum.photos/seed/Toronto/1200/800'),
('Rome',      'Italy',        'https://picsum.photos/seed/Rome/1200/800'),
('Berlin',    'Germany',      'https://picsum.photos/seed/Berlin/1200/800'),
('Barcelona','Spain',         'https://picsum.photos/seed/Barcelona/1200/800'),
('Hanoi',     'Vietnam',      'https://picsum.photos/seed/Hanoi/1200/800'),
('Istanbul',  'Turkey',       'https://picsum.photos/seed/Istanbul/1200/800'),
('Dubai',     'UAE',          'https://picsum.photos/seed/Dubai/1200/800');



INSERT INTO spots (destination_id, name, content, img, address, likes, stars, category_id, phone, work_time)
VALUES
(1, 'Gyeongbokgung Palace', 'Historic palace in Seoul', 'https://upload.wikimedia.org/wikipedia/commons/7/76/Korea-Seoul-Gyeongbokgung-02.jpg', '161 Sajik-ro, Jongno-gu, Seoul', 120, 5, 2, '02-3700-3900', '09:00-18:00'),
(2, 'Haeundae Beach', 'Famous beach in Busan', 'https://upload.wikimedia.org/wikipedia/commons/1/1b/Haeundae_Beach_Busan.jpg', '264 Haeundaehaebyeon-ro, Busan', 85, 4, 2, '051-749-7612', '24 hours'),
(3, 'Jeju Loveland', 'Theme sculpture park in Jeju', 'https://upload.wikimedia.org/wikipedia/commons/a/a4/Jeju_Loveland_Statues.jpg', '680-26 Yeon-dong, Jeju', 65, 4, 2, '064-712-6988', '09:00-20:00'),
(4, 'Tokyo Tower', 'Observation tower in Tokyo', 'https://upload.wikimedia.org/wikipedia/commons/f/f0/Tokyo_Tower_and_around_Skyscrapers.jpg', '4 Chome-2-8 Shibakoen, Minato City, Tokyo', 230, 5, 2, '03-3433-5111', '09:00-23:00'),
(5, 'Shibuya Crossing', 'Iconic intersection in Tokyo', 'https://upload.wikimedia.org/wikipedia/commons/2/2f/Shibuya_Crossing%2C_Tokyo%2C_Japan.jpg', 'Shibuya, Tokyo', 150, 5, 2, NULL, '24 hours'),
(6, 'Eiffel Tower', 'Landmark of Paris', 'https://upload.wikimedia.org/wikipedia/commons/a/a8/Tour_Eiffel_Wikimedia_Commons.jpg', 'Champ de Mars, 5 Avenue Anatole France, Paris', 320, 5, 2, NULL, '09:30-23:45'),
(7, 'Louvre Museum', 'Art museum in Paris', 'https://upload.wikimedia.org/wikipedia/commons/3/3b/Louvre_Museum_Wikimedia_Commons.jpg', 'Rue de Rivoli, 75001 Paris', 275, 5, 2, NULL, '09:00-18:00'),
(8, 'Big Ben', 'Clock tower in London', 'https://upload.wikimedia.org/wikipedia/commons/3/3e/Big_Ben_2012-06-23.jpg', 'Westminster, London', 180, 4, 2, NULL, '24 hours'),
(9, 'Sydney Opera House', 'Famous performing arts center', 'https://upload.wikimedia.org/wikipedia/commons/4/40/Sydney_Opera_House_Sails.jpg', 'Bennelong Point, Sydney NSW', 250, 5, 2, NULL, '09:00-21:00'),
(10, 'CN Tower', 'Observation tower in Toronto', 'https://upload.wikimedia.org/wikipedia/commons/9/9a/CN_Tower_March_2004.jpg', '290 Bremner Blvd, Toronto', 210, 4, 2, NULL, '10:00-22:30'),
(11, 'Burj Khalifa', 'Tallest skyscraper in Dubai', 'https://upload.wikimedia.org/wikipedia/commons/9/93/Burj_Khalifa.jpg', '1 Sheikh Mohammed bin Rashid Blvd, Dubai', 400, 5, 2, NULL, '08:00-23:00'),
(12, 'Marina Bay Sands', 'Luxury resort in Singapore', 'https://upload.wikimedia.org/wikipedia/commons/1/11/Marina_Bay_Sands.jpg', '10 Bayfront Ave, Singapore', 310, 5, 1, NULL, '24 hours'),
(13, 'Grand Palace', 'Historic site in Bangkok', 'https://upload.wikimedia.org/wikipedia/commons/9/9b/Grand_Palace_Bangkok.jpg', 'Na Phra Lan Rd, Bangkok', 195, 4, 2, NULL, '08:30-15:30'),
(14, 'Blue House Cafe', 'Cute themed cafe in Seoul', 'https://upload.wikimedia.org/wikipedia/commons/b/b2/Blue_House_Seoul.jpg', 'Cheongwadae, Jongno-gu, Seoul', 88, 4, 4, '02-730-5800', '10:00-22:00'),
(15, 'Yonggungsa Temple', 'Temple by the sea in Busan', 'https://upload.wikimedia.org/wikipedia/commons/0/06/Haedong_Yonggungsa_Busan.jpg', '86 Yonggung-gil, Busan', 134, 5, 2, NULL, '05:00-19:00'),
(16, 'Tosokchon Samgyetang', 'Popular chicken soup restaurant', 'https://upload.wikimedia.org/wikipedia/commons/b/b7/Tosokchon_Samgyetang.jpg', '5 Jahamun-ro 5-gil, Seoul', 180, 5, 3, '02-737-7444', '10:00-22:00'),
(17, 'Cafe de One Piece', 'Anime-themed cafe', 'https://upload.wikimedia.org/wikipedia/commons/7/78/One_Piece_Cafe_Tokyo.jpg', 'Shibuya, Tokyo', 110, 4, 4, NULL, '10:00-20:00'),
(18, 'Harry Potter Studio', 'Movie studio tour', 'https://upload.wikimedia.org/wikipedia/commons/b/b0/Harry_Potter_Studio_Tour.jpg', 'Watford, UK', 260, 5, 2, NULL, '08:30-22:00'),
(19, 'La Boqueria Market', 'Colorful food market', 'https://upload.wikimedia.org/wikipedia/commons/1/15/La_Boqueria.jpg', 'Barcelona, Spain', 143, 4, 3, NULL, '08:00-20:30'),
(20, 'Rome Colosseum', 'Ancient Roman amphitheater', 'https://upload.wikimedia.org/wikipedia/commons/d/d4/Colosseo_2020.jpg', 'Piazza del Colosseo, Rome', 330, 5, 2, NULL, '08:30-19:00');

INSERT INTO trip_schedule (user_id, destination_id, title, created_at, is_shared, start_date, end_date)
VALUES
(1, 1, '서울 1박 2일 여행', NOW(), TRUE, '2025-06-01', '2025-06-02'),
(2, 2, '부산 바다 여행', NOW(), FALSE, '2025-06-03', '2025-06-05'),
(3, 3, '제주도 렌터카 투어', NOW(), TRUE, '2025-06-10', '2025-06-13'),
(4, 4, '도쿄 디즈니랜드 일정', NOW(), TRUE, '2025-06-20', '2025-06-22'),
(5, 5, '오사카 먹방 여행', NOW(), FALSE, '2025-07-01', '2025-07-03'),
(6, 6, '교토 전통 체험', NOW(), TRUE, '2025-07-05', '2025-07-07'),
(7, 7, '뉴욕 시티 투어', NOW(), TRUE, '2025-07-10', '2025-07-14'),
(8, 8, 'LA 자유여행', NOW(), FALSE, '2025-07-15', '2025-07-20'),
(9, 9, '파리 루브르 중심 일정', NOW(), TRUE, '2025-08-01', '2025-08-04'),
(10, 10, '런던 핵심 탐방', NOW(), FALSE, '2025-08-10', '2025-08-12'),
(11, 11, '방콕 시장 투어', NOW(), TRUE, '2025-08-15', '2025-08-17'),
(12, 12, '싱가포르 마리나베이 일정', NOW(), TRUE, '2025-08-20', '2025-08-22'),
(13, 13, '시드니 관광', NOW(), FALSE, '2025-08-25', '2025-08-28'),
(14, 14, '토론토 문화체험', NOW(), TRUE, '2025-09-01', '2025-09-03'),
(15, 15, '로마 유적 탐방', NOW(), FALSE, '2025-09-05', '2025-09-08'),
(16, 16, '베를린 힙스터 투어', NOW(), TRUE, '2025-09-10', '2025-09-12'),
(17, 17, '바르셀로나 길거리 투어', NOW(), TRUE, '2025-09-15', '2025-09-17'),
(18, 18, '하노이 역사 투어', NOW(), FALSE, '2025-09-20', '2025-09-22'),
(19, 19, '이스탄불 모스크 방문', NOW(), TRUE, '2025-09-25', '2025-09-27'),
(20, 20, '두바이 사막 사파리', NOW(), FALSE, '2025-09-29', '2025-10-01');

INSERT INTO trip_schedule_items (spot_id, trip_schedule_id, day, sequence)
VALUES
(1, 1, 1, 1), (2, 1, 1, 2),
(3, 2, 1, 1), (15, 2, 2, 1),
(4, 3, 1, 1), (5, 3, 2, 1),
(6, 4, 1, 1), (7, 4, 2, 1),
(8, 5, 1, 1), (9, 5, 2, 1),
(10, 6, 1, 1), (11, 6, 2, 1),
(12, 7, 1, 1), (13, 7, 2, 1),
(14, 8, 1, 1), (3, 8, 2, 1),
(2, 9, 1, 1), (6, 9, 2, 1),
(17, 10, 1, 1), (5, 10, 2, 1),
(19, 11, 1, 1), (20, 11, 2, 1),
(18, 12, 1, 1), (1, 12, 2, 1),
(7, 13, 1, 1), (8, 13, 2, 1),
(9, 14, 1, 1), (10, 14, 2, 1),
(11, 15, 1, 1), (12, 15, 2, 1),
(13, 16, 1, 1), (14, 16, 2, 1),
(15, 17, 1, 1), (16, 17, 2, 1),
(17, 18, 1, 1), (18, 18, 2, 1),
(19, 19, 1, 1), (20, 19, 2, 1),
(1, 20, 1, 1), (2, 20, 2, 1);

INSERT INTO bookmarks (user_id, spot_id)
VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5),
(6, 6), (7, 7), (8, 8), (9, 9), (10, 10),
(11, 11), (12, 12), (13, 13), (14, 14), (15, 15),
(16, 16), (17, 17), (18, 18), (19, 19), (20, 20);

INSERT INTO boards (uploader_id, title, content, category, created_at)
VALUES
(1, '서울 여행 후기', '정말 재밌었어요!', 'free', NOW()),
(2, '부산 바다 최고', '회 먹고 해운대 다녀옴', 'free', NOW()),
(3, '제주도 맛집 추천', '흑돼지 진짜 맛있음', 'idea', NOW()),
(4, '도쿄 디즈니랜드 리뷰', '환상적!', 'free', NOW()),
(5, '오사카 자유여행 꿀팁', '도톤보리 강추', 'idea', NOW()),
(6, '파리 예술 여행기', '루브르 감동', 'free', NOW()),
(7, '런던 버스 투어', '빅벤 최고', 'idea', NOW()),
(8, '싱가포르 클린 시티', '정말 깨끗함', 'free', NOW()),
(9, '로마에서 만난 감동', '콜로세움 웅장해요', 'idea', NOW()),
(10, '하노이 먹방 여행', '쌀국수 먹고 또 먹음', 'free', NOW()),
(11, '이스탄불 모스크', '아야소피아 감동', 'idea', NOW()),
(12, '두바이 초고층', '버즈 칼리파 짱', 'free', NOW()),
(13, '시드니 오페라 하우스 후기', '장관이었어요', 'idea', NOW()),
(14, '방콕 시장 투어', '길거리 음식 미쳤다', 'free', NOW()),
(15, '토론토 문화 체험', 'CN타워 좋음', 'idea', NOW()),
(16, '바르셀로나 미술관', '피카소 굿', 'free', NOW()),
(17, '뉴욕 시티 팁', '메트로 카드 필수', 'idea', NOW()),
(18, 'LA 드라이브', '헐리우드사인 봄', 'free', NOW()),
(19, '교토 전통의 멋', '기모노 체험 추천', 'idea', NOW()),
(20, '베를린 클럽 후기', '독일 클럽 갓', 'free', NOW());

INSERT INTO comments (board_id, content, uploader_id, created_at)
VALUES
(1, '좋은 정보 감사합니다!', 2, NOW()),
(1, '여기 진짜 가보고 싶어요', 3, NOW()),
(2, '부산은 언제나 최고죠', 4, NOW()),
(3, '흑돼지 맛집 정보 좀 주세요', 5, NOW()),
(4, '디즈니랜드는 사랑입니다', 6, NOW()),
(5, '도톤보리 진짜 최고!', 7, NOW()),
(6, '예술 감성 느껴지네요', 8, NOW()),
(7, '런던은 우산 필수인가요?', 9, NOW()),
(8, '싱가포르 저도 가봤어요', 10, NOW()),
(9, '로마 일정 참고할게요!', 11, NOW()),
(10, '쌀국수 먹고 싶다', 12, NOW()),
(11, '모스크 웅장하죠', 13, NOW()),
(12, '두바이 여행 궁금했는데 감사합니다', 14, NOW()),
(13, '오페라 하우스 멋져요', 15, NOW()),
(14, '방콕 시장 진짜 맛집 천국', 16, NOW()),
(15, 'CN타워 올라가보고 싶네요', 17, NOW()),
(16, '피카소 전시 재밌어요', 18, NOW()),
(17, '뉴욕 메트로 꿀팁 좋네요', 19, NOW()),
(18, 'LA도 꼭 가보고 싶어요', 20, NOW()),
(19, '기모노 체험 신기하네요', 1, NOW());

INSERT INTO board_likes (user_id, board_id)
VALUES
(1, 1), (2, 2), (3, 3), (4, 4), (5, 5),
(6, 6), (7, 7), (8, 8), (9, 9), (10, 10),
(11, 11), (12, 12), (13, 13), (14, 14), (15, 15),
(16, 16), (17, 17), (18, 18), (19, 19), (20, 20);

INSERT INTO board_dislikes (user_id, board_id)
VALUES
(2, 1), (4, 3), (6, 5), (8, 7), (10, 9),
(12, 11), (14, 13), (16, 15), (18, 17), (20, 19);

INSERT INTO board_reports (user_id, board_id)
VALUES
(3, 2), (5, 4), (7, 6), (9, 8), (11, 10),
(13, 12), (15, 14), (17, 16), (19, 18), (1, 20);

INSERT INTO comment_reports (user_id, comment_id)
VALUES
(2, 1), (4, 2), (6, 3), (8, 4), (10, 5),
(12, 6), (14, 7), (16, 8), (18, 9), (20, 10);

INSERT INTO board_images (board_id, origin_name, uuid, created_at, deleted_at, is_deleted)
VALUES
(1, 'image1.jpg', 'uuid-1', NOW(), NULL, FALSE),
(2, 'image2.jpg', 'uuid-2', NOW(), NULL, FALSE),
(3, 'image3.jpg', 'uuid-3', NOW(), NULL, FALSE),
(4, 'image4.jpg', 'uuid-4', NOW(), NULL, FALSE),
(5, 'image5.jpg', 'uuid-5', NOW(), NULL, FALSE),
(6, 'image6.jpg', 'uuid-6', NOW(), NULL, FALSE),
(7, 'image7.jpg', 'uuid-7', NOW(), NULL, FALSE),
(8, 'image8.jpg', 'uuid-8', NOW(), NULL, FALSE),
(9, 'image9.jpg', 'uuid-9', NOW(), NULL, FALSE),
(10, 'image10.jpg', 'uuid-10', NOW(), NULL, FALSE);

INSERT INTO imgs (user_id, image_path, category)
VALUES
(1, 'profile1.jpg', 'profile'),
(2, 'profile2.jpg', 'profile'),
(3, 'spot1.jpg', 'spot'),
(4, 'spot2.jpg', 'spot'),
(5, 'board1.jpg', 'board'),
(6, 'board2.jpg', 'board'),
(7, 'profile3.jpg', 'profile'),
(8, 'spot3.jpg', 'spot'),
(9, 'board3.jpg', 'board'),
(10, 'spot4.jpg', 'spot'),
(11, 'profile4.jpg', 'profile'),
(12, 'board4.jpg', 'board'),
(13, 'spot5.jpg', 'spot'),
(14, 'profile5.jpg', 'profile'),
(15, 'board5.jpg', 'board'),
(16, 'profile6.jpg', 'profile'),
(17, 'spot6.jpg', 'spot'),
(18, 'profile7.jpg', 'profile'),
(19, 'spot7.jpg', 'spot'),
(20, 'board6.jpg', 'board');
