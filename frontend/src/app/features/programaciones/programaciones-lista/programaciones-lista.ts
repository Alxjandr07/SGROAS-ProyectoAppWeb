import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { environment } from '../../../../environments/environment';
import { ProgramacionAbdService } from '../../../core/services/abd/programacion-abd.service';
import { RutaAbdService } from '../../../core/services/abd/ruta-abd.service';
import { UnidadAbdService } from '../../../core/services/abd/unidad-abd.service';
import { Paginador } from '../../../shared/components/paginador/paginador';
import { ProgramacionAbd, RutaAbd, UnidadAbd } from '../../../core/models/abd.model';

const ESTADOS = ['Programado', 'En Curso', 'Completado', 'Cancelado'];
const LIMITE_SELECT = 200;

@Component({
  selector: 'app-programaciones-lista',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, Paginador],
  templateUrl: './programaciones-lista.html',
  styleUrl: './programaciones-lista.scss',
})
export class ProgramacionesLista implements OnInit, OnDestroy {
  private service = inject(ProgramacionAbdService);
  private rutaService = inject(RutaAbdService);
  private unidadService = inject(UnidadAbdService);
  private http = inject(HttpClient);
  private fb = inject(FormBuilder);
  private destruir$ = new Subject<void>();
  private buscar$ = new Subject<string>();

  private readonly conductoresUrl = `${environment.apiUrl}/abd/conductores`;

  estados = ESTADOS;
  filtroEstado = signal('');
  filtroConductor = signal<number | null>(null);
  filtroRuta = signal<number | null>(null);
  filtroFechaDesde = signal('');
  filtroFechaHasta = signal('');
  buscar = signal('');

  programaciones = signal<ProgramacionAbd[]>([]);
  rutas = signal<RutaAbd[]>([]);
  unidades = signal<UnidadAbd[]>([]);
  conductores = signal<{ idConductor: number; nombres: string; apellidos: string }[]>([]);

  loading = signal(true);
  guardando = signal(false);
  errorMsg = signal<string | null>(null);
  errorForm = signal<string | null>(null);
  mostrandoFormulario = signal(false);

  totalPages = signal(0);
  totalElements = signal(0);
  currentPage = signal(0);

  form = this.fb.group({
    fecha: ['', Validators.required],
    horaSalida: ['', Validators.required],
    horaEstimadaLlegada: ['', Validators.required],
    estado: ['Programado', Validators.required],
    idRuta: [null as number | null, Validators.required],
    idUnidad: [null as number | null, Validators.required],
    idConductor: [null as number | null, Validators.required],
  });

  ngOnInit(): void {
    this.cargar();
    this.cargarCatalogos();
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
    const filtros = {
      estado: this.filtroEstado() || undefined,
      idConductor: this.filtroConductor() ?? undefined,
      idRuta: this.filtroRuta() ?? undefined,
      fechaDesde: this.filtroFechaDesde() || undefined,
      fechaHasta: this.filtroFechaHasta() || undefined,
    };
    this.service.listar(page, 50, filtros).subscribe({
      next: (res) => {
        this.programaciones.set(res.content);
        this.totalPages.set(res.totalPages);
        this.totalElements.set(res.totalElements);
        this.currentPage.set(res.number);
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set('Error al cargar programaciones.');
        this.loading.set(false);
      },
    });
  }

  private cargarCatalogos(): void {
    this.rutaService.listar(0, LIMITE_SELECT).subscribe({
      next: (res) => this.rutas.set(res.content),
    });
    this.unidadService.listar(0, LIMITE_SELECT).subscribe({
      next: (res) => this.unidades.set(res.content),
    });
    this.http
      .get<any>(`${this.conductoresUrl}?page=0&size=${LIMITE_SELECT}`)
      .subscribe({ next: (res) => this.conductores.set(res.content) });
  }

  aplicarFiltroEstado(event: Event): void {
    this.filtroEstado.set((event.target as HTMLSelectElement).value);
    this.cargar(0);
  }

  aplicarFiltroConductor(event: Event): void {
    const v = (event.target as HTMLSelectElement).value;
    this.filtroConductor.set(v ? Number(v) : null);
    this.cargar(0);
  }

  aplicarFiltroRuta(event: Event): void {
    const v = (event.target as HTMLSelectElement).value;
    this.filtroRuta.set(v ? Number(v) : null);
    this.cargar(0);
  }

  enBuscar(event: Event): void {
    this.buscar.set((event.target as HTMLInputElement).value);
    this.buscar$.next(this.buscar());
  }

  enFechaDesde(event: Event): void {
    this.filtroFechaDesde.set((event.target as HTMLInputElement).value);
    this.cargar(0);
  }

  enFechaHasta(event: Event): void {
    this.filtroFechaHasta.set((event.target as HTMLInputElement).value);
    this.cargar(0);
  }

  cambiarPagina(p: number): void {
    this.cargar(p);
  }

  abrirFormulario(): void {
    this.form.reset({ estado: 'Programado' });
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
      fecha: v.fecha!,
      horaSalida: v.horaSalida!,
      horaEstimadaLlegada: v.horaEstimadaLlegada!,
      estado: v.estado!,
      idRuta: v.idRuta!,
      idUnidad: v.idUnidad!,
      idConductor: v.idConductor!,
    };
    this.guardando.set(true);
    this.errorForm.set(null);
    this.service.crear(data).subscribe({
      next: () => {
        this.guardando.set(false);
        this.cancelar();
        this.cargar(this.currentPage());
      },
      error: (err) => {
        this.guardando.set(false);
        this.errorForm.set(err?.error?.detail ?? 'No se pudo guardar la programación.');
      },
    });
  }

  confirmarEliminar(id: number): void {
    if (confirm('¿Eliminar esta programación?')) {
      this.service.eliminar(id).subscribe({
        next: () => this.cargar(this.currentPage()),
        error: () => alert('No se pudo eliminar la programación.'),
      });
    }
  }
}
