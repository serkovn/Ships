package com.space.service;

import com.space.model.Ship;
import com.space.repository.RepositoryShip;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceShip {
    private final RepositoryShip shipRepository;

    public ServiceShip(RepositoryShip shipRepository) {
        this.shipRepository = shipRepository;
    }

    public List<Ship> getAll() {

        return shipRepository.findAll();
    }

    public Optional<Ship> getShip(Long id) {
        return shipRepository.findById(id);
    }
    public Ship createShip(Ship ship) {
        return shipRepository.save(ship);
    }
    public Ship update(Ship ship) {
        Assert.notNull(ship, "");
        return shipRepository.save(ship);
    }
    public void delete(Long id) {
        shipRepository.deleteById(id);
    }

}
