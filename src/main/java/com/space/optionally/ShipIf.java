package com.space.optionally;

import com.space.model.Ship;

public class ShipIf {
    public static boolean conditionShip(Ship ship) {
        return (ship.getName() == null || ship.getName().length() > 50 || ship.getPlanet() == null ||
                ship.getPlanet().length() > 50 || ship.getShipType() == null ||
                ship.getSpeed() == null || ship.getCrewSize() == null || ship.getName().isEmpty() || ship.getPlanet().isEmpty() ||
                ship.getSpeed() >= 1.0 || ship.getSpeed() <= 0.00 || ship.getCrewSize() >= 10000 || ship.getCrewSize() <= 0 ||
                ship.getProdDate().getYear() + 1900 < 2800 || ship.getProdDate().getYear() + 1900 > 3019);
    }
}
