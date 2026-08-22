import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UnidadAbdService } from '../../../core/services/abd/unidad-abd.service';
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
export class UnidadesLista implements OnInit {
  private service = inject(UnidadAbdService);
  private fb = inject(FormBuilder);

  estados = ESTADOS;
  filtroEstado = signal('');

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
  }

  cargar(page = 0): void {
    this.loading.set(true);
    this.errorMsg.set(null);
    this.service.listar(page, 50, this.filtroEstado()).subscribe({
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
