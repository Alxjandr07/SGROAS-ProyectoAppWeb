import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { RutaAbdService } from '../../../core/services/abd/ruta-abd.service';
import { CatalogoAbdService } from '../../../core/services/abd/catalogo-abd.service';
import { Auth } from '../../../core/services/auth';
import { Paginador } from '../../../shared/components/paginador/paginador';
import { RutaAbd, TerminalAbd } from '../../../core/models/abd.model';

@Component({
  selector: 'app-rutas-lista',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, Paginador],
  templateUrl: './rutas-lista.html',
  styleUrl: './rutas-lista.scss',
})
export class RutasLista implements OnInit, OnDestroy {
  private rutaService = inject(RutaAbdService);
  private catalogoService = inject(CatalogoAbdService);
  private authService = inject(Auth);
  private fb = inject(FormBuilder);
  private destruir$ = new Subject<void>();
  private buscar$ = new Subject<string>();

  buscar = signal('');

  get puedeEditar(): boolean {
    return this.authService.tieneRol(['ADMIN', 'COORDINADOR']);
  }

  rutas = signal<RutaAbd[]>([]);
  terminales = signal<TerminalAbd[]>([]);
  loading = signal(true);
  guardando = signal(false);
  errorMsg = signal<string | null>(null);
  errorForm = signal<string | null>(null);
  mostrandoFormulario = signal(false);
  editId = signal<number | null>(null);

  totalPages = signal(0);
  totalElements = signal(0);
  currentPage = signal(0);

  form = this.fb.group({
    idTerminalOrigen: [null as number | null, Validators.required],
    idTerminalDestino: [null as number | null, Validators.required],
    precioPasaje: [null as number | null, [Validators.required, Validators.min(0.01)]],
  });

  ngOnInit(): void {
    this.cargarCatalogo();
    this.cargar();
    this.buscar$
      .pipe(debounceTime(350), distinctUntilChanged(), takeUntil(this.destruir$))
      .subscribe(() => this.cargar(0));
  }

  ngOnDestroy(): void {
    this.destruir$.next();
    this.destruir$.complete();
  }

  cargarCatalogo(): void {
    this.catalogoService.obtener().subscribe({
      next: (cat) => this.terminales.set(cat.terminales),
      error: () => this.errorMsg.set('Error al cargar terminales.'),
    });
  }

  cargar(page = 0): void {
    this.loading.set(true);
    this.errorMsg.set(null);
    this.rutaService.listar(page, 50, this.buscar()).subscribe({
      next: (res) => {
        this.rutas.set(res.content);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.currentPage.set(res.number);
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set('Error al cargar rutas.');
        this.loading.set(false);
      },
    });
  }

  cambiarPagina(p: number): void {
    this.cargar(p);
  }

  enBuscar(event: Event): void {
    this.buscar.set((event.target as HTMLInputElement).value);
    this.buscar$.next(this.buscar());
  }

  abrirFormulario(): void {
    this.editId.set(null);
    this.form.reset();
    this.errorForm.set(null);
    this.mostrandoFormulario.set(true);
  }

  editar(ruta: RutaAbd): void {
    this.editId.set(ruta.idRuta);
    this.form.patchValue({
      idTerminalOrigen: ruta.idTerminalOrigen,
      idTerminalDestino: ruta.idTerminalDestino,
      precioPasaje: ruta.precioPasaje,
    });
    this.errorForm.set(null);
    this.mostrandoFormulario.set(true);
  }

  cancelar(): void {
    this.mostrandoFormulario.set(false);
    this.form.reset();
  }

  guardar(): void {
    if (this.form.invalid) return;
    const v = this.form.value;
    const data = {
      idTerminalOrigen: v.idTerminalOrigen!,
      idTerminalDestino: v.idTerminalDestino!,
      precioPasaje: v.precioPasaje!,
    };
    this.guardando.set(true);
    this.errorForm.set(null);
    const peticion = this.editId()
      ? this.rutaService.actualizar(this.editId()!, data)
      : this.rutaService.crear(data);
    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.cancelar();
        this.cargar(this.currentPage());
      },
      error: (err) => {
        this.guardando.set(false);
        this.errorForm.set(err?.error?.detail ?? 'No se pudo guardar la ruta.');
      },
    });
  }

  confirmarEliminar(id: number): void {
    if (confirm('¿Eliminar esta ruta?')) {
      this.rutaService.eliminar(id).subscribe({
        next: () => this.cargar(this.currentPage()),
        error: () => alert('No se pudo eliminar la ruta. Verifique que no tenga programaciones asociadas.'),
      });
    }
  }
}
