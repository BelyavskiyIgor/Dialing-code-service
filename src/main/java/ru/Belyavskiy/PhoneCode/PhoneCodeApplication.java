package ru.Belyavskiy.PhoneCode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PhoneCodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhoneCodeApplication.class, args);
	}

}
