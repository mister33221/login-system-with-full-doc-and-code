import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, switchMap, mapTo } from 'rxjs';

export interface LoginPayload {
  username: string;
  password: string;
}

export interface LoginResult {
  accessToken: string;
  refreshToken: string;
}

export interface CurrentUser {
  username: string;
  email: string;
  roles: string[];
  permissions: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private accessTokenKey = 'access_token';
  private rolesKey = 'roles';
  private permissionsKey = 'perms';
  private refreshTokenKey = 'refresh_token';

  constructor(private readonly http: HttpClient) {}

  isAuthenticated(): boolean {
    return !!localStorage.getItem(this.accessTokenKey);
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.accessTokenKey);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.refreshTokenKey);
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
    localStorage.removeItem(this.permissionsKey);
    localStorage.removeItem(this.refreshTokenKey);
  }

  login(payload: LoginPayload): Observable<void> {
    return this.http.post<LoginResult>('/api/auth/login', payload).pipe(
      tap((res) => {
        localStorage.setItem(this.accessTokenKey, res.accessToken);
        localStorage.setItem(this.refreshTokenKey, res.refreshToken ?? '');
      }),
      switchMap(() => this.fetchCurrentUser()),
      tap((user) => this.storeUser(user)),
      mapTo(void 0)
    );
  }

  logout(): Observable<{ success: boolean; revoked: boolean }> {
    const refreshToken = this.getRefreshToken();
    return this.http.post<{ success: boolean; revoked: boolean }>('/api/auth/logout', {
      refreshToken,
    });
  }

  fetchCurrentUser(): Observable<CurrentUser> {
    return this.http.get<CurrentUser>('/api/me');
  }

  private storeUser(user: CurrentUser) {
    localStorage.setItem(this.rolesKey, JSON.stringify(user.roles ?? []));
    localStorage.setItem(this.permissionsKey, JSON.stringify(user.permissions ?? []));
  }
}
