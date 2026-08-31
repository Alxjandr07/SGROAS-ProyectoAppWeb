import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { IncidenteAbd, IncidenteAbdRequest } from '../../models/abd.model';

@Injectable({ providedIn: 'root' })
export class IncidenteAbdService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/abd/incidentes`;

  listar(page = 0, size = 50, filtros?: { estado?: string; nivel?: string; search?: string }): Observable<any> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (filtros?.estado) params = params.set('estado', filtros.estado);
    if (filtros?.nivel) params = params.set('nivel', filtros.nivel);
    if (filtros?.search) params = params.set('search', filtros.search);
    return this.http.get<any>(this.apiUrl, { params });
  }

  crear(data: IncidenteAbdRequest): Observable<IncidenteAbd> {
    return this.http.post<IncidenteAbd>(this.apiUrl, data);
  }

  actualizar(id: number, data: IncidenteAbdRequest): Observable<IncidenteAbd> {
    return this.http.put<IncidenteAbd>(`${this.apiUrl}/${id}`, data);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
