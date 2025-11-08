package pe.com.farmaciadey.metodopago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"pe.com.farmaciadey.metodopago"})
public class MetodopagoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MetodopagoApplication.class, args);
	}

}
