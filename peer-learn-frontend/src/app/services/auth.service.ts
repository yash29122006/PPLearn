import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface CurrentUser {
  userId: number;
  username: string;
  email: string;
  role: string;
  totalPoints: number;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: number;
  username: string;
  email: string;
  role: string;
  totalPoints: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly http = inject(HttpClient);

  private readonly tokenKey = 'peer_learn_token';
  private readonly userKey = 'peer_learn_user';

  register(request: RegisterRequest): Observable<any> {
    return this.http.post('/api/auth/register', request);
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      '/api/auth/login',
      request
    ).pipe(
      tap(response => {
        localStorage.setItem(this.tokenKey, response.token);

        localStorage.setItem(
          this.userKey,
          JSON.stringify({
  userId: response.userId,
  username: response.username,
  email: response.email,
  role: response.role,
  totalPoints: response.totalPoints
})
        );
      })
    );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    return this.getToken() !== null;
  }

  getCurrentUser(): {
  userId: number;
  username: string;
  email: string;
  role: string;
  totalPoints: number;
} | null {

    const user = localStorage.getItem(this.userKey);

    if (!user) {
      return null;
    }

    return JSON.parse(user);
  }

  
getCurrentUserFromServer(): Observable<CurrentUser> {
  return this.http.get<CurrentUser>('/api/auth/me').pipe(
    tap(user => {
      localStorage.setItem(
        this.userKey,
        JSON.stringify(user)
      );
    })
  );
}
}