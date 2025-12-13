import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-register-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-md mx-auto mt-16 bg-white shadow p-8 rounded space-y-4">
      <h1 class="text-xl font-semibold">Register</h1>
      <form (ngSubmit)="register()" class="space-y-3">
        <div>
          <label class="block text-sm font-medium mb-1">Username</label>
          <input class="w-full border rounded px-3 py-2" name="username" [(ngModel)]="username" required />
        </div>
        <div>
          <label class="block text-sm font-medium mb-1">Email</label>
          <input class="w-full border rounded px-3 py-2" name="email" [(ngModel)]="email" />
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
        <div>
          <label class="block text-sm font-medium mb-1">角色</label>
          <select class="w-full border rounded px-3 py-2" name="role" [(ngModel)]="role">
            <option value="USER">USER</option>
            <option value="ADMIN">ADMIN</option>
            <option value="AUDITOR">AUDITOR</option>
          </select>
        </div>
        <div class="flex items-center justify-between">
          <button class="bg-slate-900 text-white px-4 py-2 rounded" type="submit">Register</button>
          <span class="text-sm text-red-500" *ngIf="error">{{ error }}</span>
        </div>
      </form>
    </div>
  `,
})
export class RegisterPageComponent {
  username = '';
  email = '';
  password = '';
  role = 'USER';
  error = '';

  constructor(private readonly router: Router, private readonly auth: AuthService) {}

  register() {
    if (!this.username || !this.password) {
      this.error = '請輸入帳號與密碼';
      return;
    }
    this.auth
      .register({
        username: this.username,
        email: this.email,
        password: this.password,
        roleCodes: [this.role],
      })
      .subscribe({
        next: () => {
          this.error = '';
          this.router.navigate(['/']);
        },
        error: (err) => {
          const backendMsg: string | undefined = err?.error?.message;
          const fieldErrors = err?.error?.data as Record<string, string> | undefined;
          if (err.status === 409) {
            this.error = backendMsg ?? '帳號已存在';
          } else if (err.status === 400) {
            // 優先顯示欄位訊息（例如密碼長度）
            this.error =
              fieldErrors?.['password'] ||
              fieldErrors?.['username'] ||
              backendMsg ||
              '請求參數驗證失敗';
          } else {
            this.error = backendMsg ?? '註冊失敗，請稍後再試';
          }
        },
      });
  }
}
