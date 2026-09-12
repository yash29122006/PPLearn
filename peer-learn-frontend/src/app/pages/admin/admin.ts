import {
  Component,
  OnInit,
  ChangeDetectorRef,
  inject
} from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

import {
  AdminService,
  AdminStudent,
  ApplicationResponse
} from '../../services/admin.service';

@Component({
  selector: 'app-admin',
  imports: [
  CommonModule,
  DatePipe,
  FormsModule
],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class Admin implements OnInit {

  private readonly adminService = inject(AdminService);
  private readonly changeDetectorRef = inject(ChangeDetectorRef);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);

  students: AdminStudent[] = [];
  applications: ApplicationResponse[] = [];

  loadingStudents = false;
  loadingApplications = false;

  errorMessage = '';

  ngOnInit(): void {
    this.loadStudents();
    this.loadApplications();
  }

  loadStudents(): void {
  this.loadingStudents = true;
  this.errorMessage = '';

  this.adminService.getStudents().subscribe({
    next: (students) => {
      this.students = students;
      this.loadingStudents = false;

      this.changeDetectorRef.detectChanges();
    },

    error: () => {
      this.loadingStudents = false;
      this.errorMessage = 'Failed to load students.';

      this.changeDetectorRef.detectChanges();
    }
  });
}

  loadApplications(): void {
  this.loadingApplications = true;
  this.errorMessage = '';

  this.adminService.getPendingApplications().subscribe({
    next: (applications) => {
      this.applications = applications;
      this.loadingApplications = false;

      this.changeDetectorRef.detectChanges();
    },

    error: () => {
      this.loadingApplications = false;
      this.errorMessage = 'Failed to load applications.';

      this.changeDetectorRef.detectChanges();
    }
  });
}

  acceptApplication(applicationId: number): void {

  this.errorMessage = '';

  this.adminService.acceptApplication(applicationId).subscribe({
    next: () => {
      this.loadApplications();
      this.loadStudents();
    },
    error: () => {
      this.errorMessage = 'Failed to accept application.';
    }
  });
}

  rejectApplication(applicationId: number): void {

  const confirmed = confirm(
    'Are you sure you want to reject this application?'
  );

  if (!confirmed) {
    return;
  }

  this.errorMessage = '';

  this.adminService.rejectApplication(applicationId).subscribe({
    next: () => {
      this.loadApplications();
    },
    error: () => {
      this.errorMessage = 'Failed to reject application.';
    }
  });
}

  removeStudent(studentId: number): void {
    const confirmed = confirm(
      'Are you sure you want to remove this student?'
    );

    if (!confirmed) {
      return;
    }

    this.adminService.removeStudent(studentId).subscribe({
      next: () => {
        this.loadStudents();
      },
      error: () => {
        this.errorMessage = 'Failed to remove student.';
      }
    });
  }

  logout(): void {
  this.authService.logout();
  this.router.navigate(['/login']);
}

searchTerm = '';
get filteredStudents(): AdminStudent[] {
  const term = this.searchTerm.trim().toLowerCase();

  if (!term) {
    return this.students;
  }

  return this.students.filter(student =>
    student.username.toLowerCase().includes(term) ||
    student.email.toLowerCase().includes(term)
  );
}

get totalStudentPoints(): number {
  return this.students.reduce(
    (total, student) => total + student.totalPoints,
    0
  );
}


}