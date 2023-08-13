package dev.cadebe.spring6restmvc.controller;

import dev.cadebe.spring6restmvc.model.BeerDto;
import dev.cadebe.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(BeerController.BASE_URL)
@RequiredArgsConstructor
public class BeerController {

    public static final String BASE_URL = "/api/v1/beer";

    private final BeerService beerService;

    @GetMapping
    public List<BeerDto> getBeers() {
        return beerService.listBeers();
    }

    @GetMapping("{beerId}")
    public BeerDto getBeerById(@PathVariable("beerId") UUID beerId) {
        return beerService.getBeerbyId(beerId).orElseThrow(NotFoundException::new);
    }

    @PostMapping
    public ResponseEntity<String> saveNewBeer(@Validated @RequestBody BeerDto beer) {
        val savedBeer = beerService.saveNewBeer(beer);

        val headers = new HttpHeaders();
        headers.add("Location", BeerController.BASE_URL + "/" + savedBeer.getId());

        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("{beerId}")
    public ResponseEntity<String> updateById(@PathVariable UUID beerId, @RequestBody BeerDto beer) {
        val updated = beerService.updateBeerById(beerId, beer);

        if (updated.isEmpty()) {
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("{beerId}")
    public ResponseEntity<String> patchBeerById(@PathVariable("beerId") UUID beerId, @RequestBody BeerDto beer) {
        beerService.patchBeerById(beerId, beer);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("{beerId}")
    public ResponseEntity<String> deleteBeerById(@PathVariable UUID beerId) {
        if (!beerService.deleteBeerById(beerId)) {
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
