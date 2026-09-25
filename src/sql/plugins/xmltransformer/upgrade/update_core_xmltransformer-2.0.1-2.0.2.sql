-- liquibase formatted sql
-- changeset xmltransformer:update_core_xmltransformer-2.0.1-2.0.2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- comment The rights take the plugin's own ids: the core 8.0.1-8.0.2 upgrade removes the CORE_ ones. Renamed in place to keep the users who hold them.
UPDATE core_admin_right SET id_right = 'XMLTRANSFORMER_STYLES_MANAGEMENT', name = 'xmltransformer.adminFeature.styles_management.name', description = 'xmltransformer.adminFeature.styles_management.description', plugin_name = 'xmltransformer', icon_url = 'ti ti-brush' WHERE id_right = 'CORE_STYLES_MANAGEMENT';
UPDATE core_user_right SET id_right = 'XMLTRANSFORMER_STYLES_MANAGEMENT' WHERE id_right = 'CORE_STYLES_MANAGEMENT';
UPDATE core_admin_right SET id_right = 'XMLTRANSFORMER_STYLESHEET_MANAGEMENT', name = 'xmltransformer.adminFeature.stylesheet_management.name', description = 'xmltransformer.adminFeature.stylesheet_management.description', plugin_name = 'xmltransformer', icon_url = 'ti ti-file-code' WHERE id_right = 'CORE_STYLESHEET_MANAGEMENT';
UPDATE core_user_right SET id_right = 'XMLTRANSFORMER_STYLESHEET_MANAGEMENT' WHERE id_right = 'CORE_STYLESHEET_MANAGEMENT';

-- changeset xmltransformer:update_core_xmltransformer-2.0.1-2.0.2-rev1.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM core_admin_right WHERE id_right = 'XMLTRANSFORMER_STYLES_MANAGEMENT'
-- comment Restores the right on a site that already lost the CORE_ one.
INSERT INTO core_admin_right ( id_right, name, level_right, admin_url, description, is_updatable, plugin_name, id_feature_group, icon_url, documentation_url, id_order, is_external_feature ) VALUES ('XMLTRANSFORMER_STYLES_MANAGEMENT', 'xmltransformer.adminFeature.styles_management.name', 0, 'jsp/admin/style/ManageStyles.jsp', 'xmltransformer.adminFeature.styles_management.description', 1, 'xmltransformer', 'STYLE', 'ti ti-brush', NULL, 3, 0);
INSERT INTO core_user_right ( id_right, id_user ) VALUES ('XMLTRANSFORMER_STYLES_MANAGEMENT',1);

-- changeset xmltransformer:update_core_xmltransformer-2.0.1-2.0.2-rev2.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM core_admin_right WHERE id_right = 'XMLTRANSFORMER_STYLESHEET_MANAGEMENT'
-- comment Restores the right on a site that already lost the CORE_ one.
INSERT INTO core_admin_right ( id_right, name, level_right, admin_url, description, is_updatable, plugin_name, id_feature_group, icon_url, documentation_url, id_order, is_external_feature ) VALUES ('XMLTRANSFORMER_STYLESHEET_MANAGEMENT', 'xmltransformer.adminFeature.stylesheet_management.name', 0, 'jsp/admin/style/ManageStyleSheets.jsp', 'xmltransformer.adminFeature.stylesheet_management.description', 1, 'xmltransformer', 'STYLE', 'ti ti-file-code', NULL, 2, 0);
INSERT INTO core_user_right ( id_right, id_user ) VALUES ('XMLTRANSFORMER_STYLESHEET_MANAGEMENT',1);
