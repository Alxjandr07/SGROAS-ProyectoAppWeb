import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Conductor, ConductorRequest } from '../models/conductor.model';

@Injectable({ providedIn: 'root' })
export class ConductorService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/conductores`;

  listar(page = 0, size = 10, search = ''): Observable<any> {
    const params = new URLSearchParams({ page: String(page), size: String(size) });
    if (search) params.set('search', search);
    return this.http.get<any>(`${this.apiUrl}?${params}`);
  }

  buscarPorId(id: number): Observable<Conductor> {
    return this.http.get<Conductor>(`${this.apiUrl}/${id}`);
  }

  crear(data: ConductorRequest): Observable<Conductor> {
    return this.http.post<Conductor>(this.apiUrl, data);
  }

  actualizar(id: number, data: ConductorRequest): Observable<Conductor> {
    return this.http.put<Conductor>(`${this.apiUrl}/${id}`, data);
  }

  desactivar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
