package com.bankcardsystem.bankcardsystem;

import com.bankcardsystem.bankcardsystem.entity.Role;
import com.bankcardsystem.bankcardsystem.entity.User;
import com.bankcardsystem.bankcardsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BankCardSystemApplication {

//	@Autowired
//	private UserRepository userRepository;
//
//	@Autowired
//	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(BankCardSystemApplication.class, args);

	}
//	@Override
//	public void run(String... args) {
//		if (userRepository.findByEmailIgnoreCase("admin@example.com").isEmpty()) {
//			User admin = new User();
//			admin.setEmail("admin@example.com");
//			admin.setPassword(passwordEncoder.encode("admin123"));
//			admin.setRole(Role.ADMIN);
//			userRepository.save(admin);
//			System.out.println("✅ Admin created");
//		}
//	}


}
