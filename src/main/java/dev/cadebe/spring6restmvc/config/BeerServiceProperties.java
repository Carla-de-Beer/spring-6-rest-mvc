package dev.cadebe.spring6restmvc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "beer.service")
@Getter
@Setter
public class BeerServiceProperties {

    private int defaultPageSize = 25;

    private int pageLimit = 1000;
}
