import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-error-403',
  standalone: true,
  template: `
    <div class="max-w-lg mx-auto mt-16 bg-white shadow p-8 rounded space-y-3 text-center">
      <h1 class="text-2xl font-semibold">403 - 權限不足</h1>
      <p>您沒有存取這個頁面的權限。</p>
      <div class="space-x-3">
        <button class="bg-slate-900 text-white px-4 py-2 rounded" (click)="goHome()">返回首頁</button>
        <button class="bg-slate-200 text-slate-900 px-4 py-2 rounded" (click)="logout()">登出</button>
      </div>
    </div>
  `,
})
export class Error403Component {
  constructor(private readonly router: Router) {}
  goHome() {
    this.router.navigate(['/']);
  }
  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}
