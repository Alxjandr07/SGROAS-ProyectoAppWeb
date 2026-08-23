import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../../core/services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class Login {
  private fb = inject(FormBuilder);
  private authService = inject(Auth);
  private router = inject(Router);

  loading = signal(false);
  errorMsg = signal<string | null>(null);

  cuentasDemo = [
    { email: 'admin@sgroas.com', rol: 'Administrador', detalle: 'Acceso total al sistema' },
    { email: 'coordinador@sgroas.com', rol: 'Coordinador', detalle: 'Flota, unidades, rutas y programaciones' },
    { email: 'seguridad@sgroas.com', rol: 'Seguridad', detalle: 'Incidentes y alertas' },
    { email: 'operador@sgroas.com', rol: 'Operador', detalle: 'Registro de programaciones' },
  ];

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  usarCuenta(email: string): void {
    this.loginForm.patchValue({ email, password: 'admin123' });
    this.onSubmit();
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMsg.set(null);

    this.authService.login(this.loginForm.getRawValue() as { email: string; password: string }).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMsg.set(
          err.status === 401
            ? 'Correo o contraseña incorrectos.'
            : 'No se pudo conectar con el servidor. Intenta de nuevo.'
        );
      }
    });
  }
}