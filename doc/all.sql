drop table if exists `test`;

create table `test` (
       `id` bigint not null comment  'id',
       `name` varchar(50) comment '名称',
       `password` varchar(50) comment '密码',
       primary key (`id`)
)engine=innodb default  charset=utf8mb4 comment='测试';#记得用innodb引擎,Innodb是mysql的引擎之一

insert into `test` (id,name,password) values(1,'测试','123');

drop table if exists `demo`;

create table `demo`(
    `id` bigint not null comment 'id',
    `name` varchar(50) comment '名称',
    primary key (`id`)
)engine=innodb default  charset =utf8mb4 comment '测试';

insert into `demo`(id,name) values(1,'测试');

#电子书表
drop table  if exists `ebook`;
create table `ebook`(
    `id` bigint not null comment 'id',
    `name` varchar(50) comment  '名称',
    `category1_id` bigint comment '分类1',
    `category2_id` bigint comment '分类2',
    `description` varchar(200) comment '描述',
    `cover` varchar(200) comment '封面',
    `doc_count` int comment '文档数',
    `view_count` int comment '阅读数',
    `vote_count` int comment '点赞数',
    primary key (`id`)
)engine=innodb default  charset=utf8mb4 comment '电子书';

insert into `ebook` (id,name,description) value (1,'Spring Boot 入门教程','高成本入门 Java 开发,企业级级应用开发最佳首选框架');

insert into `ebook` (id,name,description) value (2,'Vue 入门教程','高成本入门 Vue 开发,企业级级应用开发最佳首选框架');
insert into `ebook` (id,name,description) value (3,'Apex入门教程','三天上猎杀');

insert into `ebook` (id,name,description) value (4,'CSGO低端局无脑套路','爷们要战斗~爷们要战斗~');


#分类
drop table if exists `category`;
create table `category`(
                           `id` bigint not null comment  'id',
                           `parent` bigint not null  default  0 comment '父id',
                           `name` varchar(50) not null comment '名称',
                           `sort` int comment '顺序',
                           primary key (`id`)
)engine=innodb default charset =utf8mb4 comment='分类';

insert into `category` (id, parent, name, sort) values(100,000,'前端开发',100);
insert into `category` (id, parent, name, sort) values(101,100,'vue',101);
insert into `category` (id, parent, name, sort) values(102,100,'csss',102);
insert into `category` (id, parent, name, sort) values(200,000,'Html',200);
insert into `category` (id, parent, name, sort) values(201,200,'java',201);
insert into `category` (id, parent, name, sort) values(202,200,'python',202);
insert into `category` (id, parent, name, sort) values(300,000,'数据库',300);
insert into `category` (id, parent, name, sort) values(301,300,'mysql',301);
insert into `category` (id, parent, name, sort) values(302,300,'服务器',302);
insert into `category` (id, parent, name, sort) values(400,000,'开发工具',400);
insert into `category` (id, parent, name, sort) values(401,400,'其他',401);
insert into `category` (id, parent, name, sort) values(402,400,'集成开发',402);


#文档表
drop table if exists `doc`;
create table `doc`(
                      `id` bigint not null comment 'id',
                      `ebook_id` bigint not null default  0 comment '电子书id',
                      `parent` bigint not null default 0 comment '父id',
                      `name` varchar(50) not null comment '名称',
                      `sort` int comment '顺序',
                      `view_count` int default 0 comment '阅读数',
                      `vote_count` int default 0 comment '点赞数',
                      primary key (`id`)
)engine=innodb default charset =utf8mb4 comment='文档';

insert into `doc` (id,ebook_id,parent,name,sort,view_count,vote_count) values (1,1,0,'文档1',1,0,0);
insert into `doc` (id,ebook_id,parent,name,sort,view_count,vote_count) values (2,1,1,'文档1.1',1,0,0);
insert into `doc` (id,ebook_id,parent,name,sort,view_count,vote_count) values (3,1,0,'文档2',1,0,0);
insert into `doc` (id,ebook_id,parent,name,sort,view_count,vote_count) values (4,1,3,'文档2.1',1,0,0);


#文档内容表
drop table if exists `content`;
create table `content`(
                          `id` bigint not null comment '文档id',
                          `content` mediumtext not null comment '内容',
                          primary key (`id`)
)engine=innodb default charset =utf8mb4 comment='文档内容';


#用户表 unique key 唯一键 id不能重复 限制唯一
drop table if exists `user`;
create table `user`(
                       `id` bigint not null  comment 'ID',
                       `login_name` varchar(50) not null comment '登陆名',
                       `name` varchar(50) comment '昵称',
                       `password` char(32) not null comment '密码',
                       primary key (`id`),
                       unique key `login_name`(`login_name`)
)engine=innodb default charset =utf8mb4 comment='用户';
#初始化密码test加密初始化没运行
-- insert into `user` (id, login_name, name, password) values (1,'test','测试','e70e2222a9d67c4f2eae107533359aa4');
insert into `user` (id, login_name, name, password) values (1,'test','测试','test');

#安电子书分组统计文档数据
update ebook t1,(select ebook_id,
    count(1) doc_count,sum(view_count) view_count,
    sum(vote_count) vote_count
    from doc group by ebook_id) t2
set t1.doc_count = t2.doc_count, t1.view_count = t2.view_count,
    t1.vote_count = t2.vote_count
where t1.id = t2.ebook_id;

#电子书快照表
drop table if exists `ebook_snapshot`;
create table `ebook_snapshot` (
    `id` bigint auto_increment not null comment 'id',
    `ebook_id` bigint not null default  0 comment '电子书id',
    `date` date not null comment '快照日期',
    `view_count` int not null default 0 comment '阅读数',
    `vote_count` int not null default 0 comment '点赞数',
    `view_increase` int not null default 0 comment '阅读增长',
    `vote_increase` int not null default 0 comment '点赞增长',
    primary key (`id`),
    unique key `ebook_id_date_unique`(`ebook_id`, `date`)
)engine=innodb default charset =utf8mb4 comment='电子书快照表';

#
# insert into ebook_snapshot(ebook_id, `date`, view_count, vote_count, view_increase, vote_increase)
# select t1.id, curdate(),0,0,0,0
# from ebook t1
# where not exists(select 1
#                  from ebook_snapshot t2
#                  where t1.id = t2.ebook_id
#                    and t2.`date` = curdate());
#
# update ebook_snapshot t1,ebook t2
# set t1.view_count = t2.view_count,t1.vote_count = t2.vote_count
# where t1.`date` = curdate()
#   and t1.ebook_id = t2.id;
#
# select t1.ebook_id,view_count,vote_count from ebook_snapshot t1
# where t1.`date` = date_sub(curdate(),interval 1 day );
#
# update ebook_snapshot t1 left join (select ebook_id,view_count,vote_count from ebook_snapshot
#                                     where `date` = date_sub(curdate(),interval 1 day )) t2
#     on t1.ebook_id = t2.ebook_id
# set t1.view_increase = (t1.view_count - ifnull(t2.view_count,0)),
#     t1.vote_increase = (t1.vote_count - ifnull(t2.vote_count,0))
# where t1.`date` = curdate();
