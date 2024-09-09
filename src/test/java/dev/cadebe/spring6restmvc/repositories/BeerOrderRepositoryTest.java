package dev.cadebe.spring6restmvc.repositories;

import dev.cadebe.spring6restmvc.bootstrap.BootstrapData;
import dev.cadebe.spring6restmvc.data.BeerOrderEntity;
import dev.cadebe.spring6restmvc.data.BeerOrderShipmentEntity;
import dev.cadebe.spring6restmvc.data.CustomerEntity;
import dev.cadebe.spring6restmvc.services.BeerCsvServiceImpl;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerOrderRepositoryTest {

    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private CustomerEntity testCustomer;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.findAll().getFirst();
    }

    @Test
    void shouldAddNewBeerOrder() {
        val customerRef = "Some test order";
        val trackingNumber = "TR-12345";

        BeerOrderEntity beerOrder = BeerOrderEntity.builder()
                .customerRef(customerRef)
                .customer(testCustomer)
                .beerOrderShipment(BeerOrderShipmentEntity.builder()
                        .trackingNumber(trackingNumber)
                        .build())
                .build();

        val savedBeerOrder = beerOrderRepository.save(beerOrder);

        assertThat(savedBeerOrder.getCustomerRef()).isEqualTo(customerRef);
        assertThat(savedBeerOrder.getCustomer().getBeerOrders())
                .hasSize(1)
                .extracting(BeerOrderEntity::getCustomerRef)
                .containsExactly(customerRef);

        assertThat(savedBeerOrder).extracting(BeerOrderEntity::getCustomer).isNotNull();
        assertThat(savedBeerOrder.getCustomer().getBeerOrders())
                .hasSize(1)
                .extracting(BeerOrderEntity::getCustomer)
                .extracting(CustomerEntity::getName)
                .containsExactly("Customer 1");

        assertThat(savedBeerOrder).extracting(BeerOrderEntity::getBeerOrderShipment).isNotNull();
        assertThat(savedBeerOrder.getBeerOrderShipment()).extracting(BeerOrderShipmentEntity::getId).isNotNull();
        assertThat(savedBeerOrder.getBeerOrderShipment())
                .extracting(BeerOrderShipmentEntity::getTrackingNumber)
                .isEqualTo(trackingNumber);
    }
}