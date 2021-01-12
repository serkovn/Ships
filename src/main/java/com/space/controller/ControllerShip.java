package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.optionally.ShipIf;
import com.space.repository.RepositoryShip;
import com.space.service.ServiceShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest")
public class ControllerShip {
    @Autowired
    private ServiceShip shipService;

    @GetMapping("/ships")
    public List<Ship> getAllShips(@RequestParam(value = "order", defaultValue = "ID") String order,
                                  @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
                                  @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize,
                                  @RequestParam(required = false, value = "name", defaultValue = "") String name,
                                  @RequestParam(required = false, value = "planet", defaultValue = "") String planet,
                                  @RequestParam(required = false, value = "shipType") ShipType shipType,
                                  @RequestParam(required = false, value = "after", defaultValue = "26192302747154") Long minProdDate,
                                  @RequestParam(required = false, value = "before", defaultValue = "33134715547154") Long maxProdDate,
                                  @RequestParam(required = false, value = "minCrewSize", defaultValue = "1") Integer minCrewSize,
                                  @RequestParam(required = false, value = "maxCrewSize", defaultValue = "9999") Integer maxCrewSize,
                                  @RequestParam(required = false, value = "minSpeed", defaultValue = "0.01") Double minSpeed,
                                  @RequestParam(required = false, value = "maxSpeed", defaultValue = "0.99") Double maxSpeed,
                                  @RequestParam(required = false, value = "minRating", defaultValue = "0.01") Double minRating,
                                  @RequestParam(required = false, value = "maxRating", defaultValue = "80") Double maxRating,
                                  @RequestParam(required = false, value = "isUsed") Boolean isUsed) {


        List<Ship> ships = shipService.getAll().stream().filter(ship -> ship.getName().contains(name)).
                filter(ship -> ship.getPlanet().contains(planet)).
                filter(ship -> (shipType == null || ship.getShipType().equals(shipType))).
                filter(ship -> ship.getProdDate().after(new Date(minProdDate))).
                filter(ship -> ship.getProdDate().before(new Date(maxProdDate))).
                filter(ship -> ship.getCrewSize() >= minCrewSize).
                filter(ship -> ship.getCrewSize() <= maxCrewSize).
                filter(ship -> ship.getSpeed() >= minSpeed).
                filter(ship -> ship.getSpeed() <= maxSpeed).
                filter(ship -> ship.getRating() >= minRating).
                filter(ship -> ship.getRating() <= maxRating).
                filter(ship -> (isUsed == null || ship.isUsed().equals(isUsed))).
                collect(Collectors.toList());

        Comparator<Ship> comparator = null;
        switch (ShipOrder.valueOf(order)) {
            case ID: {
                comparator = Comparator.comparing(Ship::getId);
                break;
            }
            case SPEED: {
                comparator = Comparator.comparing(Ship::getSpeed);
                break;
            }
            case DATE: {
                comparator = Comparator.comparing(Ship::getProdDate);
                break;
            }
            case RATING: {
                comparator = Comparator.comparing(Ship::getRating);
            }
        }

        Collections.sort(ships, comparator);
        ships = ships.stream().skip(pageNumber * pageSize).limit(pageSize).collect(Collectors.toList());
        return ships;
    }

    @GetMapping("/ships/count")
    public Integer getAllCount(@RequestParam(required = false, value = "name", defaultValue = "") String name,
                               @RequestParam(required = false, value = "planet", defaultValue = "") String planet,
                               @RequestParam(required = false, value = "shipType") ShipType shipType,
                               @RequestParam(required = false, value = "after", defaultValue = "26192302747154") Long minProdDate,
                               @RequestParam(required = false, value = "before", defaultValue = "33134715547154") Long maxProdDate,
                               @RequestParam(required = false, value = "minCrewSize", defaultValue = "1") Integer minCrewSize,
                               @RequestParam(required = false, value = "maxCrewSize", defaultValue = "9999") Integer maxCrewSize,
                               @RequestParam(required = false, value = "minSpeed", defaultValue = "0.01") Double minSpeed,
                               @RequestParam(required = false, value = "maxSpeed", defaultValue = "0.99") Double maxSpeed,
                               @RequestParam(required = false, value = "minRating", defaultValue = "0.01") Double minRating,
                               @RequestParam(required = false, value = "maxRating", defaultValue = "80") Double maxRating,
                               @RequestParam(required = false, value = "isUsed") Boolean isUsed) {


        Long count = shipService.getAll().stream().filter(ship -> ship.getName().contains(name)).
                filter(ship -> ship.getPlanet().contains(planet)).
                filter(ship -> (shipType == null || ship.getShipType().equals(shipType))).
                filter(ship -> ship.getProdDate().after(new Date(minProdDate))).
                filter(ship -> ship.getProdDate().before(new Date(maxProdDate))).
                filter(ship -> ship.getCrewSize() >= minCrewSize).
                filter(ship -> ship.getCrewSize() <= maxCrewSize).
                filter(ship -> ship.getSpeed() >= minSpeed).
                filter(ship -> ship.getSpeed() <= maxSpeed).
                filter(ship -> ship.getRating() >= minRating).
                filter(ship -> ship.getRating() <= maxRating).
                filter(ship -> (isUsed == null || ship.isUsed().equals(isUsed))).count();
        return Integer.parseInt(count.toString());
    }

