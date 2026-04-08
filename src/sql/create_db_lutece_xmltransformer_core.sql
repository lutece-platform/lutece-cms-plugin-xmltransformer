-- liquibase formatted sql
-- changeset xmltransformer:create_db_xmltransformer.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- precondition-sql-check expectedResult:0 SELECT COUNT(1) from INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA=database() AND TABLE_NAME='core_style';

--
-- Table structure for table core_style
--
DROP TABLE IF EXISTS core_style;
CREATE TABLE core_style (
	id_style int default 0 NOT NULL,
	description_style varchar(100) default '' NOT NULL,
	id_portlet_type varchar(50) default NULL,
	id_portal_component int default 0 NOT NULL,
	PRIMARY KEY (id_style)
);

CREATE INDEX index_style ON core_style (id_portlet_type);

--
-- Table structure for table core_style_mode_stylesheet
--
DROP TABLE IF EXISTS core_style_mode_stylesheet;
CREATE TABLE core_style_mode_stylesheet (
	id_style int default 0 NOT NULL,
	id_mode int default 0 NOT NULL,
	id_stylesheet int default 0 NOT NULL,
	PRIMARY KEY (id_style,id_mode,id_stylesheet)
);

CREATE INDEX index_style_mode_stylesheet ON core_style_mode_stylesheet (id_stylesheet,id_mode);

--
-- Table structure for table core_stylesheet
--
DROP TABLE IF EXISTS core_stylesheet;
CREATE TABLE core_stylesheet (
	id_stylesheet int AUTO_INCREMENT NOT NULL,
	description varchar(255),
	file_name varchar(255),
	source long varbinary,
	PRIMARY KEY (id_stylesheet)
);
