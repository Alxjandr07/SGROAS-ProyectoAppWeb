import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { Shell } from './features/dashboard/shell/shell';
import { Overview } from './features/dashboard/overview/overview';
import { ConductorLista } from './features/conductores/lista/conductor-lista';
import { ConductorFormulario } from './features/conductores/formulario/conductor-formulario';
import { UsuarioLista } from './features/usuarios/lista/usuario-lista';
import { UsuarioFormulario } from './features/usuarios/formulario/usuario-formulario';
import { RutasLista } from './features/rutas/rutas-lista/rutas-lista';
import { UnidadesLista } from './features/unidades/unidades-lista/unidades-lista';
import { ProgramacionesLista } from './features/programaciones/programaciones-lista/programaciones-lista';
import { IncidentesLista } from './features/seguridad/incidentes/incidentes-lista';
import { ReportesAbd } from './features/reportes/reportes-abd/reportes-abd';
import { AdminPlaceholder } from './features/administracion/admin-placeholder';
import { authGuard, roleGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },
  {
    path: 'dashboard',
    component: Shell,
    canActivate: [authGuard],
    children: [
      { path: '', component: Overview },
      {
        path: 'usuarios',
        data: { roles: ['ADMIN'] },
        canActivate: [roleGuard],
        children: [
          { path: '', component: UsuarioLista },
          { path: 'nuevo', component: UsuarioFormulario },
          { path: 'editar/:id', component: UsuarioFormulario },
        ],
      },
      {
        path: 'flota',
        data: { roles: ['ADMIN', 'COORDINADOR'] },
        canActivate: [roleGuard],
        children: [
          { path: '', component: ConductorLista },
          { path: 'nuevo', component: ConductorFormulario },
          { path: 'editar/:id', component: ConductorFormulario },
        ],
      },
      {
        path: 'unidades',
        data: { roles: ['ADMIN', 'COORDINADOR'] },
        canActivate: [roleGuard],
        component: UnidadesLista,
      },
      {
        path: 'rutas',
        data: { roles: ['ADMIN', 'COORDINADOR'] },
        canActivate: [roleGuard],
        component: RutasLista,
      },
      {
        path: 'programaciones',
        data: { roles: ['ADMIN', 'COORDINADOR', 'OPERADOR'] },
        canActivate: [roleGuard],
        component: ProgramacionesLista,
      },
      {
        path: 'seguridad',
        data: { roles: ['ADMIN', 'SEGURIDAD'] },
        canActivate: [roleGuard],
        component: IncidentesLista,
      },
      { path: 'reportes', component: ReportesAbd },
      { path: 'administracion', component: AdminPlaceholder },
    ],
  },
];
