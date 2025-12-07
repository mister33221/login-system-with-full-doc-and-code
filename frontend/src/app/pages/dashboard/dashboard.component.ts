import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <div class="p-6">
      <h1 class="text-xl font-semibold mb-2">Dashboard</h1>
      <p class="text-sm text-slate-700">這裡是登入後的預設頁面，可導向角色、稽核、會話等功能。</p>
    </div>
  `,
})
export class DashboardComponent {}
