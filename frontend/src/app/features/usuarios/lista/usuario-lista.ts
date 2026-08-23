import { Component, signal, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { UsuarioService } from '../../../core/services/usuario.service';
import { Usuario } from '../../../core/models/usuario.model';

@Component({
  selector: 'app-usuario-lista',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './usuario-lista.html',
  styleUrl: './usuario-lista.scss',
})
export class UsuarioLista implements OnInit, OnDestroy {
  private service = inject(UsuarioService);
  private destruir$ = new Subject<void>();
  private buscar$ = new Subject<string>();

  buscar = signal('');
  usuarios = signal<Usuario[]>([]);
  loading = signal(true);
  totalPages = signal(0);
  currentPage = signal(0);
  errorMsg = signal<string | null>(null);

  ngOnInit(): void {
    this.cargar();
    this.buscar$
      .pipe(debounceTime(350), distinctUntilChanged(), takeUntil(this.destruir$))
      .subscribe(() => this.cargar(0));
  }

  ngOnDestroy(): void {
    this.destruir$.next();
    this.destruir$.complete();
  }

  cargar(page = 0): void {
    this.loading.set(true);
    this.errorMsg.set(null);
    this.service.listar(page, 10, this.buscar()).subscribe({
      next: (res) => {
        this.usuarios.set(res.content);
        this.totalPages.set(res.totalPages);
        this.currentPage.set(res.number);
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set('Error al cargar usuarios.');
        this.loading.set(false);
      },
    });
  }

  enBuscar(event: Event): void {
    this.buscar.set((event.target as HTMLInputElement).value);
    this.buscar$.next(this.buscar());
  }

  cambiarPagina(p: number): void {
    if (p >= 0 && p < this.totalPages()) {
      this.cargar(p);
    }
  }

  confirmarEliminar(id: number): void {
    if (confirm('¿Desactivar este usuario?')) {
      this.service.desactivar(id).subscribe({
        next: () => this.cargar(this.currentPage()),
        error: () => alert('No se pudo desactivar el usuario.'),
      });
    }
  }
}
