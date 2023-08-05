package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;

import java.util.Date;

public class AirportService {

    AirportRepository airportRepository = new AirportRepository();
    public void add(Airport airport) {
        airportRepository.add(airport);
    }

    public String getLargestAirportName() {
        return airportRepository.getLargestAirportName();
    }

    public double getShortestDurationBetweenCities(City fromCity, City toCity) {
        return airportRepository.getShortestDurationBetweenCities(fromCity,toCity);
    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
       return airportRepository.getNumberOfPeopleOn(date, airportName);
    }

    public int calculateFlightFare(Integer flightId) {
        return airportRepository.calculateFlightFare(flightId);
    }

    public String bookATicket(Integer flightId, Integer passengerId) {
        return  airportRepository.bookATicket(flightId,passengerId);
    }

    public String cancelATicket(Integer flightId, Integer passengerId) {
        return airportRepository.cancelATicket(flightId,passengerId);
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        return airportRepository.countOfBookingsDoneByPassengerAllCombined(passengerId);
    }

    public String addFlight(Flight flight) {
        return airportRepository.addFlight(flight);
    }

    public String getAirportNameFromFlightId(Integer flightId) {
        return airportRepository.getAirportNameFromFlightId(flightId);
    }

    public int calculateRevenueOfAFlight(Integer flightId) { return airportRepository.calculateRevenueOfAFlight(flightId);
    }

    public String addPassenger(Passenger passenger) {return airportRepository.addPassenger(passenger);
    }
}
