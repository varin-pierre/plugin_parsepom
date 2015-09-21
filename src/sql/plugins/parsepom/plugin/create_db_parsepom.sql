
--
-- Structure for table parsepom_site
--

DROP TABLE IF EXISTS parsepom_site;
CREATE TABLE parsepom_site (
id_site int(6) NOT NULL,
name varchar(50) NOT NULL default '',
nbdependencies int(11) NOT NULL default '0',
PRIMARY KEY (id_site)
);

--
-- Structure for table parsepom_dependency
--

DROP TABLE IF EXISTS parsepom_dependency;
CREATE TABLE parsepom_dependency (
id_dependecy int(6) NOT NULL,
groupid varchar(50) NOT NULL default '',
artifactid varchar(50) NOT NULL default '',
version varchar(50) NOT NULL default '',
type varchar(50) NOT NULL default '',
sid int(11) NOT NULL default '0',
PRIMARY KEY (id_dependecy)
);
