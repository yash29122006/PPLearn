import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LeaderboardEntry {
  rank: number;
  studentId: number;
  studentUsername: string;
  totalPoints: number;
  weekStart: string;
  weekEnd: string;
}

@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {

  private readonly http = inject(HttpClient);

  getLeaderboard(): Observable<LeaderboardEntry[]> {
    return this.http.get<LeaderboardEntry[]>(
      '/api/leaderboard'
    );
  }
}