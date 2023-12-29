import { Component, inject } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.css']
})
export class RegisterPageComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  form: FormGroup;
  loading: boolean = false;
  errorMsg: string = '';

  constructor() {
    this.form = new FormGroup({
      username: new FormControl(),
      password: new FormControl(),
      accessId: new FormControl(),
      name: new FormControl(),
      lastName: new FormControl(),
    });
  }

  // llamo al servicio AuthService
  async onRegister() {
    if (this.loading) {
      return; // Evita múltiples solicitudes si ya se está cargando
    }

    let tmpForm: any = this.form.value;
    tmpForm = {...tmpForm, role: 'USER'};
    //console.log(tmpForm);
    //return;
    this.loading = true; // Establece el estado de carga a true

    try {
      const res: any = await this.authService.register(tmpForm); // uso servicio de login
      //console.log(res);
      if(res.jwt){
        localStorage.setItem('jwt', res.jwt); // Almacena el token JWT en el almacenamiento local
        this.router.navigate(['/main']); // dirige a la página "/main"
      }
    } catch (error: any) {
      let status: number = error.error.status;
      this.errorMsg = error.error.message;
      if(status >= 500){
        Swal.fire('Error en el acceso', "Razón: " + this.errorMsg + ". Consulta con el administrador, por favor", 'error' );
      }
      else if(status >=400 && status < 500){
        Swal.fire('Error en el acceso', this.errorMsg, 'error' );
      }
    } finally {
      this.loading = false; // Restaura el estado de carga a false, ya sea en éxito o error
    }
  }
}
