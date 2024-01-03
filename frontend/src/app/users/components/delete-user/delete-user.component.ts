import { Component, Inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MessageSnackBarComponent } from 'src/app/shared/components/message-snack-bar/message-snack-bar.component';
import { User } from 'src/app/shared/interfaces/user.interface';
import { UsersService } from '../../service/users.service';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-delete-user',
  templateUrl: './delete-user.component.html',
  styleUrls: ['./delete-user.component.css']
})
export class DeleteUserComponent {
  constructor(
    @Inject(MAT_DIALOG_DATA) private userID: string,
    private usersService: UsersService,
    private snackBar: MatSnackBar
  ) {}

  errorMsg: string = '';
  usuario?: User;

  onBorrarUsuario() {
    this.usersService.borrarUser(this.userID).subscribe(
      {
        next: (response: any) => {
          console.log(response);
          this.snackBar.openFromComponent(MessageSnackBarComponent, {
            duration: 3500,
            data: response.mensaje,
          });
        },
        error: (e:any) => {
          console.error(e);
          let status: number = e.status;
          this.errorMsg = e.error;
          if(status >= 500){
            Swal.fire('Error al borrar usuario', "Razón: " + this.errorMsg + ". Consulta con el administrador, por favor", 'error' );
          }
          else if(status >=400 && status < 500){
            Swal.fire('Error al borrar usuario', this.errorMsg, 'error' );
          }
          else{
            Swal.fire('Error al borrar usuario', "Error desconocido. Consulta con el administrador, por favor.", 'error' );
          }
        }
      }
      
    );
  }
}
