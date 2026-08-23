import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { UnidadAbd, UnidadAbdRequest } from '../../models/abd.model';

@Injectable({ providedIn: 'root' })
export class UnidadAbdService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/abd/unidades`;

  listar(page = 0, size = 50, estado = '', search = ''): Observable<any> {
    const params = new URLSearchParams({ page: String(page), size: String(size) });
    if (estado) params.set('estado', estado);
    if (search) params.set('search', search);
    return this.http.get<any>(`${this.apiUrl}?${params}`);
  }

  buscarPorId(id: number): Observable<UnidadAbd> {
    return this.http.get<UnidadAbd>(`${this.apiUrl}/${id}`);
  }

  crear(data: UnidadAbdRequest): Observable<UnidadAbd> {
    return this.http.post<UnidadAbd>(this.apiUrl, data);
  }

  actualizar(id: number, data: UnidadAbdRequest): Observable<UnidadAbd> {
    return this.http.put<UnidadAbd>(`${this.apiUrl}/${id}`, data);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
