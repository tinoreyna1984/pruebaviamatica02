import { Component, inject } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import Swal from 'sweetalert2';
import { MessageSnackBarComponent } from 'src/app/shared/components/message-snack-bar/message-snack-bar.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UsersService } from '../../service/users.service';

@Component({
  selector: 'app-add-user',
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.css']
})
export class AddUserComponent {

  private usersService = inject(UsersService);
  private snackBar = inject(MatSnackBar);

  formAddUsuario: FormGroup;

  constructor() {
    this.formAddUsuario = new FormGroup({
      name: new FormControl(),
      lastName: new FormControl(),
      username: new FormControl(),
      password: new FormControl(),
      accessId: new FormControl(),
      role: new FormControl(),
    });
  }

  onAddUsuario(){
    let tmpForm: any = this.formAddUsuario.value;
    const { role } = this.formAddUsuario.value;
    if(!role)
      tmpForm = {...tmpForm, role: 'USER'};
    //console.log(tmpForm);
    this.usersService.addUser(tmpForm).subscribe(
      {
        next: (response: any) => {
          //console.log(response);
          this.snackBar.openFromComponent(MessageSnackBarComponent, {
            duration: 3500,
            data: response.mensaje,
          });
        },
        error: (e:any) => {
          console.error(e.message);
          Swal.fire('Error al agregar usuario', "Razón: " + e.message + ". Consulta con el administrador, por favor.", 'error' );
        }
      }
    )
  }

}

