import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {
  Router,
  RouterLink
} from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  loading = false;
  errorMessage = '';

  loginForm = this.fb.nonNullable.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  onSubmit(): void {

    if (this.loading) {
      return;
    }

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    const request = this.loginForm.getRawValue();

    this.authService.login(request).subscribe({

      next: (response) => {

        console.log('Login successful:', response);

        this.loading = false;

        if (response.role === 'ADMIN') {

          console.log('Navigating to admin...');

          this.router.navigate(['/admin']).then(
            success => {
              console.log('Admin navigation result:', success);
            },
            error => {
              console.error('Admin navigation error:', error);
            }
          );

        } else {

          console.log('Navigating to home...');

          this.router.navigate(['/home']).then(
            success => {
              console.log('Home navigation result:', success);
            },
            error => {
              console.error('Home navigation error:', error);
            }
          );
        }
      },

      error: (error) => {

        console.error('Login error:', error);

        this.loading = false;

        if (error.status === 401) {
          this.errorMessage = 'Invalid username or password.';
        } else {
          this.errorMessage =
            'Login failed. Please try again.';
        }
      }

    });
  }
}