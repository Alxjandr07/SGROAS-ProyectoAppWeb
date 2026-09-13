package ec.edu.uteq.sgroas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    /**
     * Crea la fabrica de conexiones hacia el servidor Redis configurado en propiedades.
     * @return fabrica con direccion y puerto listos para abrir conexiones
     */
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);

        return new LettuceConnectionFactory(config);
    }

    /**
     * Expone la plantilla para leer y escribir cadenas de texto en Redis.
     * @param redisConnectionFactory fabrica con la conexion activa hacia el servidor Redis
     * @return plantilla preparada para operar con claves y valores de texto
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(
            LettuceConnectionFactory redisConnectionFactory
    ) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}