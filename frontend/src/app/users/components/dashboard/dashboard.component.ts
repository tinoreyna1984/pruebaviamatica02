import { Component, Input, OnInit } from '@angular/core';
import { AuthService } from 'src/app/auth/services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  constructor(private authService: AuthService) {}

  @Input() loading: boolean = false;

  dashboard: any;
  active: number = 0;
  locked: number = 0;
  total: number = 0;

  ngOnInit(): void {
    this.loading = true;
    this.authService.getDashboard().subscribe({
      next: (dashboard: any) => {
        this.dashboard = dashboard;
        //console.log(this.dashboard);
        this.total = this.dashboard.total;
        this.active = this.dashboard.activos || 0;
        this.locked = this.dashboard.bloqueados || 0;
        this.loading = false;
      },
      error: (e: any) => {
        //console.error(e.message);
        Swal.fire(
          'Error en la carga',
          'Razón: ' + e.message + '. Consulta con el administrador, por favor.',
          'error'
        );
        this.loading = false;
      },
    });
  }
}
