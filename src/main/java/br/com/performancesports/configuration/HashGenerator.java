package br.com.performancesports.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class HashGenerator {

//    @Bean
//    CommandLineRunner gerarHashes() {
//        return args -> {
//            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//            String senha = "Senha@123";
//            int quantidade = 10; // ajuste aqui
//
//            for (int i = 1; i <= quantidade; i++) {
//                String hash = encoder.encode(senha);
//                System.out.printf("%02d) HASH = %s%n", i, hash);
//            }
//        };
//    }

    @Bean
    String encoder() {
        String pass = new BCryptPasswordEncoder().encode("Prof@123");
        System.out.println(pass);
        return null;
    }
}
