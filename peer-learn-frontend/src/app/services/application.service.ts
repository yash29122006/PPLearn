import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ApplicationResponse {
  applicationId: number;
  studentId: number;
  studentUsername: string;
  status: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApplicationService {

  private readonly http = inject(HttpClient);

  applyToCommunity(): Observable<ApplicationResponse> {
    return this.http.post<ApplicationResponse>(
      '/api/applications',
      {}
    );
  }

  getMyApplication(): Observable<ApplicationResponse> {
    return this.http.get<ApplicationResponse>(
      '/api/applications/me'
    );
  }
}