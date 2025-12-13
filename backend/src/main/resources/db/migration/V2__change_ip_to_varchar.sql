-- Change ip columns from inet to varchar
ALTER TABLE sessions ALTER COLUMN ip TYPE VARCHAR(45);
ALTER TABLE audit_events ALTER COLUMN ip TYPE VARCHAR(45);
