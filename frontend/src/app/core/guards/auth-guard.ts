import { inject } from '@angular/core';
import { CanActivateFn, Route, Router } from '@angular/router';
import { Auth } from '../services/auth';

export const authGuard: CanActivateFn = () => {
  const authService = inject(Auth);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};

/** Guardia de roles: la ruta declara data.roles con los roles permitidos
 *  (sin el prefijo ROLE_). Sin data.roles deja pasar a cualquier autenticado. */
export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(Auth);
  const router = inject(Router);
  const roles: string[] = route.data['roles'] ?? [];

  if (!authService.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }
  if (roles.length === 0 || authService.tieneRol(roles)) {
    return true;
  }
  router.navigate(['/dashboard']);
  return false;
};
