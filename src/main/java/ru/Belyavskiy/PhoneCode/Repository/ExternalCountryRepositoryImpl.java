package ru.Belyavskiy.PhoneCode.Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import ru.Belyavskiy.PhoneCode.Entity.Country;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class ExternalCountryRepositoryImpl implements CountryRepository {

    Logger logger = LoggerFactory.getLogger(ExternalCountryRepositoryImpl.class);


    private Map<String, String> countryNames = new HashMap<>();
    private Map<String, String> countryCodes = new HashMap<>();
    private RestTemplate restTemplate;

    public ExternalCountryRepositoryImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        updateCountries();

    }

    private void updateCountries() {


        ParameterizedTypeReference<Map<String, String>> responseType = new ParameterizedTypeReference<Map<String, String>>() {};
        try {
            ResponseEntity<Map<String, String>> response = restTemplate.exchange("names.json", HttpMethod.GET, null, responseType);

            if (response.getStatusCode() == HttpStatus.OK) {
                countryNames = response.getBody();
            } else {
                logger.error("Не удалось  получить список стран");
                return;
            }

            response = restTemplate.exchange("phone.json", HttpMethod.GET, null, responseType);
            if (response.getStatusCode() == HttpStatus.OK) {
                countryCodes = response.getBody();
            } else {
                logger.error("Не удалось  получить список кодов стран");
                return;
            }
        } catch (Exception e) {
            logger.error("Не удалось подключиться к  удаженному  источнику данных");
            return;
        }

        logger.info("Данные успешно  получены");
    }

    @Override
    @Cacheable(cacheNames = "recordsCache", key = "#countryName")
    public List<Country> findByCountryStartsWith(String countryName) {

        try {
            TimeUnit.SECONDS.sleep(10);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Predicate<Map.Entry<String, String>> filter = entry -> entry.getValue().toLowerCase().startsWith(countryName.toLowerCase());
        List<String> filteredKeys = countryNames.entrySet().stream().filter(filter).map(Map.Entry::getKey).collect(Collectors.toList());

        return filteredKeys.stream().map(key -> formCountry(key)).collect(Collectors.toList());
    }

    private Country formCountry(String country) {

        if (!countryNames.containsKey(country)) throw new IllegalArgumentException();
        Country countryEntity = new Country();
        countryEntity.setName(country);
        countryEntity.setCountry(countryNames.get(country));
        countryEntity.setCode(countryCodes.get(country));
        return countryEntity;
    }


}
