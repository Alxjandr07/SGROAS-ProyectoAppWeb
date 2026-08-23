import { HostListener, Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
interface ColumnaReporte {
  clave: string;
  etiqueta: string;
}

interface TipoReporte {
  valor: string;
  etiqueta: string;
  columnas: ColumnaReporte[];
}

@Component({
  selector: 'app-reporte-personalizado',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reporte-personalizado.html',
  styleUrl: './reporte-personalizado.scss',
})
export class ReportePersonalizado {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/abd`;

  @HostListener('document:keydown.escape')
  alEscape(): void {
    if (this.abierto()) this.abierto.set(false);
  }

  readonly tipos: TipoReporte[] = [    {
      valor: 'programaciones', etiqueta: 'Programaciones',
      columnas: [
        { clave: 'idProgramacion', etiqueta: 'ID' },
        { clave: 'fecha', etiqueta: 'Fecha' },
        { clave: 'horaSalida', etiqueta: 'Salida' },
        { clave: 'rutaDescripcion', etiqueta: 'Ruta' },
        { clave: 'unidadPlaca', etiqueta: 'Unidad' },
        { clave: 'conductorNombres', etiqueta: 'Conductor' },
        { clave: 'estado', etiqueta: 'Estado' },
      ],
    },
    {
      valor: 'rutas', etiqueta: 'Rutas',
      columnas: [
        { clave: 'idRuta', etiqueta: 'ID' },
        { clave: 'terminalOrigen', etiqueta: 'Origen' },
        { clave: 'terminalDestino', etiqueta: 'Destino' },
        { clave: 'precioPasaje', etiqueta: 'Precio ($)' },
        { clave: 'totalProgramaciones', etiqueta: 'Programaciones' },
      ],
    },
    {
      valor: 'unidades', etiqueta: 'Unidades',
      columnas: [
        { clave: 'idUnidad', etiqueta: 'ID' },
        { clave: 'placa', etiqueta: 'Placa' },
        { clave: 'numeroDisco', etiqueta: 'Disco' },
        { clave: 'modelo', etiqueta: 'Modelo' },
        { clave: 'capacidad', etiqueta: 'Capacidad' },
        { clave: 'anioFabricacion', etiqueta: 'Año' },
        { clave: 'estado', etiqueta: 'Estado' },
      ],
    },
    {
      valor: 'incidentes', etiqueta: 'Incidentes',
      columnas: [
        { clave: 'idIncidente', etiqueta: 'ID' },
        { clave: 'fechaIncidente', etiqueta: 'Fecha' },
        { clave: 'tipo', etiqueta: 'Tipo' },
        { clave: 'nivelSugerido', etiqueta: 'Nivel' },
        { clave: 'unidadPlaca', etiqueta: 'Unidad' },
        { clave: 'estado', etiqueta: 'Estado' },
      ],
    },
    {
      valor: 'conductores', etiqueta: 'Conductores (Flota)',
      columnas: [
        { clave: 'cedula', etiqueta: 'Cédula' },
        { clave: 'nombres', etiqueta: 'Nombres' },
        { clave: 'apellidos', etiqueta: 'Apellidos' },
        { clave: 'numeroLicencia', etiqueta: 'Licencia' },
        { clave: 'telefono', etiqueta: 'Teléfono' },
        { clave: 'estado', etiqueta: 'Estado' },
      ],
    },
  ];

  abierto = signal(false);
  tipo = signal('programaciones');
  filtroTexto = signal('');
  filtroEstado = signal('');
  filtroNivel = signal('');
  filtroDesde = signal('');
  filtroHasta = signal('');

  generando = signal(false);
  busco = signal(false);
  error = signal<string | null>(null);
  filas = signal<Record<string, unknown>[]>([]);
  totalGeneral = signal<number | null>(null);

  estadosProgramacion = ['Programado', 'En Curso', 'Completado', 'Cancelado'];
  estadosIncidente = ['Reportado', 'En Revision', 'Cerrado'];
  niveles = ['BAJO', 'MEDIO', 'ALTO'];
  estadosUnidad = ['Activo', 'Inactivo', 'En Mantenimiento'];

  tipoActual = computed(() => this.tipos.find((t) => t.valor === this.tipo()) ?? this.tipos[0]);
  columnas = computed(() => this.tipoActual().columnas);

  alternar(): void {
    this.abierto.set(!this.abierto());
  }

  cambiarTipo(ev: Event): void {
    this.tipo.set((ev.target as HTMLSelectElement).value);
    this.limpiarFiltros();
    this.filas.set([]);
    this.totalGeneral.set(null);
    this.busco.set(false);
    this.error.set(null);
  }

  private limpiarFiltros(): void {
    this.filtroTexto.set('');
    this.filtroEstado.set('');
    this.filtroNivel.set('');
    this.filtroDesde.set('');
    this.filtroHasta.set('');
  }

  limpiar(): void {
    this.limpiarFiltros();
    this.filas.set([]);
    this.totalGeneral.set(null);
    this.busco.set(false);
    this.error.set(null);
  }

  generar(): void {
    this.generando.set(true);
    this.error.set(null);

    let params = new HttpParams().set('page', '0').set('size', '100');
    const t = this.tipo();

    if (t === 'programaciones') {
      if (this.filtroEstado()) params = params.set('estado', this.filtroEstado());
      if (this.filtroDesde()) params = params.set('fechaDesde', this.filtroDesde());
      if (this.filtroHasta()) params = params.set('fechaHasta', this.filtroHasta());
    } else if (t === 'unidades') {
      if (this.filtroEstado()) params = params.set('estado', this.filtroEstado());
      if (this.filtroTexto()) params = params.set('search', this.filtroTexto());
    } else if (t === 'incidentes') {
      if (this.filtroEstado()) params = params.set('estado', this.filtroEstado());
      if (this.filtroNivel()) params = params.set('nivel', this.filtroNivel());
    } else if (t === 'rutas') {
      if (this.filtroTexto()) params = params.set('search', this.filtroTexto());
    } else if (t === 'conductores') {
      if (this.filtroTexto()) params = params.set('search', this.filtroTexto());
    }

    const url =
      t === 'conductores' ? `${environment.apiUrl}/conductores`
        : t === 'rutas' ? `${this.apiUrl}/rutas`
        : `${this.apiUrl}/${t}`;

    this.http.get<any>(url, { params }).subscribe({
      next: (res) => {
        this.filas.set(res?.content ?? []);
        this.totalGeneral.set(typeof res?.totalElements === 'number' ? res.totalElements : null);
        this.busco.set(true);
        this.generando.set(false);
      },
      error: () => {
        this.error.set('No se pudo generar el reporte. Intenta de nuevo.');
        this.generando.set(false);
      },
    });
  }

  valorFila(fila: Record<string, unknown>, clave: string): string {
    const v = fila[clave];
    return v === null || v === undefined || v === '' ? '—' : String(v);
  }

  private nombreArchivo(extension: string): string {
    const fecha = new Date().toISOString().slice(0, 10);
    return `sgroas-reporte-${this.tipoActual().etiqueta.toLowerCase().replace(/[^a-z]+/g, '-')}-${fecha}.${extension}`;
  }

  exportarCsv(): void {
    const cols = this.columnas();
    const esc = (v: string) => `"${v.replace(/"/g, '""')}"`;
    const lineas = [
      cols.map((c) => esc(c.etiqueta)).join(';'),
      ...this.filas().map((f) => cols.map((c) => esc(this.valorFila(f, c.clave))).join(';')),
    ];
    const blob = new Blob(['\uFEFF' + lineas.join('\r\n')], { type: 'text/csv;charset=utf-8;' });
    const enlace = document.createElement('a');
    enlace.href = URL.createObjectURL(blob);
    enlace.download = this.nombreArchivo('csv');
    enlace.click();
    URL.revokeObjectURL(enlace.href);
  }

  imprimir(): void {
    const cols = this.columnas();
    const celdas = this.filas()
      .map((f) => `<tr>${cols.map((c) => `<td>${this.valorFila(f, c.clave)}</td>`).join('')}</tr>`)
      .join('');
    const win = window.open('', '_blank', 'width=980,height=700');
    if (!win) return;
    win.document.write(`<!DOCTYPE html><html><head><title>${this.nombreArchivo('pdf').replace('.pdf', '')}</title>
      <style>
        body { font-family: Arial, Helvetica, sans-serif; margin: 24px; color: #111827; }
        h1 { font-size: 18px; margin: 0 0 4px; }
        p.sub { color: #6b7280; font-size: 12px; margin: 0 0 16px; }
        table { width: 100%; border-collapse: collapse; font-size: 12px; }
        th { background: #1f2937; color: #fff; text-align: left; padding: 6px 8px; text-transform: uppercase; font-size: 10px; letter-spacing: .05em; }
        td { padding: 6px 8px; border-bottom: 1px solid #e5e7eb; }
        tr:nth-child(even) td { background: #f9fafb; }
      </style></head><body>
      <h1>SGROAS — Reporte de ${this.tipoActual().etiqueta}</h1>
      <p class="sub">${this.filas().length} registros · generado el ${new Date().toLocaleString('es-EC')}</p>
      <table><thead><tr>${cols.map((c) => `<th>${c.etiqueta}</th>`).join('')}</tr></thead><tbody>${celdas}</tbody></table>
      </body></html>`);
    win.document.close();
    win.focus();
    win.print();
  }
}
