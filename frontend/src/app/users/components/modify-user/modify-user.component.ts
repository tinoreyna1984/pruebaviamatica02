import { Component, OnInit, inject } from '@angular/core';
import { MessageSnackBarComponent } from 'src/app/shared/components/message-snack-bar/message-snack-bar.component';
import Swal from 'sweetalert2';
import { UsersService } from '../../service/users.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { User } from 'src/app/shared/interfaces/user.interface';
import { FormControl, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-modify-user',
  templateUrl: './modify-user.component.html',
  styleUrls: ['./modify-user.component.css']
})
export class ModifyUserComponent  implements OnInit {
  private usersService = inject(UsersService);
  private snackBar = inject(MatSnackBar);
  private usuarioID: string = inject(MAT_DIALOG_DATA);
  private usuario!: User;
  loading: boolean = false;

  formModifyUsuario: FormGroup;

  constructor() {
    this.formModifyUsuario = new FormGroup({
      name: new FormControl(),
      lastName: new FormControl(),
      username: new FormControl(),
      password: new FormControl(),
      accessId: new FormControl(),
      email: new FormControl(),
      role: new FormControl(),
    });
  }

  ngOnInit(): void {
    this.loading = true;
    setTimeout(() => {
      this.usersService.getUser(this.usuarioID).subscribe({
        next: (response: User) => {
          this.loading = false;
          this.usuario = response;
          //console.log(this.usuario);
          this.formModifyUsuario.patchValue({
            name: this.usuario.name,
            lastName: this.usuario.lastName,
            username: this.usuario.username,
            password: '',
            accessId: this.usuario.accessId,
            email: this.usuario.email,
            role: this.usuario.role,
          });
        },
        error: (e: any) => {
          //console.error(e.message);
          this.loading = false;
          Swal.fire(
            'Error en encontrar el usuario',
            'No se encuentra el usuario con ID ' + this.usuarioID,
            'error'
          );
        },
      });
    }, 1800);
  }

  onModifyUsuario(){
    let tmpForm: any = this.formModifyUsuario.value;
    const { role, password } = this.formModifyUsuario.value;
    if(!role)
      tmpForm = {...tmpForm, role: 'USER'};
    if(!password){
      Swal.fire(
        'Ingresa tu clave',
        'La clave es requerida',
        'error'
      );
      return; //TODO: mantener el dialog abierto mientras este campo falte
    }
    this.usersService.modifyUser(tmpForm, this.usuarioID).subscribe({
      next: (response: any) => {
        //console.log(response);
        this.snackBar.openFromComponent(MessageSnackBarComponent, {
          duration: 3500,
          data: response.mensaje,
        });
      },
      error: (e: any) => {
        //console.error(e.message);
        Swal.fire(
          'Error al modificar usuario',
          'Razón: ' + e.message + '. Consulta con el administrador, por favor.',
          'error'
        );
      },
    });
  }
}
