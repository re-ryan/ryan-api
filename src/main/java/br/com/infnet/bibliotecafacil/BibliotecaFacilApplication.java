package br.com.infnet.bibliotecafacil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class BibliotecaFacilApplication {

    public static void main(final String[] args) {
        SpringApplication.run(BibliotecaFacilApplication.class, args);
    }
}
