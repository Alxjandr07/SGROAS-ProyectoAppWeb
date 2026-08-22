import { Component, computed, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';

const MAX_BOTONES = 5;

@Component({
  selector: 'app-paginador',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './paginador.html',
  styleUrl: './paginador.scss',
})
export class Paginador {
  currentPage = input.required<number>();
  totalPages = input.required<number>();
  totalElements = input<number>(0);

  pageChange = output<number>();

  paginasVisibles = computed<number[]>(() => {
    const total = this.totalPages();
    const actual = this.currentPage();
    if (total <= MAX_BOTONES) {
      return Array.from({ length: Math.max(total, 1) }, (_, i) => i);
    }
    let inicio = Math.max(0, actual - Math.floor(MAX_BOTONES / 2));
    const fin = Math.min(total, inicio + MAX_BOTONES);
    inicio = Math.max(0, fin - MAX_BOTONES);
    return Array.from({ length: fin - inicio }, (_, i) => inicio + i);
  });

  irA(pagina: number): void {
    if (pagina >= 0 && pagina < this.totalPages() && pagina !== this.currentPage()) {
      this.pageChange.emit(pagina);
    }
  }
}
