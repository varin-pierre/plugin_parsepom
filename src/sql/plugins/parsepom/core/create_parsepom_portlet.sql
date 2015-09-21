  
--
-- Structure for table parsepom_portlet
--
DROP TABLE IF EXISTS parsepom_portlet;
CREATE TABLE parsepom_portlet (
  id_portlet int default '0' NOT NULL,
  parsepom_feed_id varchar(100) default NULL,
  PRIMARY KEY  (id_portlet)
);
