import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { ConteoAbd, ResumenAbd } from '../../models/abd.model';

@Injectable({ providedIn: 'root' })
export class ReporteAbdService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/abd/reportes`;

  resumen(): Observable<ResumenAbd> {
    return this.http.get<ResumenAbd>(`${this.apiUrl}/resumen`);
  }

  incidentesPorNivel(): Observable<ConteoAbd[]> {
    return this.http.get<ConteoAbd[]>(`${this.apiUrl}/incidentes-por-nivel`);
  }

  incidentesPorEstado(): Observable<ConteoAbd[]> {
    return this.http.get<ConteoAbd[]>(`${this.apiUrl}/incidentes-por-estado`);
  }
}
