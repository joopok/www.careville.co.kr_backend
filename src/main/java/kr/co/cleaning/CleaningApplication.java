package kr.co.cleaning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("kr.co.cleaning.mapper")
public class CleaningApplication {

	public static void main(String[] args) {
		SpringApplication.run(CleaningApplication.class, args);
	}

}
