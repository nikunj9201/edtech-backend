-- Fix Test Series independence from Course.
-- If the old test_series table still contains a course_id FK, remove it.
-- Run these commands in order after confirming the constraint name.

-- 1) Show current constraint and FK name:
-- SHOW CREATE TABLE test_series;

-- 2) Drop the foreign key that references course_id (replace with the exact constraint name returned above):
-- ALTER TABLE test_series DROP FOREIGN KEY `FKc3i81vmbo5jt5a4jygh64i0o9`;

-- 3) Drop the course_id column if it exists:
-- ALTER TABLE test_series DROP COLUMN `course_id`;

-- If your MySQL version does not support DROP COLUMN IF EXISTS, use the preceding explicit command above.

-- After this, the TestSeries table should no longer contain a course_id column.
-- Test Series is independent of Course and only has TestPaper children.
