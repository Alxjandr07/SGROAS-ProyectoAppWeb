package ec.edu.uteq.sgroas.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRateLimiterTest {

    @Test
    void ipSinIntentosNoDebeEstarBloqueada() {
        LoginRateLimiter limiter = new LoginRateLimiter();

        assertFalse(limiter.estaBloqueado("192.168.1.1"));
    }

    @Test
    void menosDeSeisIntentosNoBloquea() {
        LoginRateLimiter limiter = new LoginRateLimiter();
        for (int i = 0; i < 5; i++) {
            limiter.recordFailedAttempt("192.168.1.1");
        }

        assertFalse(limiter.estaBloqueado("192.168.1.1"));
    }

    @Test
    void seisIntentosFallidosBloquean() {
        LoginRateLimiter limiter = new LoginRateLimiter();
        for (int i = 0; i < 6; i++) {
            limiter.recordFailedAttempt("192.168.1.1");
        }

        assertTrue(limiter.estaBloqueado("192.168.1.1"));
    }

    @Test
    void resetearDebeDesbloquearIp() {
        LoginRateLimiter limiter = new LoginRateLimiter();
        for (int i = 0; i < 6; i++) {
            limiter.recordFailedAttempt("192.168.1.1");
        }
        assertTrue(limiter.estaBloqueado("192.168.1.1"));

        limiter.resetear("192.168.1.1");

        assertFalse(limiter.estaBloqueado("192.168.1.1"));
    }

    @Test
    void bloqueoExpiradoDebeDesbloquear() throws InterruptedException {
        LoginRateLimiter limiter = new LoginRateLimiter();
        for (int i = 0; i < 6; i++) {
            limiter.recordFailedAttempt("192.168.1.1");
        }
        assertTrue(limiter.estaBloqueado("192.168.1.1"));

        Thread.sleep(61_000);

        assertFalse(limiter.estaBloqueado("192.168.1.1"));
    }
}
