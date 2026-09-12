import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AttemptResponse {
  attemptId: number;
  questionId: number;
  studentId: number;
  studentUsername: string;
  startedAt: string | null;
  finishedAt: string | null;
  status: string;
  pointsEarned: number;
  bonusPoints: number;
  totalPoints: number;
}

@Injectable({
  providedIn: 'root'
})
export class AttemptService {

  private readonly http = inject(HttpClient);

  startAttempt(questionId: number): Observable<AttemptResponse> {
    return this.http.post<AttemptResponse>(
      `/api/attempts/${questionId}/start`,
      {}
    );
  }

  finishAttempt(questionId: number): Observable<AttemptResponse> {
    return this.http.post<AttemptResponse>(
      `/api/attempts/${questionId}/finish`,
      {}
    );
  }

  getMyAttempt(questionId: number): Observable<AttemptResponse> {
    return this.http.get<AttemptResponse>(
      `/api/attempts/${questionId}/me`
    );
  }

  getQuestionAttempts(
    questionId: number
  ): Observable<AttemptResponse[]> {
    return this.http.get<AttemptResponse[]>(
      `/api/attempts/${questionId}`
    );
  }
}