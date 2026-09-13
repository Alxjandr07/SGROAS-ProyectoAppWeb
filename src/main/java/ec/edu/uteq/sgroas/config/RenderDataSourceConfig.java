package ec.edu.uteq.sgroas.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class RenderDataSourceConfig {

    private static final String DEFAULT_PORT = "5432";

    /**
     * Crea la fuente de datos con las credenciales de PostgreSQL del entorno de despliegue.
     * @return fuente de datos conectada a la base indicada por las variables de entorno
     */
    @Bean
    @ConditionalOnProperty(name = "PGHOST")
    public DataSource renderDataSource() {
        return createDataSource(
                System.getenv("PGHOST"),
                System.getenv().getOrDefault("PGPORT", DEFAULT_PORT),
                System.getenv().getOrDefault("PGDATABASE", ""),
                System.getenv().getOrDefault("PGUSER", ""),
                System.getenv().getOrDefault("PGPASSWORD", ""));
    }

    /**
     * Arma una fuente de datos Hikari con los datos de conexion de PostgreSQL.
     * @param host nombre del servidor donde se aloja la base de datos
     * @param port puerto de escucha del servidor de base de datos
     * @param database nombre de la base a la que se desea conectar
     * @param user nombre del usuario con permiso de acceso a la base
     * @param password clave del usuario para autorizar la conexion
     * @return fuente de datos configurada con la URL y credenciales recibidas
     */
    static HikariDataSource createDataSource(String host, String port, String database,
                                             String user, String password) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + "/" + database);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }
}