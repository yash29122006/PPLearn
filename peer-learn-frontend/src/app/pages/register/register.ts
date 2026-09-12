import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  imports: [
  CommonModule,
  ReactiveFormsModule,
  RouterLink
  ],
  templateUrl: './register.html',
  styleUrl: './register.css'
})
export class Register {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  loading = false;
  errorMessage = '';
  successMessage = '';

  registerForm = this.fb.nonNullable.group({
    username: ['', [
      Validators.required,
      Validators.maxLength(50)
    ]],
    email: ['', [
      Validators.required,
      Validators.email,
      Validators.maxLength(150)
    ]],
    password: ['', [
      Validators.required,
      Validators.minLength(6)
    ]]
  });

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.register(
      this.registerForm.getRawValue()
    ).subscribe({
      next: () => {
        this.loading = false;
        this.successMessage =
  'Registration submitted successfully. Please wait for admin approval before logging in.';

        this.registerForm.reset();
      },

      error: (error) => {
        this.loading = false;

        if (error.status === 409) {
          this.errorMessage =
            'Username or email already exists.';
        } else if (error.error?.message) {
          this.errorMessage = error.error.message;
        } else {
          this.errorMessage =
            'Registration failed. Please try again.';
        }
      }
    });
  }
}