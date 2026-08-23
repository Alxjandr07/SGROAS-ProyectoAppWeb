import { Component, computed, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ItemGrafico } from '../../../../core/models/abd.model';

const RADIO = 54;
const CIRCUNFERENCIA = 2 * Math.PI * RADIO;

@Component({
  selector: 'app-anillo',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './anillo.html',
  styleUrl: './anillo.scss',
})
export class Anillo {
  datos = input.required<ItemGrafico[]>();
  titulo = input('');

  readonly circunferencia = CIRCUNFERENCIA;

  total = computed(() => this.datos().reduce((acc, d) => acc + d.valor, 0));

  segmentos = computed(() => {
    const total = this.total() || 1;
    let acumulado = 0;
    return this.datos().map((d) => {
      const arco = (d.valor / total) * CIRCUNFERENCIA;
      const seg = { ...d, arco, inicio: acumulado };
      acumulado += arco;
      return seg;
    });
  });

  colorDe(item: ItemGrafico): string {
    return item.color ?? '#2563eb';
  }
}
