import { Component } from '@angular/core';
import { AuthService } from './auth/services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'pruebaviamatica-frontend';
  debug: boolean = true;

  // inyecta dependencia AuthService
  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    if (!this.debug) {
      this.authService.checkInactivityAndLogout(); // Llama al método de verificación de inactividad al cargar la aplicación y luego cada X segundos
      setInterval(() => {
        this.authService.checkInactivityAndLogout();
      }, 60000); // Verificar cada minuto
    }
  }
}
