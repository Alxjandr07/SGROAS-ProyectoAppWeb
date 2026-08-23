import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { IncidenteAbdService } from '../../../core/services/abd/incidente-abd.service';
import { UnidadAbdService } from '../../../core/services/abd/unidad-abd.service';
import { Paginador } from '../../../shared/components/paginador/paginador';
import { AlertaAbd, IncidenteAbd, UnidadAbd } from '../../../core/models/abd.model';

const TIPOS = ['Falla Mecanica', 'Accidente', 'Desvio de Ruta', 'Emergencia Medica', 'Robo', 'Otro'];
const NIVELES = ['BAJO', 'MEDIO', 'ALTO'];
const ESTADOS_INCIDENTE = ['Reportado', 'En Revision', 'Cerrado'];
const LIMITE_SELECT = 200;

@Component({
  selector: 'app-incidentes-lista',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, Paginador],
  templateUrl: './incidentes-lista.html',
  styleUrl: './incidentes-lista.scss',
})
export class IncidentesLista implements OnInit, OnDestroy {
  private service = inject(IncidenteAbdService);
  private unidadService = inject(UnidadAbdService);
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);
  private destruir$ = new Subject<void>();
  private buscar$ = new Subject<string>();

  private readonly alertasUrl = `${environment.apiUrl}/abd/alertas`;

  tipos = TIPOS;
  niveles = NIVELES;
  estadosIncidente = ESTADOS_INCIDENTE;
  filtroEstado = signal('');
  filtroNivel = signal('');
  buscar = signal('');

  incidentes = signal<IncidenteAbd[]>([]);
  unidades = signal<UnidadAbd[]>([]);
  alertas = signal<AlertaAbd[]>([]);

  loading = signal(true);
  guardando = signal(false);
  errorMsg = signal<string | null>(null);
  errorForm = signal<string | null>(null);
  mostrandoFormulario = signal(false);

  totalPages = signal(0);
  totalElements = signal(0);
  currentPage = signal(0);
  totalPaginasAlertas = 0;

  form = this.fb.group({
    tipo: [TIPOS[0], Validators.required],
    nivelSugerido: [NIVELES[0], Validators.required],
    estado: [ESTADOS_INCIDENTE[0], Validators.required],
    idUnidad: [null as number | null, Validators.required],
    evidencia: [''],
    descripcion: ['', [Validators.required, Validators.maxLength(500)]],
  });

  ngOnInit(): void {
    this.cargar();
    this.unidadService.listar(0, LIMITE_SELECT).subscribe({
      next: (res) => this.unidades.set(res.content),
    });
    this.cargarAlertas(0);
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
    this.service.listar(page, 50, {
      estado: this.filtroEstado() || undefined,
      nivel: this.filtroNivel() || undefined,
      search: this.buscar() || undefined,
    }).subscribe({
      next: (res) => {
        this.incidentes.set(res.content);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.currentPage.set(res.number);
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set('Error al cargar incidentes.');
        this.loading.set(false);
      },
    });
  }

  private cargarAlertas(page: number): void {
    if (page >= 3) return;
    this.http.get<any>(`${this.alertasUrl}?page=${page}&size=50`).subscribe({
      next: (res) => {
        const acumulado = page === 0 ? [] : this.alertas();
        this.alertas.set([...acumulado, ...res.content]);
        this.totalPaginasAlertas = res.totalPages;
        if (page + 1 < Math.min(res.totalPages, 3)) {
          this.cargarAlertas(page + 1);
        }
      },
    });
  }

  aplicarFiltroEstado(event: Event): void {
    this.filtroEstado.set((event.target as HTMLSelectElement).value);
    this.cargar(0);
  }

  aplicarFiltroNivel(event: Event): void {
    this.filtroNivel.set((event.target as HTMLSelectElement).value);
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
    this.form.reset({ tipo: TIPOS[0], nivelSugerido: NIVELES[0], estado: ESTADOS_INCIDENTE[0] });
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
      tipo: v.tipo!,
      descripcion: v.descripcion!.trim(),
      nivelSugerido: v.nivelSugerido!,
      evidencia: v.evidencia?.trim() || undefined,
      estado: v.estado!,
      idUnidad: v.idUnidad!,
    };
    this.guardando.set(true);
    this.errorForm.set(null);
    this.service.crear(data).subscribe({
      next: () => {
        this.guardando.set(false);
        this.cancelar();
        this.alertas.set([]);
        this.totalPaginasAlertas = 0;
        this.cargarAlertas(0);
        this.cargar(this.currentPage());
      },
      error: (err) => {
        this.guardando.set(false);
        this.errorForm.set(err?.error?.detail ?? 'No se pudo reportar el incidente.');
      },
    });
  }

  confirmarEliminar(id: number): void {
    if (confirm('¿Eliminar este incidente?')) {
      this.service.eliminar(id).subscribe({
        next: () => this.cargar(this.currentPage()),
        error: () => alert('No se pudo eliminar el incidente.'),
      });
    }
  }
}
