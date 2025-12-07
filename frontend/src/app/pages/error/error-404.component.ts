import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-error-404',
  standalone: true,
  template: `
    <div class="max-w-lg mx-auto mt-16 bg-white shadow p-8 rounded space-y-3 text-center">
      <h1 class="text-2xl font-semibold">404 - 找不到頁面</h1>
      <p>該資源不存在或已移除。</p>
      <div>
        <button class="bg-slate-900 text-white px-4 py-2 rounded" (click)="goHome()">返回首頁</button>
      </div>
    </div>
  `,
})
export class Error404Component {
  constructor(private readonly router: Router) {}
  goHome() {
    this.router.navigate(['/']);
  }
}
