package com.realpubliclibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class RealPublicLibraryApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(RealPublicLibraryApplication.class, args);
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(RealPublicLibraryApplication.class);
    }

}
