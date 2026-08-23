import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ReporteAbdService } from '../../../core/services/abd/reporte-abd.service';
import { Auth } from '../../../core/services/auth';
import { ConteoAbd, ResumenAbd } from '../../../core/models/abd.model';

interface SummaryCard {
  label: string;
  value: number;
  trend: string;
  alerta?: boolean;
}

interface ModuleCard {
  icon: string;
  title: string;
  description: string;
  path: string;
  roles: string[] | null;
}

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './overview.html',
  styleUrl: './overview.scss'
})
export class Overview implements OnInit {
  private reportes = inject(ReporteAbdService);
  private authService = inject(Auth);

  resumen = signal<ResumenAbd | null>(null);
  unidadesEstado = signal<ConteoAbd[]>([]);
  incidentesEstado = signal<ConteoAbd[]>([]);
  loading = signal(true);
  errorMsg = signal<string | null>(null);

  private conteo(conteos: ConteoAbd[], clave: string): number {
    return conteos.find((c) => c.clave === clave)?.total ?? 0;
  }

  summary = computed<SummaryCard[]>(() => {
    const r = this.resumen();
    if (!r) return [];
    const activas = this.conteo(this.unidadesEstado(), 'Activo');
    const abiertos =
      this.conteo(this.incidentesEstado(), 'Reportado') +
      this.conteo(this.incidentesEstado(), 'En Revision');
    return [
      {
        label: 'Unidades activas',
        value: activas,
        trend: `de ${r.totalUnidades.toLocaleString('es')} registradas`,
      },
      {
        label: 'Rutas registradas',
        value: r.totalRutas,
        trend: 'operativas en el sistema',
      },
      {
        label: 'Programaciones activas',
        value: r.programacionesActivas,
        trend: `de ${r.totalProgramaciones.toLocaleString('es')} históricas`,
      },
      {
        label: 'Incidentes abiertos',
        value: abiertos,
        trend: `${r.incidentesAltoNivel.toLocaleString('es')} de nivel ALTO`,
        alerta: r.incidentesAltoNivel > 0,
      },
      {
        label: 'Alertas automáticas',
        value: r.totalAlertas,
        trend: 'generadas por incidentes ALTO',
        alerta: r.totalAlertas > 0,
      },
      {
        label: 'En mantenimiento',
        value: r.unidadesEnMantenimiento,
        trend: 'unidades en taller',
      },
    ];
  });

  private readonly todosLosModules: ModuleCard[] = [
    { icon: '👤', title: 'Usuarios y Roles', description: 'Administra socios, choferes y permisos de acceso.', path: 'usuarios', roles: ['ADMIN'] },
    { icon: '🚌', title: 'Flota Vehicular', description: 'Control de unidades, placas y mantenimiento.', path: 'flota', roles: ['ADMIN', 'COORDINADOR'] },
    { icon: '🛣️', title: 'Rutas y Frecuencias', description: 'Asignación de horarios y recorridos por unidad.', path: 'rutas', roles: ['ADMIN', 'COORDINADOR'] },
    { icon: '🛡️', title: 'Seguridad', description: 'Monitoreo, incidentes y alertas en tiempo real.', path: 'seguridad', roles: ['ADMIN', 'SEGURIDAD'] },
    { icon: '📅', title: 'Programaciones', description: 'Salidas diarias por ruta, unidad y conductor.', path: 'programaciones', roles: ['ADMIN', 'COORDINADOR', 'OPERADOR'] },
    { icon: '📊', title: 'Reportes', description: 'Estadísticas operativas y de seguridad exportables.', path: 'reportes', roles: null }
  ];

  get modules(): ModuleCard[] {
    return this.todosLosModules.filter(
      (m) => m.roles === null || this.authService.tieneRol(m.roles)
    );
  }

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.loading.set(true);
    this.errorMsg.set(null);
    this.reportes.resumen().subscribe({
      next: (r) => {
        this.resumen.set(r);
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set('No se pudieron cargar los indicadores.');
        this.loading.set(false);
      },
    });
    this.reportes.unidadesPorEstado().subscribe({ next: (c) => this.unidadesEstado.set(c) });
    this.reportes.incidentesPorEstado().subscribe({ next: (c) => this.incidentesEstado.set(c) });
  }
}
