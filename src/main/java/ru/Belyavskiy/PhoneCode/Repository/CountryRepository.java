package ru.Belyavskiy.PhoneCode.Repository;

import org.springframework.stereotype.Repository;
import ru.Belyavskiy.PhoneCode.Entity.Country;

import java.util.List;

@Repository
public interface CountryRepository {
    List<Country> findByCountryStartsWith(String countryName);
    Country getByName(String name);
}
