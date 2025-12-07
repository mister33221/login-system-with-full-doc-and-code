import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-md mx-auto mt-16 bg-white shadow p-8 rounded space-y-4">
      <h1 class="text-xl font-semibold">Login</h1>
      <form (ngSubmit)="login()" class="space-y-3">
        <div>
          <label class="block text-sm font-medium mb-1">Username</label>
          <input
            class="w-full border rounded px-3 py-2"
            name="username"
            [(ngModel)]="username"
            required
          />
        </div>
        <div>
          <label class="block text-sm font-medium mb-1">Password</label>
          <input
            class="w-full border rounded px-3 py-2"
            type="password"
            name="password"
            [(ngModel)]="password"
            required
          />
        </div>
        <div class="flex items-center justify-between">
          <button class="bg-slate-900 text-white px-4 py-2 rounded" type="submit">Login</button>
          <span class="text-sm text-red-500" *ngIf="error">{{ error }}</span>
        </div>
      </form>
    </div>
  `,
})
export class LoginPageComponent {
  username = '';
  password = '';
  error = '';

  constructor(private readonly router: Router) {}

  login() {
    // TODO: call backend; for now, mock success and store dummy token/role
    if (!this.username || !this.password) {
      this.error = '請輸入帳號與密碼';
      return;
    }
    localStorage.setItem('access_token', 'demo-token');
    localStorage.setItem('roles', JSON.stringify(['ADMIN']));
    this.error = '';
    this.router.navigate(['/']);
  }
}
