import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReporteAbdService } from '../../../core/services/abd/reporte-abd.service';
import { ConteoAbd, ItemGrafico, ResumenAbd, TopRutaAbd } from '../../../core/models/abd.model';
import { Barras } from '../../../shared/components/graficos/barras/barras';
import { Anillo } from '../../../shared/components/graficos/anillo/anillo';
import { Columnas } from '../../../shared/components/graficos/columnas/columnas';
import { ReportePersonalizado } from '../reporte-personalizado/reporte-personalizado';

const COLORES_ESTADO_PROG: Record<string, string> = {
  'Programado': '#3b82f6',
  'En Curso': '#f59e0b',
  'Completado': '#10b981',
  'Cancelado': '#ef4444',
};

const COLORES_NIVEL: Record<string, string> = {
  'BAJO': '#10b981',
  'MEDIO': '#f59e0b',
  'ALTO': '#ef4444',
};

const COLORES_INCIDENTE_ESTADO: Record<string, string> = {
  'Reportado': '#3b82f6',
  'En Revision': '#f59e0b',
  'Cerrado': '#94a3b8',
};

const COLORES_UNIDAD: Record<string, string> = {
  'Activo': '#10b981',
  'Inactivo': '#ef4444',
  'En Mantenimiento': '#f59e0b',
  'Inactivo ': '#ef4444',
};

const MESES = ['ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic'];

@Component({
  selector: 'app-reportes-abd',
  standalone: true,
  imports: [CommonModule, Barras, Anillo, Columnas, ReportePersonalizado],
  templateUrl: './reportes-abd.html',
  styleUrl: './reportes-abd.scss',
})
export class ReportesAbd implements OnInit {
  private service = inject(ReporteAbdService);

  resumen = signal<ResumenAbd | null>(null);
  porNivel = signal<ConteoAbd[]>([]);
  porEstadoIncidente = signal<ConteoAbd[]>([]);
  unidadesEstado = signal<ConteoAbd[]>([]);
  programacionesEstado = signal<ConteoAbd[]>([]);
  programacionesMes = signal<ConteoAbd[]>([]);
  topRutas = signal<TopRutaAbd[]>([]);
  loading = signal(true);
  errorMsg = signal<string | null>(null);

  tarjetas = computed(() => {
    const r = this.resumen();
    if (!r) return [];
    return [
      { etiqueta: 'Programaciones', valor: r.totalProgramaciones, color: '#2563eb', pie: `${r.programacionesActivas.toLocaleString('es')} activas`, destacar: false },
      { etiqueta: 'Rutas', valor: r.totalRutas, color: '#0ea5e9', pie: 'registradas', destacar: false },
      { etiqueta: 'Unidades', valor: r.totalUnidades, color: '#8b5cf6', pie: `${r.unidadesEnMantenimiento} en mantenimiento`, destacar: false },
      { etiqueta: 'Incidentes', valor: r.totalIncidentes, color: '#f97316', pie: 'reportados', destacar: false },
      { etiqueta: 'Nivel ALTO', valor: r.incidentesAltoNivel, color: '#ef4444', pie: 'requieren atencion', destacar: true },
      { etiqueta: 'Alertas', valor: r.totalAlertas, color: '#e11d48', pie: 'automaticas', destacar: true },
      { etiqueta: 'Programadas hoy', valor: this.programacionesHoy(), color: '#3b82f6', pie: 'estado Programado', destacar: false },
      { etiqueta: 'Completadas', valor: this.completadas(), color: '#10b981', pie: 'histórico', destacar: false },
    ];
  });

  private programacionesHoy(): number {
    return this.programacionesEstado().find((c) => c.clave === 'Programado')?.total ?? 0;
  }

  private completadas(): number {
    return this.programacionesEstado().find((c) => c.clave === 'Completado')?.total ?? 0;
  }

  private aItems(conteos: ConteoAbd[], colores?: Record<string, string>): ItemGrafico[] {
    return conteos.map((c) => ({ etiqueta: c.clave, valor: c.total, color: colores?.[c.clave] }));
  }

  programacionesEstadoGrafico = computed(() => this.aItems(this.programacionesEstado(), COLORES_ESTADO_PROG));
  incidentesNivelGrafico = computed(() => this.ordenarNivel(this.aItems(this.porNivel(), COLORES_NIVEL)));
  incidentesEstadoGrafico = computed(() => this.aItems(this.porEstadoIncidente(), COLORES_INCIDENTE_ESTADO));
  unidadesEstadoGrafico = computed(() => this.aItems(this.unidadesEstado(), COLORES_UNIDAD));

  programacionesMesGrafico = computed<ItemGrafico[]>(() =>
    this.programacionesMes().map((c) => ({
      etiqueta: this.etiquetaMes(c.clave),
      valor: c.total,
      color: '#2563eb',
    }))
  );

  topRutasGrafico = computed<ItemGrafico[]>(() =>
    this.topRutas().map((t, i) => ({
      etiqueta: t.descripcion,
      valor: t.totalProgramaciones,
      color: ['#2563eb', '#0ea5e9', '#8b5cf6', '#f59e0b', '#10b981'][i % 5],
    }))
  );

  private ordenarNivel(items: ItemGrafico[]): ItemGrafico[] {
    const orden = { BAJO: 0, MEDIO: 1, ALTO: 2 } as Record<string, number>;
    return [...items].sort((a, b) => (orden[a.etiqueta] ?? 9) - (orden[b.etiqueta] ?? 9));
  }

  private etiquetaMes(clave: string): string {
    const [anio, mes] = clave.split('-');
    const idx = Number(mes) - 1;
    const nombre = MESES[idx] ?? mes;
    return `${nombre} ${anio.slice(2)}`;
  }

  ngOnInit(): void {
    this.recargar();
  }

  recargar(): void {
    this.loading.set(true);
    this.errorMsg.set(null);
    this.service.resumen().subscribe({
      next: (r) => {
        this.resumen.set(r);
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set('Error al calcular indicadores.');
        this.loading.set(false);
      },
    });
    this.service.incidentesPorNivel().subscribe({ next: (c) => this.porNivel.set(c) });
    this.service.incidentesPorEstado().subscribe({ next: (c) => this.porEstadoIncidente.set(c) });
    this.service.unidadesPorEstado().subscribe({ next: (c) => this.unidadesEstado.set(c) });
    this.service.programacionesPorEstado().subscribe({ next: (c) => this.programacionesEstado.set(c) });
    this.service.programacionesPorMes().subscribe({ next: (c) => this.programacionesMes.set(c) });
    this.service.topRutas().subscribe({ next: (c) => this.topRutas.set(c) });
  }
}
