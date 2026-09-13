package ec.edu.uteq.sgroas.security;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class LoginRateLimiter {

    private final ConcurrentHashMap<String, long[]> intentos = new ConcurrentHashMap<>();
    private static final long MAX_INTENTOS = 6;
    private static final long VENTANA_MS = 60_000;

    /**
     * Indica si una direccion ya supero los intentos fallidos de la ventana actual.
     * @param ip direccion del cliente cuyo historial de intentos se revisa
     * @return verdadero cuando se alcanzo el maximo y el bloqueo sigue vigente
     */
    public boolean estaBloqueado(String ip) {
        long[] datos = intentos.get(ip);
        if (datos == null) return false;
        long ahora = System.currentTimeMillis();
        if (ahora - datos[1] > VENTANA_MS) {
            intentos.remove(ip);
            return false;
        }
        return datos[0] >= MAX_INTENTOS;
    }

    /**
     * Suma un fallo de acceso al conteo de la direccion dentro de la ventana actual.
     * @param ip direccion del cliente que realizo el intento de acceso fallido
     */
    public void registrarIntentoFallido(String ip) {
        intentos.compute(ip, (k, v) -> {
            if (v == null) return new long[]{1, System.currentTimeMillis()};
            if (System.currentTimeMillis() - v[1] > VENTANA_MS) return new long[]{1, System.currentTimeMillis()};
            v[0]++;
            return v;
        });
    }

    /**
     * Borra el historial de fallos de una direccion tras un acceso exitoso.
     * @param ip direccion del cliente cuyo conteo de intentos se elimina
     */
    public void resetear(String ip) {
        intentos.remove(ip);
    }
}
