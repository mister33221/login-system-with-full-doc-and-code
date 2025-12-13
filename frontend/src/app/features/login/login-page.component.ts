import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

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

  constructor(private readonly router: Router, private readonly auth: AuthService) {}

  login() {
    if (!this.username || !this.password) {
      this.error = '請輸入帳號與密碼';
      return;
    }
    this.auth.login({ username: this.username, password: this.password }).subscribe({
      next: () => {
        this.error = '';
        this.router.navigate(['/']);
      },
      error: (err) => {
        const backendMsg: string | undefined = err?.error?.message;
        const fieldErrors = err?.error?.data as Record<string, string> | undefined;
        if (err.status === 401) {
          this.error = fieldErrors?.['password'] || backendMsg || '帳號或密碼錯誤';
        } else if (err.status === 423) {
          this.error = backendMsg ?? '帳號已鎖定';
        } else {
          this.error =
            fieldErrors?.['password'] ||
            fieldErrors?.['username'] ||
            backendMsg ||
            '系統發生錯誤，請稍後再試';
        }
      },
    });
  }
}
