package dev.cadebe.spring6restmvc.model;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeerCsv {

    @CsvBindByName
    private String row;

    @CsvBindByName(column = "count.x")
    private Integer countX;

    @CsvBindByName
    private String abv;

    @CsvBindByName
    private String ibu;

    @CsvBindByName
    private String id;

    @CsvBindByName
    private String beer;

    @CsvBindByName
    private String style;

    @CsvBindByName(column = "brewery_id")
    private Integer breweryId;

    @CsvBindByName
    private Float ounces;

    @CsvBindByName
    private String style2;

    @CsvBindByName(column = "count.y")
    private Integer countY;

    @CsvBindByName
    private String brewery;

    @CsvBindByName
    private String city;

    @CsvBindByName
    private String state;

    @CsvBindByName
    private String label;
}
