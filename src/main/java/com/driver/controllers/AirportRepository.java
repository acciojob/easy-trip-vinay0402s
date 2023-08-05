package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import io.swagger.models.auth.In;

import java.util.*;

public class AirportRepository {

    HashMap<String,Airport> airportDB = new HashMap<>();//stores airport name vs airport
    HashMap<Integer, Flight> flightDB = new HashMap<>();//flightid vs flight

    HashMap<Integer,List<Integer>> flightIdVsPassengerIdsDb=new HashMap<>();//stores flight id vs list of passenger ids,i.e in a particular

    HashMap<Integer,List<Integer>> passengerIdVsFlightIdsDb=new HashMap<>();//stores passengers Id vs bookings of that passenger over
    //various flightIds

    HashMap<Integer, Passenger> passengerDb=new HashMap<>();
    public void add(Airport airport) {
         airportDB.put(airport.getAirportName(), airport);
    }

    public String getLargestAirportName(){
        List<String> names = new ArrayList<>();
        int No_of_terminals = 0;
        for(String ele : airportDB.keySet()){
            int available_terminal = airportDB.get(ele).getNoOfTerminals();
            if(available_terminal>=No_of_terminals) {
                No_of_terminals = available_terminal;
                names.add(ele);//add name of airport to names
            }
        }
        Collections.sort(names);
        return names.get(0);
    }

    public double getShortestDurationBetweenCities(City fromCity, City toCity) {
        double min_time = Double.MAX_VALUE;
        for(Integer ele : flightDB.keySet()){
            Flight flight = flightDB.get(ele);
            if(flight.getFromCity().equals(fromCity) && flight.getToCity().equals(toCity)){
               //
                double time = flight.getDuration();
                if(time < min_time)
                    min_time=time;
            }
        }
        return min_time==Double.MAX_VALUE?-1:min_time;
    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
        //Calculate the total number of people who have flights on that day on a particular airport
        //This includes both the people who have come for a flight and who have landed on an airport after their flight
        if(airportDB.get(airportName)==null) return 0;
        City curr_city = airportDB.get(airportName).getCity();

        int cnt=0;
        if(flightDB.size()!=0){
            for(Integer id : flightDB.keySet()){
                Flight flightobject = flightDB.get(id);
                if(flightobject.getFromCity().equals(curr_city) || flightobject.getToCity().equals(curr_city)){
                    if(flightobject.getFlightDate().equals(date))
                        cnt+=flightIdVsPassengerIdsDb.get(id).size();
                }
            }
        }
        return cnt;
    }

    public int calculateFlightFare(Integer flightId){
        //Calculation of flight prices is a function of number of people who have booked the flight already.
        //Price for any flight will be : 3000 + noOfPeopleWhoHaveAlreadyBooked*50
        //Suppose if 2 people have booked the flight already : the price of flight for the third person will be 3000 + 2*50 = 3100
        //This will not include the current person who is trying to book, he might also be just checking price
       int noOfPeopleWhoHaveAlreadyBooked = flightIdVsPassengerIdsDb.get(flightId).size();
       int fare=0;
       int total = 3000+noOfPeopleWhoHaveAlreadyBooked*50;
       return  total;
    }

    public String bookATicket(Integer flightId, Integer passengerId) {
        //If the numberOfPassengers who have booked the flight is greater than : maxCapacity, in that case :
        //return a String "FAILURE"
        //Also if the passenger has already booked a flight then also return "FAILURE".

        if(flightIdVsPassengerIdsDb.get(flightId)!=null && flightIdVsPassengerIdsDb.get(flightId).size()==flightDB.get(flightId).getMaxCapacity())
            return "FAILURE";


        //if passenger booked the flight already means his/her details should be present in the database
        ///if it matches return failure
        if(passengerIdVsFlightIdsDb.get(passengerId)!=null){
            for(int i : passengerIdVsFlightIdsDb.get(passengerId)){
                if(i==passengerId)
                    return  "FAILURE";
            }
        }

        //else if you are able to book a ticket then return "SUCCESS"
        flightIdVsPassengerIdsDb.putIfAbsent(flightId,new ArrayList<>());
        flightIdVsPassengerIdsDb.get(flightId).add(passengerId);
        passengerIdVsFlightIdsDb.putIfAbsent(flightId,new ArrayList<>());
        passengerIdVsFlightIdsDb.get(flightId).add(passengerId);
        return "SUCCESS";
    }

    public String cancelATicket(Integer flightId, Integer passengerId) {
        //If the passenger has not booked a ticket for that flight or the flightId is invalid or in any other failure case
        // then return a "FAILURE" message
        // Otherwise return a "SUCCESS" message
        // and also cancel the ticket that passenger had booked earlier on the given flightId
        if(flightDB.get(flightId)==null) return "FAILURE";
        if(passengerIdVsFlightIdsDb.get(flightId)==null) return "FAILURE";

        boolean foundflight =false;
        for(Integer ele : passengerIdVsFlightIdsDb.get(flightId)){
            if(ele==flightId){
                foundflight=true;
                break;
            }
        }
        if(foundflight==false) return "FAILURE";
        passengerIdVsFlightIdsDb.get(passengerId).remove(flightId);
        flightIdVsPassengerIdsDb.get(flightId).remove(passengerId);
        return "SUCCESS";
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        //Tell the count of flight bookings done by a passenger: This will tell the total count of flight bookings done by a passenger :
        return passengerIdVsFlightIdsDb.get(passengerId).size();
    }

    public String addFlight(Flight flight) {
        flightDB.put(flight.getFlightId(),flight);
        return "SUCCESS";
    }

    public String getAirportNameFromFlightId(Integer flightId) {
        //We need to get the starting airportName from where the flight will be taking off (Hint think of City variable if that can be of some use)
        //return null incase the flightId is invalid or you are not able to find the airportName
        if(flightDB.get(flightId)==null) return null;
        City curr_city = flightDB.get(flightId).getFromCity();
        for(String name : airportDB.keySet()){
            Airport airportobj = airportDB.get(name);
            if(airportobj.getCity().equals(curr_city))
                return name;
        }
        return null;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {
        //Calculate the total revenue that a flight could have
        //That is of all the passengers that have booked a flight till now and then calculate the revenue
        //Revenue will also decrease if some passenger cancels the flight
        int totalpassenger = flightIdVsPassengerIdsDb.get(flightId).size();
        int val=totalpassenger-1;
        int total = 3000+totalpassenger*50*(val*(val+1)/2);
        return total;
    }

    public String addPassenger(Passenger passenger) {
        //Add a passenger to the database
        //And return a "SUCCESS" message if the passenger has been added successfully.
        passengerDb.put(passenger.getPassengerId(),passenger);
        return "SUCCESS";
    }
}
