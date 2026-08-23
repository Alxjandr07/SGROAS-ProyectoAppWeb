import { Component, computed, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ItemGrafico } from '../../../../core/models/abd.model';

@Component({
  selector: 'app-barras',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './barras.html',
  styleUrl: './barras.scss',
})
export class Barras {
  items = input.required<ItemGrafico[]>();

  private readonly maximo = computed(() =>
    Math.max(1, ...this.items().map((i) => i.valor))
  );

  porcentaje(valor: number): number {
    return Math.max(2, Math.round((valor / this.maximo()) * 100));
  }

  colorDe(item: ItemGrafico): string {
    return item.color ?? '#2563eb';
  }
}
