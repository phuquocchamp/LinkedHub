//package com.phuquocchamp.backend.application;
//
//import com.phuquocchamp.backend.domain.authentication.model.AuthUser;
//import com.phuquocchamp.backend.domain.authentication.repository.AuthUserRepository;
//import com.phuquocchamp.backend.domain.authentication.utils.Encoder;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class InitialDatabase {
//    private final Encoder encoder;
//    public InitialDatabase(Encoder encoder) {
//        this.encoder = encoder;
//    }
//
//    @Bean
//    public CommandLineRunner init(AuthUserRepository repository) {
//        return args -> {
//            AuthUser u = new AuthUser("phuquocchamp@example.com", encoder.encode("user@123456"));
//            repository.save(u);
//        };
//
//    }
//}
