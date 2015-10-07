
--
-- Structure for table parsepom_site
--

DROP TABLE IF EXISTS parsepom_site;
CREATE TABLE parsepom_site (
id_site int(6) NOT NULL,
name varchar(255) NOT NULL default '',
version varchar(50) NOT NULL default '',
id_plugins varchar(20000) NOT NULL default '',
last_update varchar(50) NOT NULL default '',
PRIMARY KEY (id_site)
);

--
-- Structure for table parsepom_dependency
--

DROP TABLE IF EXISTS parsepom_dependency;
CREATE TABLE parsepom_dependency (
id_dependency int(6) NOT NULL,
group_id varchar(50) NOT NULL default '',
artifact_id varchar(50) NOT NULL default '',
version varchar(50) NOT NULL default '',
type varchar(50) NOT NULL default '',
site_id int(11) NOT NULL default '0',
FOREIGN KEY (site_id) REFERENCES parsepom_site(id_site),
PRIMARY KEY (id_dependency)
);
