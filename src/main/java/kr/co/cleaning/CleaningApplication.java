package kr.co.cleaning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@MapperScan("kr.co.cleaning.mapper")
@EnableAsync
public class CleaningApplication {

	private static final Logger log = LoggerFactory.getLogger(CleaningApplication.class);

	public static void main(String[] args) {
		log.info("============================================");
		log.info("  Careville Backend - Starting Application  ");
		log.info("============================================");
		SpringApplication.run(CleaningApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		log.info("============================================");
		log.info("  Careville Backend - Application Started!  ");
		log.info("  Health Check: /health                     ");
		log.info("  Admin Panel: /apage/                      ");
		log.info("============================================");
	}
}
