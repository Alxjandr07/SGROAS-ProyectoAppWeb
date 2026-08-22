import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { RutaAbd, RutaAbdRequest } from '../../models/abd.model';

@Injectable({ providedIn: 'root' })
export class RutaAbdService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/abd/rutas`;

  listar(page = 0, size = 50): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}?page=${page}&size=${size}`);
  }

  buscarPorId(id: number): Observable<RutaAbd> {
    return this.http.get<RutaAbd>(`${this.apiUrl}/${id}`);
  }

  crear(data: RutaAbdRequest): Observable<RutaAbd> {
    return this.http.post<RutaAbd>(this.apiUrl, data);
  }

  actualizar(id: number, data: RutaAbdRequest): Observable<RutaAbd> {
    return this.http.put<RutaAbd>(`${this.apiUrl}/${id}`, data);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
