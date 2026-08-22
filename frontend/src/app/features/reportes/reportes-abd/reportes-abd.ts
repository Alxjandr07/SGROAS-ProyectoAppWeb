import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReporteAbdService } from '../../../core/services/abd/reporte-abd.service';
import { ConteoAbd, ResumenAbd } from '../../../core/models/abd.model';

@Component({
  selector: 'app-reportes-abd',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './reportes-abd.html',
  styleUrl: './reportes-abd.scss',
})
export class ReportesAbd implements OnInit {
  private service = inject(ReporteAbdService);

  resumen = signal<ResumenAbd | null>(null);
  porNivel = signal<ConteoAbd[]>([]);
  porEstado = signal<ConteoAbd[]>([]);
  loading = signal(true);
  errorMsg = signal<string | null>(null);

  tarjetas = computed(() => {
    const r = this.resumen();
    if (!r) return [];
    return [
      { etiqueta: 'Programaciones totales', valor: r.totalProgramaciones, destacar: false },
      { etiqueta: 'Programaciones activas', valor: r.programacionesActivas, destacar: false },
      { etiqueta: 'Rutas registradas', valor: r.totalRutas, destacar: false },
      { etiqueta: 'Unidades en flota', valor: r.totalUnidades, destacar: false },
      { etiqueta: 'Unidades en mantenimiento', valor: r.unidadesEnMantenimiento, destacar: r.unidadesEnMantenimiento > 0 },
      { etiqueta: 'Incidentes reportados', valor: r.totalIncidentes, destacar: false },
      { etiqueta: 'Incidentes de nivel ALTO', valor: r.incidentesAltoNivel, destacar: r.incidentesAltoNivel > 0 },
      { etiqueta: 'Alertas generadas', valor: r.totalAlertas, destacar: r.totalAlertas > 0 },
    ];
  });

  ngOnInit(): void {
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
    this.service.incidentesPorEstado().subscribe({ next: (c) => this.porEstado.set(c) });
  }
}
