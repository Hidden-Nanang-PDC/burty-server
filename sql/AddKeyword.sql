-- 새로운 키워드 추가
INSERT INTO community_categories (name) VALUES ('교육');

INSERT INTO community_keywords (word, category_id, mapped_category_id)
VALUES ('교육', 3, (SELECT id FROM community_categories WHERE name = '교육'));
-- category_id : (1=지역, 2=연령대, 3=직무)