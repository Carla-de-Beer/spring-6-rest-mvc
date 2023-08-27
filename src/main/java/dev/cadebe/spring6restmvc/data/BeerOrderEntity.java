package dev.cadebe.spring6restmvc.data;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@Table(name = "beer_order")
public class BeerOrderEntity {

    public BeerOrderEntity(UUID id, Long version, String customerRef, LocalDateTime createdDate, LocalDateTime updatedDate,
                           CustomerEntity customer, Set<BeerOrderLineEntity> beerOrderLines, BeerOrderShipmentEntity beerOrderShipment) {
        this.id = id;
        this.version = version;
        this.customerRef = customerRef;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        setCustomer(customer);
        this.beerOrderLines = beerOrderLines;
        setBeerOrderShipment(beerOrderShipment);
    }

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36, columnDefinition = "varchar(36)", updatable = false, nullable = false)
    private UUID id;

    @Version
    private Long version;

    @Size(max = 255)
    private String customerRef;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @ManyToOne
    private CustomerEntity customer;

    @Builder.Default
    @OneToMany(mappedBy = "beerOrder")
    private Set<BeerOrderLineEntity> beerOrderLines = new HashSet<>();

    @OneToOne(cascade = CascadeType.PERSIST)
    private BeerOrderShipmentEntity beerOrderShipment;

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
        customer.getBeerOrders().add(this);
    }

    private void setBeerOrderShipment(BeerOrderShipmentEntity beerOrderShipment) {
        this.beerOrderShipment = beerOrderShipment;
        beerOrderShipment.setBeerOrder(this);
    }

    public boolean isNew() {
        return id == null;
    }
}
