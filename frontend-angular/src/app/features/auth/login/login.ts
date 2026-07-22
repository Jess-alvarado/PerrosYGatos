import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss'
})
export class LoginComponent {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loading = false;
  error = '';
  hidePassword = true;

  form = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required]]
  });

  onSubmit() {
    if (this.form.invalid) return;

    this.loading = true;
    this.error = '';

    const { username, password } = this.form.value;

    this.authService.login({ username: username!, password: password! })
      .subscribe({
        next: () => {
          const role = this.authService.getRole();
          if (role === 'ROLE_OWNER') {
            this.router.navigate(['/owner/dashboard']);
          } else if (role === 'ROLE_PROFESSIONAL') {
            this.router.navigate(['/professional/dashboard']);
          }
        },
        error: (err) => {
          this.loading = false;
          this.error = err.status === 401
            ? 'Usuario o contraseña incorrectos'
            : 'Ocurrió un error. Intenta de nuevo.';
        },
        complete: () => {
          this.loading = false;
        }
      });
  }
}
