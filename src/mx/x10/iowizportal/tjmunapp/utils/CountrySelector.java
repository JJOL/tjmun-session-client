package mx.x10.iowizportal.tjmunapp.utils;


import java.util.*;

/**
 * Created by JJOL on 20/08/2015.
 */
public class CountrySelector {

    private final Set<String> countryPool;

    private final Set<String> markedCountries = new LinkedHashSet<>();

    public CountrySelector(Set<String> listOfCountries) {
        countryPool = listOfCountries;
    }

    public List<String> getCountries(final String countryName) {
        final Set<String> matches = new HashSet<>();
        final String searchName = countryName.toLowerCase().trim();
        //if(searchName.isEmpty())
            //return new ArrayList<>(matches);

        for(String country : countryPool) {
            if(country.toLowerCase().startsWith(searchName)) {
                matches.add(country);
            }
        }

        return new ArrayList<>(matches);
    }

    public void addCountry(String name) {
        markedCountries.add(name);
    }

    public void removeCountry(String name) {
        markedCountries.remove(name);
    }

    public List<String> getCountriesList() {
        return new ArrayList<>(markedCountries);
    }




}
