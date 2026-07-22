import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const roleGuard = (requiredRole: string): CanActivateFn => {
  return () => {
    const router = inject(Router);
    const role = localStorage.getItem('role');

    if (role !== requiredRole) {
      router.navigate(['/auth/login']);
      return false;
    }
    return true;
  };
};
