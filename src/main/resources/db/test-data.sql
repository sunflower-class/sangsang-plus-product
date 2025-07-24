-- Test data for Product Service (PostgreSQL)
-- 이 스크립트는 테스트 목적으로만 사용하세요

-- 테스트 상품 데이터 삽입 (PostgreSQL 문법 사용)
INSERT INTO products (user_id, user_email, user_name, title, description, category, price, created_at, updated_at) VALUES
(1, 'user1@example.com', '사용자1', '맛있는 사과', '신선하고 달콤한 사과입니다.', '과일', 5000.00, CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days'),
(2, 'user2@example.com', '사용자2', '노트북 판매', '사용한지 1년된 노트북입니다.', '전자제품', 800000.00, CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
(1, 'user1@example.com', '사용자1', '운동화', '거의 새 것 같은 운동화입니다.', '의류', 120000.00, CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
(3, 'user3@example.com', '사용자3', '중고 책', '프로그래밍 관련 서적입니다.', '도서', 25000.00, CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
(2, 'user2@example.com', '사용자2', '커피 원두', '갓 볶은 원두 커피입니다.', '식품', 15000.00, CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day'),
(4, 'admin@example.com', '관리자', '테스트 상품', '관리자가 등록한 테스트 상품입니다.', '기타', 10000.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 테스트 상품 이미지 데이터 삽입
INSERT INTO product_images (product_id, url, alt_text, display_order, created_at) VALUES
(1, 'https://example.com/images/apple1.jpg', '사과 이미지 1', 1, CURRENT_TIMESTAMP),
(1, 'https://example.com/images/apple2.jpg', '사과 이미지 2', 2, CURRENT_TIMESTAMP),
(2, 'https://example.com/images/laptop1.jpg', '노트북 이미지', 1, CURRENT_TIMESTAMP),
(3, 'https://example.com/images/shoes1.jpg', '운동화 이미지', 1, CURRENT_TIMESTAMP),
(4, 'https://example.com/images/book1.jpg', '책 이미지', 1, CURRENT_TIMESTAMP),
(5, 'https://example.com/images/coffee1.jpg', '커피 원두 이미지', 1, CURRENT_TIMESTAMP),
(6, 'https://example.com/images/test1.jpg', '테스트 상품 이미지', 1, CURRENT_TIMESTAMP);