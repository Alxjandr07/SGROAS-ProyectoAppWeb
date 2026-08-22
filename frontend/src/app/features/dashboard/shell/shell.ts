import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet, Router } from '@angular/router';
import { Auth } from '../../../core/services/auth';

interface NavItem {
  label: string;
  path: string;
}

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, RouterOutlet],
  templateUrl: './shell.html',
  styleUrl: './shell.scss'
})
export class Shell {
  private authService = inject(Auth);
  private router = inject(Router);

  navItems: NavItem[] = [
    { label: 'Inicio', path: '' },
    { label: 'Usuarios y Roles', path: 'usuarios' },
    { label: 'Flota Vehicular', path: 'flota' },
    { label: 'Unidades', path: 'unidades' },
    { label: 'Rutas', path: 'rutas' },
    { label: 'Programaciones', path: 'programaciones' },
    { label: 'Seguridad', path: 'seguridad' },
    { label: 'Reportes', path: 'reportes' }
  ];

  get currentUser() {
    return this.authService.currentUser();
  }

  get initials(): string {
    const name = this.currentUser?.nombre ?? '';
    return name
      .split(' ')
      .map((p) => p[0])
      .slice(0, 2)
      .join('')
      .toUpperCase();
  }

  logout(): void {
    this.authService.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login'])
    });
  }
}