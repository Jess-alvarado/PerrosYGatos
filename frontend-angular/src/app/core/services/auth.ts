import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { LoginRequest, RegisterRequest, AuthResponse } from '../models/auth-model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private http = inject(HttpClient);
  private router = inject(Router);
  private api = `${environment.apiUrl}/api/auth`;

  login(request: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.api}/login`, request).pipe(
      tap(response => this.saveSession(response))
    );
  }

  register(request: RegisterRequest) {
    return this.http.post<AuthResponse>(`${this.api}/register`, request).pipe(
      tap(response => this.saveSession(response))
    );
  }

  logout() {
    return this.http.post(`${this.api}/logout`, {}).pipe(
      tap(() => this.clearSession())
    );
  }

  private saveSession(response: AuthResponse) {
    localStorage.setItem('token', response.accessToken);
    localStorage.setItem('refreshToken', response.refreshToken);

    // El role y userId se leen del JWT para UX
    const payload = this.decodeToken(response.accessToken);
    localStorage.setItem('userId', String(payload.uid));
    localStorage.setItem('role', payload.role);
    localStorage.setItem('username', payload.sub);
  }

  private decodeToken(token: string): any {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  }

  clearSession() {
    localStorage.clear();
    this.router.navigate(['/auth/login']);
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  getUserId(): number | null {
    const id = localStorage.getItem('userId');
    return id ? Number(id) : null;
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }
}
