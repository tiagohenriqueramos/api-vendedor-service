package br.com.casasbahia;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class VendedorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VendedorServiceApplication.class, args);
    }

}
