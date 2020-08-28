package ru.Belyavskiy.PhoneCode.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import ru.Belyavskiy.PhoneCode.Entity.Country;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class ExternalCountryRepositoryImpl implements CountryRepository {

    Logger logger = LoggerFactory.getLogger(ExternalCountryRepositoryImpl.class);


    private Map<String, String> countryNames = new ConcurrentHashMap<>();
    private Map<String, String> countryCodes = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate;



    private final String remoteNamesPath;
    private final String remotePhonePath;
    private final String localNamesPath;
    private final String localPhonePath;

    public ExternalCountryRepositoryImpl(RestTemplate restTemplate,
                                         @Value("${remote.path.names}") String remoteNamesPath,
                                         @Value("${remote.path.phone}") String remotePhonePath,
                                         @Value("${local.path.names}") String localNamesPath,
                                         @Value("${local.path.phone}") String localPhonePath) {
        this.restTemplate = restTemplate;
        this.remoteNamesPath = remoteNamesPath;
        this.remotePhonePath = remotePhonePath;
        this.localNamesPath = localNamesPath;
        this.localPhonePath = localPhonePath;

        updateCountries();

    }
    private boolean tryUpdateData(String remotePath, String localPath, Map<String, String> data){
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = restTemplate.getForObject(remotePath, String.class);
            Map<String, String> map = mapper.readValue(json, new TypeReference<HashMap<String,String>>(){});
            data.clear();
            data.putAll(map);



            Path path = Paths.get(localPath);
            if (!Files.exists(path)) {
                logger.debug("Создаём файл");
                Files.createFile(path);
            }
            Files.write(path, json.getBytes());

        }
        catch (Exception e){
            logger.debug("Не удалось получить данные из удалённого источника с ошибкой: "+e.getMessage());

            try {
                Path path = Paths.get(localPath);
                if (!Files.exists(path))
                    return false;

                String json = new String(Files.readAllBytes(path));
                Map<String, String> map = mapper.readValue(json, new TypeReference<HashMap<String,String>>(){});
                data.clear();
                data.putAll(map);

            }
            catch (Exception el){
                return false;

            }
        }

        return true;
    }

    private void updateCountries() {

        boolean success = tryUpdateData(remoteNamesPath, localNamesPath, countryNames);
        if(!success){
            logger.error("Не удалось  получить список стран");
                return;
        }
        success = tryUpdateData(remotePhonePath, localPhonePath, countryCodes);
        if(!success){
            logger.error("Не удалось  получить список кодов стран");
                return;
        }


        logger.info("Данные успешно  получены");
    }

    @Override
    @Cacheable(cacheNames = "recordsCache", key = "#countryName")
    public List<Country> findByCountryStartsWith(String countryName) {


        Predicate<Map.Entry<String, String>> filter = entry -> entry.getValue().toLowerCase().startsWith(countryName.toLowerCase());
        List<String> filteredKeys = countryNames.entrySet().stream().filter(filter).map(Map.Entry::getKey).collect(Collectors.toList());

        return filteredKeys.stream().map(key -> getByName(key)).collect(Collectors.toList());
    }

    @Override
    public Country getByName(String name) {

        if (!countryNames.containsKey(name)) throw new IllegalArgumentException();
        Country countryEntity = new Country();
        countryEntity.setName(name);
        countryEntity.setCountry(countryNames.get(name));
        countryEntity.setCode(countryCodes.get(name));
        return countryEntity;
    }


}
