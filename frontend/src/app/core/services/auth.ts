import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { catchError, EMPTY } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, LoginResponse } from '../models/auth.model';

const SESION_KEY = 'sgroas_sesion';

@Injectable({
  providedIn: 'root'
})
export class Auth {
  private readonly apiUrl = `${environment.apiUrl}/auth`;

  currentUser = signal<LoginResponse | null>(this.cargarLocal());

  constructor(private http: HttpClient) {
    this.validarSesionConServidor();
  }

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/login`, credentials, {
        withCredentials: true
      })
      .pipe(tap((response) => this.guardarSesion(response)));
  }

  logout(): Observable<void> {
    const refreshToken = this.currentUser()?.refreshToken ?? '';
    return this.http
      .post<void>(`${this.apiUrl}/logout`, { refreshToken }, { withCredentials: true })
      .pipe(
        tap(() => this.limpiarSesion()),
        catchError(() => {
          this.limpiarSesion();
          return EMPTY;
        })
      );
  }

  isAuthenticated(): boolean {
    return this.currentUser() !== null;
  }

  rolActual(): string | null {
    const rol = this.currentUser()?.rol;
    return rol ? rol.replace('ROLE_', '') : null;
  }

  tieneRol(roles: string[]): boolean {
    const rol = this.rolActual();
    return rol !== null && roles.includes(rol);
  }

  private guardarSesion(response: LoginResponse): void {
    this.currentUser.set(response);
    try {
      localStorage.setItem(SESION_KEY, JSON.stringify(response));
    } catch { /* almacenamiento no disponible */ }
  }

  private limpiarSesion(): void {
    this.currentUser.set(null);
    try {
      localStorage.removeItem(SESION_KEY);
    } catch { /* almacenamiento no disponible */ }
  }

  private cargarLocal(): LoginResponse | null {
    try {
      const bruto = localStorage.getItem(SESION_KEY);
      return bruto ? (JSON.parse(bruto) as LoginResponse) : null;
    } catch {
      return null;
    }
  }

  /** Confirma contra el backend que la cookie HttpOnly sigue valida
   *  (al recargar la pagina el signal se restaura del localStorage y
   *  aqui se descarta si la sesion ya expiro). */
  private validarSesionConServidor(): void {
    if (this.currentUser() === null) return;
    this.http
      .get<LoginResponse>(`${this.apiUrl}/me`, { withCredentials: true })
      .subscribe({
        next: (response) => this.guardarSesion(response),
        error: () => this.limpiarSesion()
      });
  }
}
