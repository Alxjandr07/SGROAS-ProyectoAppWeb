import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { UnidadAbdService } from '../../../core/services/abd/unidad-abd.service';
import { Auth } from '../../../core/services/auth';
import { Paginador } from '../../../shared/components/paginador/paginador';
import { UnidadAbd } from '../../../core/models/abd.model';

const ESTADOS = ['Activo', 'Inactivo', 'En Mantenimiento'];

@Component({
  selector: 'app-unidades-lista',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, Paginador],
  templateUrl: './unidades-lista.html',
  styleUrl: './unidades-lista.scss',
})
export class UnidadesLista implements OnInit, OnDestroy {
  private service = inject(UnidadAbdService);
  private authService = inject(Auth);
  private fb = inject(FormBuilder);
  private destruir$ = new Subject<void>();
  private buscar$ = new Subject<string>();

  estados = ESTADOS;
  filtroEstado = signal('');
  buscar = signal('');

  get puedeEditar(): boolean {
    return this.authService.tieneRol(['ADMIN', 'COORDINADOR']);
  }

  unidades = signal<UnidadAbd[]>([]);
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
    placa: ['', [Validators.required, Validators.maxLength(15)]],
    numeroDisco: ['', [Validators.required, Validators.maxLength(10)]],
    modelo: ['', [Validators.required, Validators.maxLength(50)]],
    capacidad: [null as number | null, [Validators.required, Validators.min(1), Validators.max(200)]],
    anioFabricacion: [null as number | null],
    estado: ['Activo', Validators.required],
  });

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
    this.service.listar(page, 50, this.filtroEstado(), this.buscar()).subscribe({
      next: (res) => {
        this.unidades.set(res.content);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.currentPage.set(res.number);
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set('Error al cargar unidades.');
        this.loading.set(false);
      },
    });
  }

  aplicarFiltro(event: Event): void {
    this.filtroEstado.set((event.target as HTMLSelectElement).value);
    this.cargar(0);
  }

  enBuscar(event: Event): void {
    this.buscar.set((event.target as HTMLInputElement).value);
    this.buscar$.next(this.buscar());
  }

  cambiarPagina(p: number): void {
    this.cargar(p);
  }

  abrirFormulario(): void {
    this.editId.set(null);
    this.form.reset({ estado: 'Activo' });
    this.errorForm.set(null);
    this.mostrandoFormulario.set(true);
  }

  editar(u: UnidadAbd): void {
    this.editId.set(u.idUnidad);
    this.form.patchValue({
      placa: u.placa,
      numeroDisco: u.numeroDisco,
      modelo: u.modelo,
      capacidad: u.capacidad,
      anioFabricacion: u.anioFabricacion,
      estado: u.estado,
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
      placa: v.placa!.trim(),
      numeroDisco: v.numeroDisco!.trim(),
      modelo: v.modelo!.trim(),
      capacidad: v.capacidad!,
      anioFabricacion: v.anioFabricacion,
      estado: v.estado!,
    };
    this.guardando.set(true);
    this.errorForm.set(null);
    const peticion = this.editId()
      ? this.service.actualizar(this.editId()!, data)
      : this.service.crear(data);
    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.cancelar();
        this.cargar(this.currentPage());
      },
      error: (err) => {
        this.guardando.set(false);
        this.errorForm.set(err?.error?.detail ?? 'No se pudo guardar la unidad.');
      },
    });
  }

  confirmarEliminar(id: number): void {
    if (confirm('¿Eliminar esta unidad?')) {
      this.service.eliminar(id).subscribe({
        next: () => this.cargar(this.currentPage()),
        error: () => alert('No se pudo eliminar la unidad. Verifique que no tenga registros asociados.'),
      });
    }
  }
}
