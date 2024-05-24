package co.edu.uniquindio.barberiavip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BarberiaVipApplication {

	public static void main(String[] args) {
		SpringApplication.run(BarberiaVipApplication.class, args);
	}

}
