import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route) => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const token = authService.getToken();
  const user = authService.getCurrentUser();

  console.log('[AuthGuard] Token exists:', !!token);
  console.log('[AuthGuard] Current user:', user);

  // No authentication data
  if (!token || !user) {
    console.log('[AuthGuard] Not authenticated → /login');

    return router.createUrlTree(['/login']);
  }

  const requiredRole = route.data['role'];

  console.log('[AuthGuard] Required role:', requiredRole);
  console.log('[AuthGuard] User role:', user.role);

  // Role-protected route
  if (requiredRole && user.role !== requiredRole) {

    console.log('[AuthGuard] Role mismatch');

    if (user.role === 'ADMIN') {
      return router.createUrlTree(['/admin']);
    }

    return router.createUrlTree(['/home']);
  }

  console.log('[AuthGuard] Access granted');

  return true;
};