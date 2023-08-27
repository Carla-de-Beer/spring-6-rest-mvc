drop table if exists restdb.beer_order_shipment;

create table restdb.beer_order_shipment
(
    id              varchar(36) not null primary key,
    beer_order_id   varchar(36) not null unique,
    version         bigint,
    tracking_number varchar(50) default null,
    created_date    datetime(6) default null,
    updated_date    datetime(6) default null,
    constraint foreign key beer_order_shipment_pk (beer_order_id) references restdb.beer_order (id)

) engine = InnoDB;

alter table beer_order
    add column beer_order_shipment_id varchar(36);

alter table beer_order
    add constraint foreign key beer_order_shipment_fk (beer_order_shipment_id) references restdb.beer_order_shipment (id);