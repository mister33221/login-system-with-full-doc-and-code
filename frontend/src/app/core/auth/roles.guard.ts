import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class RolesGuard implements CanActivate {
  constructor(private readonly auth: AuthService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRoles = (route.data?.['roles'] as string[]) || [];
    if (requiredRoles.length === 0) {
      return true;
    }
    if (this.auth.hasAnyRole(requiredRoles)) {
      return true;
    }
    this.router.navigate(['/403']);
    return false;
  }
}
