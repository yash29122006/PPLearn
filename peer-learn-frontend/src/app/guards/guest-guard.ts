import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

import { AuthService } from '../services/auth.service';

export const guestGuard: CanActivateFn = () => {

  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.getCurrentUser();

  if (!user) {
    return true;
  }

  if (user.role === 'ADMIN') {
    return router.createUrlTree(['/admin']);
  }

  return router.createUrlTree(['/home']);
};