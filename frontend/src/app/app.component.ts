import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './core/auth/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="min-h-screen bg-slate-50 text-slate-900">
      <header class="bg-white shadow">
        <nav class="max-w-5xl mx-auto px-4 py-3 flex items-center gap-4 text-sm">
          <ng-container *ngIf="isAuthed; else guestLinks">
            <a routerLink="/" routerLinkActive="text-blue-600 font-semibold" class="hover:text-blue-600">Dashboard</a>
            <a routerLink="/roles" routerLinkActive="text-blue-600 font-semibold" class="hover:text-blue-600">Roles</a>
            <a routerLink="/audit" routerLinkActive="text-blue-600 font-semibold" class="hover:text-blue-600">Audit</a>
            <a routerLink="/sessions" routerLinkActive="text-blue-600 font-semibold" class="hover:text-blue-600">Sessions</a>
            <span class="flex-1"></span>
            <button class="text-red-600 hover:text-red-700" (click)="logout()">Logout</button>
          </ng-container>
          <ng-template #guestLinks>
            <span class="flex-1"></span>
            <a routerLink="/login" routerLinkActive="text-blue-600 font-semibold" class="hover:text-blue-600">Login</a>
            <a routerLink="/register" routerLinkActive="text-blue-600 font-semibold" class="hover:text-blue-600">Register</a>
          </ng-template>
        </nav>
      </header>
      <main class="max-w-5xl mx-auto px-4 py-6">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
})
export class AppComponent {
  get isAuthed(): boolean {
    return this.auth.isAuthenticated();
  }

  constructor(private readonly auth: AuthService, private readonly router: Router) {}

  logout() {
    this.auth.logout().subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login']),
    });
  }
}
