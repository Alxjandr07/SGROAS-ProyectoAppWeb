import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { CatalogosAbd } from '../../models/abd.model';

@Injectable({ providedIn: 'root' })
export class CatalogoAbdService {
  private http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/abd/catalogos`;

  obtener(): Observable<CatalogosAbd> {
    return this.http.get<CatalogosAbd>(this.apiUrl);
  }
}
