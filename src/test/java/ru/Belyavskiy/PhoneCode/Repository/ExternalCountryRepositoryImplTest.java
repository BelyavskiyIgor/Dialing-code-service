package ru.Belyavskiy.PhoneCode.Repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import ru.Belyavskiy.PhoneCode.Entity.Country;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ExternalCountryRepositoryImplTest {

    private static CountryRepository countryRepository;

    private static RestTemplate restTemplate;

    private static String localNamePath = "test_names.json";
    private static String localPhonePath = "test_phone.json";

    @BeforeEach
    public void setUp() {
        String remoteNamePath = "test_names.json";
        String remotePhonePath = "test_phone.json";

        restTemplate = mock(RestTemplate.class);


        String namesJson = "{\"BD\": \"Bangladesh\", \"BE\": \"Belgium\", \"BF\": \"Burkina Faso\"}";
        when(restTemplate.getForObject(
                eq(remoteNamePath),
                eq(String.class)))
                .thenReturn(namesJson);

        String phoneJson = "{\"BD\": \"880\", \"BE\": \"32\", \"BF\": \"226\"}";
        when(restTemplate.getForObject(
                eq(remotePhonePath),
                eq(String.class)))
                .thenReturn(phoneJson);

        countryRepository = new ExternalCountryRepositoryImpl(restTemplate, remoteNamePath, remotePhonePath, localNamePath, localPhonePath);
    }
    @Test
    public void saveFileTest(){
        Path path = Paths.get(localNamePath);
        assertTrue(Files.exists(path));

        Path path1 = Paths.get(localPhonePath);
        assertTrue(Files.exists(path1));
    }

    @Test
    public void findByIdTest() {
        Country country = countryRepository.getByName("BD");

        assertNotNull(country);
        assertEquals("BD", country.getName());
        assertEquals("Bangladesh", country.getCountry());
        assertEquals("880", country.getCode());

    }

    @Test
    public void findByIdTestNegative() {
        assertThrows(IllegalArgumentException.class, () -> countryRepository.getByName("NO"));

    }


}
