package com.jofi.jofi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import io.github.cdimascio.dotenv.Dotenv;
@EnableAsync
@SpringBootApplication(scanBasePackages = {"com.jofi.jofi.service", "com.jofi.jofi.controller"})
public class JofiApplication {

	public static void main(String[] args) {
		    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    		System.setProperty("HUGGINGFACE_API_KEY", dotenv.get("HUGGINGFACE_API_KEY"));
			System.setProperty("OPENAI_API_KEY", dotenv.get("OPENAI_API_KEY"));
    		SpringApplication.run(JofiApplication.class, args);
	}
}
