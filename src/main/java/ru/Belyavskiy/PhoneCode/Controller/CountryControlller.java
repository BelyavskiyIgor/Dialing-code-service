package ru.Belyavskiy.PhoneCode.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.Belyavskiy.PhoneCode.Entity.Country;
import ru.Belyavskiy.PhoneCode.Service.CountryService;

import javax.validation.constraints.NotNull;

import java.util.List;

@RestController
@RequestMapping("/rest")
public class CountryControlller {
    private final CountryService countryService;

    public CountryControlller(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping(path = "/code", produces = "application/json")
    public List<Country> findByCountryStartsWith(@RequestParam String country) {
        return countryService.find(country);
    }


}
