import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AlertaAbd } from '../../models/abd.model';

@Injectable({ providedIn: 'root' })
export class AlertaAbdService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/abd/alertas`;

  ultimas(cantidad = 8): Observable<any> {
    const params = new HttpParams()
      .set('page', 0)
      .set('size', cantidad)
      .set('sort', 'idAlerta,desc');
    return this.http.get<any>(this.apiUrl, { params });
  }
}
