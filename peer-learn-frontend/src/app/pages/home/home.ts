import { Component, OnInit, OnDestroy, ChangeDetectorRef, inject } from '@angular/core';
import { finalize } from 'rxjs';

import { CompetitionService, Competition } from '../../services/competition.service';

import {
  LeaderboardEntry,
  LeaderboardService,
  WeeklyLeaderboard
} from '../../services/leaderboard.service';

import { CommonModule, DatePipe } from '@angular/common';

import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService, CurrentUser } from '../../services/auth.service';

import { WebSocketService } from '../../services/websocket.service';

import { QuestionService, Question } from '../../services/question.service';

import { AttemptService, AttemptResponse } from '../../services/attempt.service';

@Component({
  selector: 'app-home',
  imports: [CommonModule, DatePipe, ReactiveFormsModule],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit, OnDestroy {
  private readonly competitionService = inject(CompetitionService);
  private readonly webSocketService = inject(WebSocketService);
  private readonly leaderboardService = inject(LeaderboardService);
  private readonly authService = inject(AuthService);
  private readonly questionService = inject(QuestionService);
  private readonly router = inject(Router);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  private readonly attemptService = inject(AttemptService);

  currentUser: CurrentUser | null = null;
  competition: Competition | null = null;
  loadingCompetition = false;
  competitionErrorMessage = '';
 currentWeekLeaderboard: LeaderboardEntry[] = [];
previousWeekLeaderboard: LeaderboardEntry[] = [];
  loadingLeaderboard = false;
  leaderboardErrorMessage = '';
  questions: Question[] = [];
  startingQuestionId: number | null = null;
  finishingQuestionId: number | null = null;
  expandedQuestionId: number | null = null;
  questionAttempts: Record<number, AttemptResponse[]> = {};
  loadingAttemptsQuestionId: number | null = null;
  attemptErrorMessage = '';

  loadingQuestions = false;
  errorMessage = '';
  private midnightTimer: ReturnType<typeof setTimeout> | null = null;

  ngOnInit(): void {
  this.loadCurrentUser();
  this.loadQuestions();
  this.loadLeaderboard();
  this.loadCompetition();
  this.connectWebSocket();

  this.scheduleMidnightRefresh();
}
  ngOnDestroy(): void {
  this.webSocketService.disconnect();

  if (this.midnightTimer !== null) {
    clearTimeout(this.midnightTimer);
    this.midnightTimer = null;
  }
}

  loadQuestions(): void {
    this.loadingQuestions = true;
    this.errorMessage = '';

    this.questionService.getQuestions().subscribe({
      next: (questions) => {
        this.questions = questions;
        this.loadingQuestions = false;

        this.changeDetectorRef.detectChanges();
      },

      error: () => {
        this.loadingQuestions = false;
        this.errorMessage = 'Failed to load questions. Please try again.';

        this.changeDetectorRef.detectChanges();
      },
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  private readonly fb = inject(FormBuilder);

  showPostForm = false;
  postingQuestion = false;
  postErrorMessage = '';
  postSuccessMessage = '';

  questionForm = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(255)]],

    description: ['', [Validators.maxLength(5000)]],

    leetcodeUrl: ['', [Validators.required, Validators.maxLength(500)]],
  });

  openPostForm(): void {
    this.showPostForm = true;
    this.postErrorMessage = '';
    this.postSuccessMessage = '';
  }

  closePostForm(): void {
    if (this.postingQuestion) {
      return;
    }

    this.showPostForm = false;
    this.postErrorMessage = '';
    this.postSuccessMessage = '';
    this.questionForm.reset();
  }

  submitQuestion(): void {
    if (this.questionForm.invalid) {
      this.questionForm.markAllAsTouched();
      return;
    }

    this.postingQuestion = true;
    this.postErrorMessage = '';
    this.postSuccessMessage = '';

    const request = this.questionForm.getRawValue();

    this.questionService
      .postQuestion(request)
      .pipe(
        finalize(() => {
          this.postingQuestion = false;
          this.changeDetectorRef.detectChanges();
        }),
      )
      .subscribe({
        next: (response) => {
          this.postSuccessMessage = `Question posted successfully! You earned ${response.pointsAwarded} points.`;

          this.questionForm.reset();

          this.loadQuestions();

          setTimeout(() => {
            this.showPostForm = false;
            this.postSuccessMessage = '';
          }, 1500);
        },

        error: (error) => {
          console.error('[Home] Failed to post question:', error);

          if (error.error?.message) {
            this.postErrorMessage = error.error.message;
          } else if (typeof error.error === 'string') {
            this.postErrorMessage = error.error;
          } else if (error.status === 400) {
            this.postErrorMessage = 'You can only post one question per day.';
          } else {
            this.postErrorMessage = 'Failed to post question. Please try again.';
          }

          this.changeDetectorRef.detectChanges();
        },
      });
  }

  startAttempt(questionId: number): void {
    this.startingQuestionId = questionId;
    this.attemptErrorMessage = '';

    this.attemptService.startAttempt(questionId).subscribe({
      next: (attempt) => {
        this.startingQuestionId = null;

        const question = this.questions.find((q) => q.questionId === questionId);

        if (question) {
          question.attempted = true;
          question.attemptStatus = attempt.status;
          question.attemptPoints = attempt.totalPoints;
        }

        this.changeDetectorRef.detectChanges();
      },

      error: (error) => {
        this.startingQuestionId = null;

        this.attemptErrorMessage = error.error?.message ?? 'Failed to start the attempt.';

        this.changeDetectorRef.detectChanges();
      },
    });
  }

  finishAttempt(questionId: number): void {
    this.finishingQuestionId = questionId;
    this.attemptErrorMessage = '';

    this.attemptService.finishAttempt(questionId).subscribe({
      next: (attempt) => {
        this.finishingQuestionId = null;

        const question = this.questions.find((q) => q.questionId === questionId);

        if (question) {
          question.attempted = true;
          question.attemptStatus = attempt.status;
          question.attemptPoints = attempt.totalPoints;
        }

        this.loadCurrentUser();

        this.changeDetectorRef.detectChanges();
      },

      error: (error) => {
        this.finishingQuestionId = null;

        this.attemptErrorMessage = error.error?.message ?? 'Failed to finish the attempt.';

        this.changeDetectorRef.detectChanges();
      },
    });
  }

  toggleQuestionAttempts(questionId: number): void {
    if (this.expandedQuestionId === questionId) {
      this.expandedQuestionId = null;
      return;
    }

    this.expandedQuestionId = questionId;

    if (this.questionAttempts[questionId]) {
      return;
    }

    this.loadingAttemptsQuestionId = questionId;
    this.attemptErrorMessage = '';

    this.attemptService.getQuestionAttempts(questionId).subscribe({
      next: (attempts) => {
        this.questionAttempts[questionId] = attempts;
        this.loadingAttemptsQuestionId = null;

        this.changeDetectorRef.detectChanges();
      },

      error: (error) => {
        this.loadingAttemptsQuestionId = null;

        this.attemptErrorMessage = error.error?.message ?? 'Failed to load attempts.';

        this.changeDetectorRef.detectChanges();
      },
    });
  }

  loadCurrentUser(): void {
    this.authService.getCurrentUserFromServer().subscribe({
      next: (user) => {
        this.currentUser = user;

        this.changeDetectorRef.detectChanges();
      },

      error: () => {
        this.authService.logout();
        this.router.navigate(['/login']);
      },
    });
  }

  loadLeaderboard(): void {
  this.leaderboardService.getLeaderboard().subscribe({
    next: (leaderboard: WeeklyLeaderboard) => {
      this.currentWeekLeaderboard = leaderboard.currentWeek;
      this.previousWeekLeaderboard = leaderboard.previousWeek;
    },
    error: (error) => {
      console.error(
        'Failed to load leaderboard:',
        error
      );

      this.currentWeekLeaderboard = [];
      this.previousWeekLeaderboard = [];
    }
  });
}

  connectWebSocket(): void {
    this.webSocketService
      .connect()
      .then(() => {
        console.log('[Home] WebSocket connection established');

        this.subscribeToQuestions();

        this.subscribeToAttempts();

        this.subscribeToLeaderboard();
      })
      .catch((error) => {
        console.error('[Home] WebSocket connection failed:', error);
      });
  }

  subscribeToQuestions(): void {
    this.webSocketService.subscribe<Question>('/topic/questions').subscribe({
      next: (event) => {
        console.log('[Home] WebSocket question event:', event);

        if (event.type === 'QUESTION_POSTED') {
  this.loadQuestions();
  this.loadLeaderboard();
}
      },

      error: (error) => {
        console.error('[Home] Question subscription error:', error);
      },
    });
  }

  subscribeToAttempts(): void {
    this.webSocketService.subscribe('/topic/attempts').subscribe({
      next: (event) => {
        console.log('[Home] WebSocket attempt event:', event);

        if (event.type === 'ATTEMPT_UPDATED') {
          // Refresh question attempt status/points
          this.loadQuestions();
          // Refresh live weekly leaderboard
  this.loadLeaderboard();
          // If an attempts panel is currently open,
          // refresh its student list as well.
          if (this.expandedQuestionId !== null) {
            const questionId = this.expandedQuestionId;

            this.questionAttempts[questionId] = [];

            this.loadingAttemptsQuestionId = questionId;

            this.attemptService.getQuestionAttempts(questionId).subscribe({
              next: (attempts) => {
                this.questionAttempts[questionId] = attempts;

                this.loadingAttemptsQuestionId = null;

                this.changeDetectorRef.detectChanges();
              },

              error: () => {
                this.loadingAttemptsQuestionId = null;

                this.changeDetectorRef.detectChanges();
              },
            });
          }

          // Refresh the current user's authoritative points.
          this.loadCurrentUser();
        }
      },

      error: (error) => {
        console.error('[Home] Attempt subscription error:', error);
      },
    });
  }

  subscribeToLeaderboard(): void {
    this.webSocketService.subscribe<LeaderboardEntry[]>('/topic/leaderboard').subscribe({
      next: (event) => {
        console.log('[Home] WebSocket leaderboard event:', event);

        if (event.type === 'LEADERBOARD_UPDATED') {
          this.loadLeaderboard();
          this.loadCompetition();
        }
      },

      error: (error) => {
        console.error('[Home] Leaderboard subscription error:', error);
      },
    });
  }

  loadCompetition(): void {
    this.loadingCompetition = true;
    this.competitionErrorMessage = '';

    this.competitionService.getCurrentCompetition().subscribe({
      next: (competition) => {
        this.competition = competition;
        this.loadingCompetition = false;
        this.changeDetectorRef.detectChanges();
      },
      error: (error) => {
        this.loadingCompetition = false;

        this.competitionErrorMessage = error.error?.message ?? 'Failed to load competition.';

        this.changeDetectorRef.detectChanges();
      },
    });
  }

  private scheduleMidnightRefresh(): void {
  const now = new Date();

  /*
   * Convert the current time to Asia/Kolkata.
   * IST is UTC+05:30.
   */
  const istFormatter = new Intl.DateTimeFormat('en-US', {
    timeZone: 'Asia/Kolkata',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hourCycle: 'h23'
  });

  const parts = istFormatter.formatToParts(now);

  const getPart = (type: string): number =>
    Number(
      parts.find(part => part.type === type)?.value
    );

  const year = getPart('year');
  const month = getPart('month');
  const day = getPart('day');

  /*
   * Calculate the next midnight in IST.
   */
  const nextMidnightUtc = Date.UTC(
    year,
    month - 1,
    day + 1,
    0,
    0,
    0
  ) - (5 * 60 + 30) * 60 * 1000;

  const delay = Math.max(
    nextMidnightUtc - now.getTime(),
    1000
  );

  this.midnightTimer = setTimeout(() => {

    /*
     * At 00:00 IST, reload today's questions.
     * The backend now returns only questions whose
     * postedDate is today's IST date.
     */
    this.loadQuestions();

    /*
     * Recalculate the next midnight.
     */
    this.scheduleMidnightRefresh();

  }, delay);
}
}
