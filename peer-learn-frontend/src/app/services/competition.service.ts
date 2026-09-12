import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Competition {
  competitionId: number;
  weekStart: string;
  weekEnd: string;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class CompetitionService {

  private readonly http = inject(HttpClient);

  getCurrentCompetition(): Observable<Competition> {
    return this.http.get<Competition>(
      '/api/competition/current'
    );
  }
}