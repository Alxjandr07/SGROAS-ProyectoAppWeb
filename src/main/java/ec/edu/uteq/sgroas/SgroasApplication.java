package ec.edu.uteq.sgroas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SgroasApplication {

    /**
     * Inicia la aplicacion de Spring con la configuracion detectada en el proyecto.
     * @param args argumentos de linea de comandos que ajustan el arranque
     */
	public static void main(String[] args) {
		SpringApplication.run(SgroasApplication.class, args);
	}

}
