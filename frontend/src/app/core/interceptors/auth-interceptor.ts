import { HttpInterceptorFn } from '@angular/common/http';

const SESION_KEY = 'sgroas_sesion';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  let token: string | null = null;
  try {
    const raw = localStorage.getItem(SESION_KEY);
    if (raw) {
      const sesion = JSON.parse(raw) as { accessToken?: string };
      token = sesion?.accessToken ?? null;
    }
  } catch {
    token = null;
  }

  const authReq = token
    ? req.clone({
        withCredentials: true,
        setHeaders: { Authorization: `Bearer ${token}` },
      })
    : req.clone({ withCredentials: true });

  return next(authReq);
};
