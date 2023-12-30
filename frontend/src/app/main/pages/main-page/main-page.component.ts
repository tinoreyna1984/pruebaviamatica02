import { Component } from '@angular/core';
import { AuthService } from 'src/app/auth/services/auth.service';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent {
  constructor(
    private authService: AuthService,
  ){}

  userRealName: string | null = '';
  userRole: string | null = '';
  userEmail: string | null = '';
  userFailedAttempts : number | null = 0;
  userLastSession: any;

  ngOnInit(): void {
    console.log(localStorage.getItem('jwt'));
    this.userRealName = this.authService.getUserRealNameFromToken();
    this.userRole = this.authService.getRoleFromToken();
    this.userEmail = this.authService.getEmailFromToken();
    this.userFailedAttempts = this.authService.geFailedAttemptsFromToken();
    this.userLastSession = this.authService.getSessions().reduce((anterior: any, actual: any) => {
      const fechaAnterior = new Date(anterior.fechaFinSesion);
      const fechaActual = new Date(actual.fechaFinSesion);
      return fechaAnterior > fechaActual ? anterior : actual;
  }) || [];
    console.log(this.userLastSession)
  }
}
