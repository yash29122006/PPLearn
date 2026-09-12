import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AdminStudent {
  studentId: number;
  username: string;
  email: string;
  totalPoints: number;
  createdAt: string;
}

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
export class AdminService {

  private readonly http = inject(HttpClient);

  getStudents(): Observable<AdminStudent[]> {
    return this.http.get<AdminStudent[]>('/api/admin/students');
  }

  getPendingApplications(): Observable<ApplicationResponse[]> {
  return this.http.get<ApplicationResponse[]>(
    '/api/admin/applications'
  );
}

  acceptApplication(applicationId: number): Observable<ApplicationResponse> {
  return this.http.patch<ApplicationResponse>(
    `/api/admin/applications/${applicationId}`,
    {
      decision: 'ACCEPT'
    }
  );
}

rejectApplication(applicationId: number): Observable<ApplicationResponse> {
  return this.http.patch<ApplicationResponse>(
    `/api/admin/applications/${applicationId}`,
    {
      decision: 'REJECT'
    }
  );
}

  removeStudent(studentId: number): Observable<void> {
    return this.http.delete<void>(
      `/api/admin/students/${studentId}`
    );
  }
}