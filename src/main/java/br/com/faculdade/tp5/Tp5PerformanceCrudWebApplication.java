package br.com.faculdade.tp5;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"br.com.faculdade.tp5", "com.tp2.engsoftware"})
public class Tp5PerformanceCrudWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(Tp5PerformanceCrudWebApplication.class, args);
    }
}
