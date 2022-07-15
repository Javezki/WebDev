DROP TABLE IF EXISTS sqlapi_user;
CREATE TABLE sqlapi_user(
    id INT PRIMARY KEY AUTO_INCREMENT  COMMENT '用户id',
    username VARCHAR(255) NULL DEFAULT NULL  COMMENT '用户名',
    password VARCHAR(255) NULL DEFAULT NULL  COMMENT '用户登录密码',
    enabled  BOOLEAN      NOT NULL DEFAULT TRUE COMMENT '用户激活（1：激活，0：禁用）',
    roles    VARCHAR(255) DEFAULT '["ROLE_USER"]' FORMAT JSON COMMENT '用户角色',
    create_time TIMESTAMP(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    update_time TIMESTAMP(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间'
 );
INSERT INTO sqlapi_user (username, roles, password, enabled)
VALUES ('admin@lftech.com', '["ROLE_ADMIN","ROLE_USER"]', 'H2Pass@2022', TRUE);
INSERT INTO sqlapi_user (username, roles, password, enabled)
VALUES ('apiuser', '["ROLE_USER"]', '0b7662cd-7efc-470f-814f-b52e1d4e9807', TRUE);
DROP TABLE IF EXISTS sqlapi_connection;
CREATE TABLE sqlapi_connection(
                            id INT PRIMARY KEY AUTO_INCREMENT  COMMENT '连接id',
                            driver_class_name VARCHAR(255) NULL DEFAULT NULL  COMMENT '启动类名',
                            url VARCHAR(255) NULL DEFAULT NULL  COMMENT 'url',
                            username VARCHAR(255) NULL DEFAULT NULL  COMMENT '登录用户',
                            password VARCHAR(255) NULL DEFAULT NULL  COMMENT '登录密码',
                            create_time TIMESTAMP(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                            update_time TIMESTAMP(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间'
);
INSERT INTO sqlapi_connection (driver_class_name, url, username, password)
VALUES ('org.h2.Driver', 'jdbc:h2:mem:sqlapi;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE', 'sa', 'H2Pass@2022');
INSERT INTO sqlapi_connection (driver_class_name, url, username, password)
VALUES ('com.mysql.cj.jdbc.Driver', 'jdbc:mysql://20.60.251.194:13306/nds?useUnicode=true&characterEncoding=UTF-8&useSSL=false&autoReconnect=true&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&allowMultiQueries=true&serverTimezone=Asia/Shanghai', 'root', 'Hhh123456.');
INSERT INTO sqlapi_connection (driver_class_name, url, username, password)
VALUES ('org.postgresql.Driver', 'jdbc:postgresql://192.168.0.198:5432/gis', 'root', 'Yfzx@2022');
DROP TABLE IF EXISTS sqlapi_query;
CREATE TABLE sqlapi_query(
                                  id INT PRIMARY KEY AUTO_INCREMENT  COMMENT 'queryId',
                                  query_name VARCHAR(255) NULL DEFAULT NULL  COMMENT '查询名',
                                  sql VARCHAR(255) NULL DEFAULT NULL  COMMENT 'prepared statement sql',
                                  connection_id INT NULL DEFAULT NULL  COMMENT '登录用户',
                                  create_time TIMESTAMP(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                  update_time TIMESTAMP(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间'
);
-- 127.0.0.1:8080/h2query/sql?ids=1&ids=2
INSERT INTO sqlapi_query (query_name, sql, connection_id)
VALUES ('h2query', 'select * from sqlapi_query where id in (:ids)', '1');
-- 127.0.0.1:8080/mysqlbase_line/sql?ids=1&ids=2&orientation=2&analyzeTimeBetween=2022-06-20 10:15:05&analyzeTimeAnd=2022-06-20 11:15:05
INSERT INTO sqlapi_query (query_name, sql, connection_id)
VALUES ('mysqlbase_line', 'SELECT * FROM leon.monitor_photo_video where orientation = :orientation and analyzeTime between :analyzeTimeBetween and :analyzeTimeAnd', '2');
INSERT INTO sqlapi_query (query_name, sql, connection_id)
VALUES ('postgistower', 'SELECT * FROM 双林杆塔 LIMIT 10', '3');