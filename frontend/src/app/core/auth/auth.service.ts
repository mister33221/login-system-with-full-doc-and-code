import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private accessTokenKey = 'access_token';
  private rolesKey = 'roles';

  isAuthenticated(): boolean {
    return !!localStorage.getItem(this.accessTokenKey);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.accessTokenKey);
  }

  getRoles(): string[] {
    const raw = localStorage.getItem(this.rolesKey);
    if (!raw) return [];
    try {
      return JSON.parse(raw);
    } catch {
      return [];
    }
  }

  hasAnyRole(required: string[]): boolean {
    const roles = this.getRoles();
    return required.some((r) => roles.includes(r));
  }

  clear(): void {
    localStorage.removeItem(this.accessTokenKey);
    localStorage.removeItem(this.rolesKey);
  }
}
