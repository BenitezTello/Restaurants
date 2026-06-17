-- ============================================================
-- V5: Permitir horas nulas en schedules
-- Un día marcado como "Cerrado" (is_closed = true) no tiene
-- hora de apertura ni de cierre, por lo que opening_time y
-- closing_time deben poder ser NULL.
-- ============================================================

ALTER TABLE schedules ALTER COLUMN opening_time DROP NOT NULL;
ALTER TABLE schedules ALTER COLUMN closing_time DROP NOT NULL;
