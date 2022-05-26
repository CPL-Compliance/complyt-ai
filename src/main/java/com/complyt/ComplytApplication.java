package com.complyt;

import com.complyt.domain.security.Authority;
import com.complyt.domain.security.User;
import com.complyt.repositories.security.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class ComplytApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ComplytApplication.class, args);
    }

//    @Autowired
//    PasswordEncoder passwordEncoder;

//    @Autowired
//    UserRepository userRepository;
    @Override
    public void run(String... args) throws Exception {
//        userRepository.findByName("admin").subscribe(System.out::println);

//        User user = User
//                .builder()
//                .password(passwordEncoder.encode("admin"))
//                .username("admin")
//                .authorities(Authority.builder().)
//                .build();
//
//        userRepository.insert(user).subscribe(userRepository -> {
//           System.out.println(user);
//        });
    }
}
