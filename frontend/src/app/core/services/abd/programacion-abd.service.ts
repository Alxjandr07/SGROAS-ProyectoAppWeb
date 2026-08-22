import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { CatalogosAbd, ProgramacionAbd, ProgramacionAbdRequest } from '../../models/abd.model';

@Injectable({ providedIn: 'root' })
export class ProgramacionAbdService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/abd/programaciones`;

  listar(page = 0, size = 50, filtros?: { estado?: string; idConductor?: number; idRuta?: number }): Observable<any> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (filtros?.estado) params = params.set('estado', filtros.estado);
    if (filtros?.idConductor) params = params.set('idConductor', filtros.idConductor);
    if (filtros?.idRuta) params = params.set('idRuta', filtros.idRuta);
    return this.http.get<any>(this.apiUrl, { params });
  }

  crear(data: ProgramacionAbdRequest): Observable<ProgramacionAbd> {
    return this.http.post<ProgramacionAbd>(this.apiUrl, data);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  catalogos(): Observable<CatalogosAbd> {
    return this.http.get<CatalogosAbd>(`${environment.apiUrl}/abd/catalogos`);
  }
}
