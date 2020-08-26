package ru.Belyavskiy.PhoneCode.Service;

import org.springframework.stereotype.Service;
import ru.Belyavskiy.PhoneCode.Entity.Country;
import ru.Belyavskiy.PhoneCode.Repository.CountryRepository;

import java.util.List;

@Service
public class CountryService {
    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<Country> find(String country){
        return countryRepository.findByCountryStartsWith(country);
    }
}
