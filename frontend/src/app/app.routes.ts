import { Routes } from '@angular/router';
import { LoginPageComponent } from './features/login/login-page.component';
import { Error403Component } from './pages/error/error-403.component';
import { Error404Component } from './pages/error/error-404.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { AdminRolesComponent } from './features/admin/roles/admin-roles.component';
import { AdminAuditComponent } from './features/admin/audit/admin-audit.component';
import { SessionListComponent } from './features/admin/sessions/session-list.component';
import { AuthGuard } from './core/auth/auth.guard';
import { RolesGuard } from './core/auth/roles.guard';

export const routes: Routes = [
  { path: 'login', component: LoginPageComponent },
  {
    path: '',
    canActivate: [AuthGuard],
    children: [
      { path: '', component: DashboardComponent },
      {
        path: 'roles',
        component: AdminRolesComponent,
        canActivate: [RolesGuard],
        data: { roles: ['ADMIN'] },
      },
      {
        path: 'audit',
        component: AdminAuditComponent,
        canActivate: [RolesGuard],
        data: { roles: ['ADMIN', 'AUDITOR'] },
      },
      {
        path: 'sessions',
        component: SessionListComponent,
        canActivate: [AuthGuard],
      },
    ],
  },
  { path: '403', component: Error403Component },
  { path: '**', component: Error404Component },
];
