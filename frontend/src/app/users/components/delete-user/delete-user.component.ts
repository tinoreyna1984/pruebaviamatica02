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

  usuario?: User;

  onBorrarUsuario() {
    this.usersService.borrarUser(this.userID).subscribe(
      {
        next: (response: any) => {
          this.snackBar.openFromComponent(MessageSnackBarComponent, {
            duration: 3500,
            data: response.mensaje,
          });
        },
        error: (e:any) => {
          //console.error(e.message);
          Swal.fire('Error al borrar usuario', "Razón: " + e.message + ". Consulta con el administrador, por favor.", 'error' );
        }
      }
      
    );
  }
}
