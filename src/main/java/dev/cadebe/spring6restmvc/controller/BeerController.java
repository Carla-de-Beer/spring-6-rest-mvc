package dev.cadebe.spring6restmvc.controller;

import dev.cadebe.spring6restmvc.model.BeerDto;
import dev.cadebe.spring6restmvc.model.BeerStyle;
import dev.cadebe.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(BeerController.BASE_URL)
@RequiredArgsConstructor
public class BeerController {

    public static final String BASE_URL = "/api/v1/beers";

    private final BeerService beerService;

    @GetMapping
    public ResponseEntity<List<BeerDto>> getBeers(@RequestParam(required = false) String beerName,
                                                  @RequestParam(required = false) BeerStyle beerStyle,
                                                  @RequestParam(required = false) Boolean showInventory,
                                                  @RequestParam(required = false) Integer pageNumber,
                                                  @RequestParam(required = false) Integer pageSize) {
        val page = beerService.listBeers(beerName, beerStyle, showInventory, pageNumber, pageSize);

        return ResponseEntity.ok().body(page.getContent());
    }

    @GetMapping("/{beerId}")
    public ResponseEntity<BeerDto> getBeerById(@PathVariable("beerId") UUID beerId) {
        val found = beerService.getBeerbyId(beerId).orElseThrow(NotFoundException::new);

        return ResponseEntity.ok().body(found);
    }

    @PostMapping
    public ResponseEntity<String> saveNewBeer(@Validated @RequestBody BeerDto beer) {
        val savedBeer = beerService.saveNewBeer(beer);

        val location = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{beerId}")
                .buildAndExpand(savedBeer.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{beerId}")
    public ResponseEntity<String> updateById(@PathVariable UUID beerId, @RequestBody BeerDto beer) {
        val updated = beerService.updateBeerById(beerId, beer);

        if (updated.isEmpty()) {
            throw new NotFoundException();
        }

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{beerId}")
    public ResponseEntity<String> patchBeerById(@PathVariable("beerId") UUID beerId, @RequestBody BeerDto beer) {
        val patched = beerService.patchBeerById(beerId, beer);

        if (patched.isEmpty()) {
            throw new NotFoundException();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{beerId}")
    public ResponseEntity<String> deleteBeerById(@PathVariable UUID beerId) {
        if (!beerService.deleteBeerById(beerId)) {
            throw new NotFoundException();
        }

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({ConversionFailedException.class})
    public ResponseEntity<String> handleConversionFailure(Exception e) {
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
