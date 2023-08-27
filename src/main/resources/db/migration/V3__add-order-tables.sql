drop table if exists restdb.beer_order;
drop table if exists restdb.beer_order_line;

create table restdb.beer_order
(
    id           varchar(36) not null unique,
    customer_id  varchar(36) not null unique,
    created_date datetime(6) default null,
    updated_date datetime(6) default null,
    customer_ref varchar(255),
    version      bigint      default null,
    primary key (id),
    constraint foreign key (customer_id)
        references restdb.customer (id)
) engine = InnoDB;

create table restdb.beer_order_line
(
    id                 varchar(36) not null unique,
    beer_id            varchar(36) not null unique,
    beer_order_id      varchar(36) not null unique,
    created_date       datetime(6) default null,
    updated_date       datetime(6) default null,
    order_quantity     integer     default null,
    quantity_allocated integer     default null,
    version            bigint      default null,
    primary key (id),
    constraint foreign key (beer_id)
        references restdb.beer (id),
    constraint foreign key (beer_order_id)
        references restdb.beer_order (id)
) engine = InnoDB;
