-- liquibase formatted sql
-- changeset xmltransformer:init_core_xmltransformer.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(1) from core_admin_right where id_right = 'XMLTRANSFORMER_STYLES_MANAGEMENT'

--
-- Init  table core_admin_right
--
INSERT INTO core_admin_right ( id_right, name, level_right, admin_url, description, is_updatable, plugin_name, id_feature_group, icon_url, documentation_url, id_order, is_external_feature ) VALUES ('XMLTRANSFORMER_STYLES_MANAGEMENT', 'xmltransformer.adminFeature.styles_management.name', 0, 'jsp/admin/style/ManageStyles.jsp', 'xmltransformer.adminFeature.styles_management.description', 1, 'xmltransformer', 'STYLE', 'ti ti-brush', NULL, 3, 0);
INSERT INTO core_admin_right ( id_right, name, level_right, admin_url, description, is_updatable, plugin_name, id_feature_group, icon_url, documentation_url, id_order, is_external_feature ) VALUES ('XMLTRANSFORMER_STYLESHEET_MANAGEMENT', 'xmltransformer.adminFeature.stylesheet_management.name', 0, 'jsp/admin/style/ManageStyleSheets.jsp', 'xmltransformer.adminFeature.stylesheet_management.description', 1, 'xmltransformer', 'STYLE', 'ti ti-file-code', NULL, 2, 0);

INSERT INTO core_user_right ( id_right, id_user ) VALUES ('XMLTRANSFORMER_STYLES_MANAGEMENT',1);
INSERT INTO core_user_right ( id_right, id_user ) VALUES ('XMLTRANSFORMER_STYLESHEET_MANAGEMENT',1);
