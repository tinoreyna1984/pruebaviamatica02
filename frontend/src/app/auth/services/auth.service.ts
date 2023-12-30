import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { firstValueFrom, fromEvent, map } from 'rxjs';
import { environment } from 'src/environments/environment';
import jwt_decode from 'jwt-decode';
import Swal from 'sweetalert2';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl: string = environment.baseUrl;
  private http = inject(HttpClient);
  private inactivityTimer: any;

  constructor(private router: Router) {
    // Observa eventos de teclado y ratón para reiniciar el temporizador de inactividad
    fromEvent(document, 'mousemove').subscribe(() =>
      this.resetInactivityTimer()
    );
    fromEvent(document, 'keydown').subscribe(() => this.resetInactivityTimer());
    this.startInactivityTimer();
  }

  // Inicia el temporizador de inactividad
  private startInactivityTimer() {
    const token = localStorage.getItem('jwt');
    if (!token) return; // no hará nada si no hay token
    this.inactivityTimer = setTimeout(() => {
      // Si han pasado más de 30 minutos desde la última interacción, cierra la sesión
      Swal.fire(
        'Saliste del programa',
        'Has estado inactivo por 30 minutos',
        'error'
      );
      this.logout();
    }, 30 * 60 * 1000);
  }

  // Reinicia el temporizador de inactividad
  private resetInactivityTimer() {
    clearTimeout(this.inactivityTimer);
    this.startInactivityTimer();
  }

  login(formValue: any) {
    return firstValueFrom(
      this.http.post<any>(`${this.baseUrl}/auth/authenticate`, formValue)
    );
  }

  register(formValue: any) {
    return firstValueFrom(
      this.http.post<any>(`${this.baseUrl}/auth/register`, formValue)
    );
  }

  // obtengo el rol de acceso
  getRoleFromToken(): string | null {
    const token = localStorage.getItem('jwt');
    if (token) {
      const decodedToken: any = jwt_decode(token);
      return decodedToken.role;
    }
    return null;
  }

  // obtengo nombre del usuario
  getUserRealNameFromToken(): string | null {
    const token = localStorage.getItem('jwt');
    if (token) {
      const decodedToken: any = jwt_decode(token);
      return decodedToken.name;
    }
    return null;
  }

  getRoutes(): any {
    const token = localStorage.getItem('jwt');
    if (token) {
      const decodedToken: any = jwt_decode(token);
      return decodedToken.authorizedRoutes;
    }
    return null;
  }

  // verifico que el rol sea de administrador
  isAdmin(): boolean {
    return this.getRoleFromToken() === 'ADMINISTRATOR';
  }

  // Método para verificar la inactividad y cerrar la sesión si es necesario
  checkInactivityAndLogout() {
    const token = localStorage.getItem('jwt');
    if (token) {
      const decodedToken: any = jwt_decode(token);
      const iat = decodedToken.iat * 1000; // Convertir a milisegundos
      const exp = decodedToken.exp * 1000; // Convertir a milisegundos
      const currentTime = new Date().getTime(); // Tiempo actual en milisegundos

      if (currentTime - iat > 30 * 60 * 1000) {
        // Si han pasado más de 30 minutos desde que se emitió el token, cierra la sesión
        Swal.fire(
          'Saliste del programa',
          'Token caducado y/o has estado inactivo por 30 minutos',
          'error'
        );
        this.logout();
      } else if (currentTime > exp) {
        // Si el token ha expirado, también cierra la sesión
        Swal.fire(
          'Saliste del programa',
          'Token caducado y/o has estado inactivo por 30 minutos',
          'error'
        );
        this.logout();
      }
    }
  }

  isLoggedIn(): boolean {
    const token = localStorage.getItem('jwt');
    return token !== null;
  }

  getDashboard(){
    const token = localStorage.getItem('jwt');
    const headers = { 'Authorization': 'Bearer ' + token }
    return this.http.get<any>(`${this.baseUrl}/dashboard`, { headers })
    .pipe(
      map((dashboard: any) => dashboard)
    ); 
  }

  // cierro sesión
  logout() {
    const token = localStorage.getItem('jwt');
    const jwt = {
      jwt: token
    }
    localStorage.removeItem('jwt');
    this.router.navigate(['/']);
    return firstValueFrom(
      this.http.patch<any>(`${this.baseUrl}/auth/logout`, JSON.stringify(jwt), {
        headers: {
          'Content-Type': 'application/json' // Añadir esta línea para especificar el tipo de contenido
        }
      })
    );
  }
}
