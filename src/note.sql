//树形子节点查询
CREATE
DEFINER=`zxb2`@`%` FUNCTION `getChildLst`(rootId INT) RETURNS longtext CHARSET utf8mb4 COLLATE utf8mb4_general_ci
BEGIN
       DECLARE
sTemp LONGTEXT;
       DECLARE
sTempChd LONGTEXT;

       SET
sTemp = '0';
       SET
sTempChd =cast(rootId as CHAR);

       WHILE
sTempChd is not null DO
         SET sTemp = concat(sTemp,',',sTempChd);
SELECT group_concat(id)
INTO sTempChd
FROM t_template_dimension_info
where FIND_IN_SET(dimension_pid, sTempChd) > 0;
END WHILE;
RETURN sTemp;
END