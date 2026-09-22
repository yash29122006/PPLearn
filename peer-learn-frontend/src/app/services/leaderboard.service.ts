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

export interface WeeklyLeaderboard {
  currentWeek: LeaderboardEntry[];
  previousWeek: LeaderboardEntry[];
}

@Injectable({
  providedIn: 'root'
})
export class LeaderboardService {

  private readonly http = inject(HttpClient);

  getLeaderboard(): Observable<WeeklyLeaderboard> {
    return this.http.get<WeeklyLeaderboard>(
      '/api/leaderboard'
    );
  }

  getPreviewLeaderboard(): Observable<LeaderboardEntry[]> {
    return this.http.get<LeaderboardEntry[]>(
      '/api/leaderboard/preview'
    );
  }
}