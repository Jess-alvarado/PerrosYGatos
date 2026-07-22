import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';
import { roleGuard } from './core/guards/role-guard';
import { inject } from '@angular/core';

export const routes: Routes = [
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },

  // Auth — público
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./features/auth/login/login')
            .then(m => m.Login)
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./features/auth/register/register')
            .then(m => m.Register)
      }
    ]
  },

  // Owner — protegido
  {
    path: 'owner',
    canActivate: [authGuard, roleGuard],
    data: {expectRole: 'ROLE_OWNER'},
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/owner/dashboard/dashboard')
            .then(m => m.Dashboard)
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/owner/profile/profile')
            .then(m => m.Profile)
      },
      {
        path: 'pets',
        loadComponent: () =>
          import('./features/owner/pets/pets')
            .then(m => m.Pets)
      },
      {
        path: 'cases',
        loadComponent: () =>
          import('./features/owner/cases/cases')
            .then(m => m.Cases)
      }
    ]
  },

  // Professional — protegido
  {
    path: 'professional',
    canActivate: [authGuard, roleGuard],
    data: {expectedRole: 'ROLE_PROFESSIONAL'},
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/professional/dashboard/dashboard')
            .then(m => m.Dashboard)
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./features/professional/profile/profile')
            .then(m => m.Profile)
      },
      {
        path: 'cases',
        loadComponent: () =>
          import('./features/professional/cases/cases')
            .then(m => m.Cases)
      }
    ]
  },

  { path: '**', redirectTo: 'auth/login' }
];
