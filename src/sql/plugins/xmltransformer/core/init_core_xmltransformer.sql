-- liquibase formatted sql
-- changeset xmltransformer:init_core_xmltransformer.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(1) from core_admin_right where id_right = 'CORE_STYLES_MANAGEMENT'

--
-- Init  table core_admin_right
--
INSERT INTO core_admin_right VALUES ('CORE_STYLES_MANAGEMENT', 'xmltransformer.adminFeature.styles_management.name', 0, 'jsp/admin/style/ManageStyles.jsp', 'xmltransformer.adminFeature.styles_management.description', 1, '', 'STYLE', 'ti ti-brush', NULL, 3, 0);
INSERT INTO core_admin_right VALUES ('CORE_STYLESHEET_MANAGEMENT', 'xmltransformer.adminFeature.stylesheet_management.name', 0, 'jsp/admin/style/ManageStyleSheets.jsp', 'xmltransformer.adminFeature.stylesheet_management.description', 1, '', 'STYLE', 'ti ti-file-code', NULL, 2, 0);

INSERT INTO core_user_right VALUES ('CORE_STYLES_MANAGEMENT',1);
INSERT INTO core_user_right VALUES ('CORE_STYLESHEET_MANAGEMENT',1);
