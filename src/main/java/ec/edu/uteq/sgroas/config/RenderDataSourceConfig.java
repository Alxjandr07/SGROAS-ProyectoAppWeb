package ec.edu.uteq.sgroas.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class RenderDataSourceConfig {

    private static final String DEFAULT_PORT = "5432";

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