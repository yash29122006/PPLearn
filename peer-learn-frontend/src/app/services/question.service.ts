import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Question {
  questionId: number;
  postedById: number;
  postedByUsername: string;
  title: string;
  description: string | null;
  leetcodeUrl: string;
  postedDate: string;
  postedAt: string;
  ownQuestion: boolean;
  attempted: boolean;
  attemptStatus: string | null;
  attemptPoints: number | null;
}

export interface QuestionRequest {
  title: string;
  description: string;
  leetcodeUrl: string;
}

export interface QuestionResponse {
  questionId: number;
  competitionId: number;
  postedById: number;
  postedByUsername: string;
  title: string;
  description: string | null;
  leetcodeUrl: string;
  postedDate: string;
  postedAt: string;
  pointsAwarded: number;
}

@Injectable({
  providedIn: 'root'
})
export class QuestionService {

  private readonly http = inject(HttpClient);

  getQuestions(): Observable<Question[]> {
    return this.http.get<Question[]>('/api/questions');
  }

  postQuestion(
    request: QuestionRequest
  ): Observable<QuestionResponse> {

    return this.http.post<QuestionResponse>(
      '/api/questions',
      request
    );
  }
}