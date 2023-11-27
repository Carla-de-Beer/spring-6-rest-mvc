package dev.cadebe.spring6restmvc.controller;

import dev.cadebe.spring6restmvc.model.BeerStyle;
import org.springframework.core.convert.converter.Converter;

public class StringToBeerStyleConverter implements Converter<String, BeerStyle> {
    @Override
    public BeerStyle convert(String source) {
        return BeerStyle.valueOf(source.toUpperCase());
    }
}