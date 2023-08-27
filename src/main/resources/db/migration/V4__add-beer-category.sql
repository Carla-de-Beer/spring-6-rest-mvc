SET FOREIGN_KEY_CHECKS = 0;

drop table if exists restdb.category;
drop table if exists restdb.beer_category;

SET FOREIGN_KEY_CHECKS = 1;

create table restdb.category
(
    id           varchar(36) not null primary key,
    version      bigint      default null,
    description  varchar(50) default null,
    created_date datetime(6) default null,
    updated_date datetime(6) default null

) engine = InnoDB;

create table restdb.beer_category
(
    beer_id     varchar(36) not null unique,
    category_id varchar(36) not null unique,
    primary key (beer_id, category_id),
    constraint foreign key pc_beer_id_fk (beer_id)
        references restdb.beer (id),
    constraint foreign key pc_category_id_fk (category_id)
        references restdb.category (id)
) engine = InnoDB;
