package ru.Belyavskiy.PhoneCode.Repository;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import ru.Belyavskiy.PhoneCode.Entity.Country;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class ExternalCountryRepositoryImpl implements CountryRepository{

    private Map<String, String> countryNames = new HashMap<>();
    private Map<String, String> countryCodes = new HashMap<>();

    private RestTemplate restTemplate;


    public ExternalCountryRepositoryImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;



        //Подготавливаем возвращаемый тип  запроса
        ParameterizedTypeReference<Map<String, String>> responseType = new ParameterizedTypeReference<Map<String, String>>() {};

        // делаем запрос
        ResponseEntity<Map<String, String>> response = restTemplate.exchange("names.json", HttpMethod.GET, null, responseType);

        //результат  запроса  кладем в  мапу
        countryNames = response.getBody();

        // делаем запрос
        response = restTemplate.exchange("phone.json", HttpMethod.GET, null, responseType);

        //результат  запроса  кладем в  мапу
        countryCodes = response.getBody();

        System.out.println(countryNames.size() + " " + countryCodes.size());

    }


    @Override
    public List<Country> findByCountryStartsWith(String countryName) {

        // подготовить предикат
        Predicate<Map.Entry<String, String>> filter = entry -> entry.getValue().toLowerCase().startsWith(countryName.toLowerCase());

        // профильтровать мапу по предикату
        List<String> filteredKeys = countryNames.entrySet().stream().filter(filter).map(Map.Entry::getKey).collect(Collectors.toList());
        // и собрать список ключей

        // превратить список ключей в список сущностей
        return filteredKeys.stream().map(key -> formCountry(key)).collect(Collectors.toList());
    }

    private Country formCountry(String country){
        //Если указали неправильный  индефикатор
        if(!countryNames.containsKey(country)) throw new IllegalArgumentException();


        Country countryEntity = new Country();

        countryEntity.setName(country);
        //взяли из мапы значения(название страны)
        countryEntity.setCountry(countryNames.get(country));
        countryEntity.setCode(countryCodes.get(country));

        return countryEntity;
    }




}
