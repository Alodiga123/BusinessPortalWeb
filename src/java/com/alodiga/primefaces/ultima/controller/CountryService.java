package com.alodiga.primefaces.ultima.controller;
 
import com.portal.business.commons.remittance.RemittanceCountry;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
 
@ManagedBean(name="countryService", eager = true)
@ApplicationScoped
public class CountryService {
     
    private List<CountryControllerList> countries;
     
    @PostConstruct
    public void init() {
        
        countries = new ArrayList<CountryControllerList>();
        countries.add(new CountryControllerList(0, "Afterdark", "afterdark"));
        countries.add(new CountryControllerList(1, "Afternoon", "afternoon"));
        countries.add(new CountryControllerList(2, "Afterwork", "afterwork"));
        countries.add(new CountryControllerList(3, "Aristo", "aristo"));
        countries.add(new CountryControllerList(4, "Black-Tie", "black-tie"));
        countries.add(new CountryControllerList(5, "Blitzer", "blitzer"));
        countries.add(new CountryControllerList(6, "Bluesky", "bluesky"));
        countries.add(new CountryControllerList(7, "Bootstrap", "bootstrap"));
        countries.add(new CountryControllerList(8, "Casablanca", "casablanca"));
        countries.add(new CountryControllerList(9, "Cupertino", "cupertino"));
        countries.add(new CountryControllerList(10, "Cruze", "cruze"));
        countries.add(new CountryControllerList(11, "Dark-Hive", "dark-hive"));
        countries.add(new CountryControllerList(12, "Delta", "delta"));
        countries.add(new CountryControllerList(13, "Dot-Luv", "dot-luv"));
        countries.add(new CountryControllerList(14, "Eggplant", "eggplant"));
        countries.add(new CountryControllerList(15, "Excite-Bike", "excite-bike"));
        countries.add(new CountryControllerList(16, "Flick", "flick"));
        countries.add(new CountryControllerList(17, "Glass-X", "glass-x"));
        countries.add(new CountryControllerList(18, "Home", "home"));
        countries.add(new CountryControllerList(19, "Hot-Sneaks", "hot-sneaks"));
        countries.add(new CountryControllerList(20, "Humanity", "humanity"));
        countries.add(new CountryControllerList(21, "Le-Frog", "le-frog"));
        countries.add(new CountryControllerList(22, "Midnight", "midnight"));
        countries.add(new CountryControllerList(23, "Mint-Choc", "mint-choc"));
        countries.add(new CountryControllerList(24, "Overcast", "overcast"));
        countries.add(new CountryControllerList(25, "Pepper-Grinder", "pepper-grinder"));
        countries.add(new CountryControllerList(26, "Redmond", "redmond"));
        
    }

    public List<CountryControllerList> getCountries() {
        return countries;
    }

    public void setCountries(List<CountryControllerList> countries) {
        this.countries = countries;
    }
     
  
}