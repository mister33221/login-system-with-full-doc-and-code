import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <div class="p-6">
      <h1 class="text-xl font-semibold mb-2">Dashboard</h1>
      <p class="text-sm text-slate-700">登入後可存取角色權限、稽核與工作階段功能。</p>
    </div>
  `,
})
export class DashboardComponent {}