    @GetMapping("/ships/{id}")
    public ResponseEntity<Ship> getShip(@PathVariable Long id) {
        if (id < 1) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Optional<Ship> ship = shipService.getShip(id);
        if (!ship.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(ship.get(), HttpStatus.OK);
    }

    @PostMapping("/ships")
    public ResponseEntity<Ship> createShip(@RequestBody(required = false) Ship ship) {
        if (ship.getProdDate() == null || ShipIf.conditionShip(ship))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (ship.isUsed() == null) ship.setUsed(false);
        ship.setUsed(ship.isUsed());
        Double rating = (ship.isUsed()) ? ((80 * ship.getSpeed() * 0.5) / (3019 - (ship.getProdDate().getYear() + 1900) + 1)) : ((80 * ship.getSpeed() * 1) / (3019 - (ship.getProdDate().getYear() + 1900) + 1));
        rating = Math.round(rating * 100) / 100.0;
        ship.setRating(rating);
        return new ResponseEntity<>(shipService.createShip(ship), HttpStatus.OK);
    }

    @PostMapping("/ships/{id}")
    public ResponseEntity<Ship> updateShip(@RequestBody(required = false) Ship ship, @PathVariable("id") Long id) {
        if (ship.getName() == null && ship.getPlanet() == null && ship.getSpeed() == null
                && ship.getProdDate() == null && ship.getCrewSize() == null && ship.getShipType() == null
                && ship.isUsed() == null) return new ResponseEntity<>(shipService.getShip(id).get(), HttpStatus.OK);

        if (id < 1) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Ship shipUpdate = !shipService.getShip(id).isPresent() ? null : shipService.getShip(id).get();
        if (shipUpdate == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        //if (ship.getId() != null) shipUpdate.setId(ship.getId());
        if (ship.getName() != null)
            if (ship.getName().length() <= 50 && !ship.getName().isEmpty()) shipUpdate.setName(ship.getName());
            else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (ship.getPlanet() != null)
            if (ship.getPlanet().length() <= 50 && !ship.getPlanet().isEmpty()) shipUpdate.setPlanet(ship.getPlanet());
            else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (ship.getShipType() != null) shipUpdate.setShipType(ship.getShipType());
        if (ship.getProdDate() != null)
            if (ship.getProdDate().getYear() + 1900 >= 2800 && ship.getProdDate().getYear() + 1900 <= 3019)
                shipUpdate.setProdDate(ship.getProdDate());
            else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (ship.isUsed() != null) shipUpdate.setUsed(ship.isUsed());
        if (ship.getSpeed() != null)
            if (ship.getSpeed() < 1.0 && ship.getSpeed() > 0.00) shipUpdate.setSpeed(ship.getSpeed());
            else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (ship.getCrewSize() != null)
            if (ship.getCrewSize() < 10000 && ship.getCrewSize() > 0) shipUpdate.setCrewSize(ship.getCrewSize());
            else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Double rating = (shipUpdate.isUsed()) ? ((80 * shipUpdate.getSpeed() * 0.5) / (3019 - (shipUpdate.getProdDate().getYear() + 1900) + 1)) : ((80 * shipUpdate.getSpeed() * 1) / (3019 - (shipUpdate.getProdDate().getYear() + 1900) + 1));
        rating = Math.round(rating * 100) / 100.0;
        shipUpdate.setRating(rating);
        //if (ship.getRating() == null) shipUpdate.setRating(rating); else shipUpdate.setRating(ship.getRating());
        return new ResponseEntity<>(shipService.update(shipUpdate), HttpStatus.OK);
    }

    @DeleteMapping("/ships/{id}")
    public ResponseEntity deleteShip(@PathVariable Long id) {
        if (id < 1) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        if (!shipService.getShip(id).isPresent()) return new ResponseEntity(HttpStatus.NOT_FOUND);
        shipService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
