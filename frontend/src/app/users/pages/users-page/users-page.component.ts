import { Component, ViewChild, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { User } from 'src/app/shared/interfaces/user.interface';
import { MatPaginator } from '@angular/material/paginator';
import Swal from 'sweetalert2';
import { AuthService } from 'src/app/auth/services/auth.service';
import { UsersService } from '../../service/users.service';
import { AddUserComponent } from '../../components/add-user/add-user.component';
import { ModifyUserComponent } from '../../components/modify-user/modify-user.component';
import { DeleteUserComponent } from '../../components/delete-user/delete-user.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MessageSnackBarComponent } from 'src/app/shared/components/message-snack-bar/message-snack-bar.component';

@Component({
  selector: 'app-users-page',
  templateUrl: './users-page.component.html',
  styleUrls: ['./users-page.component.css'],
})
export class UsersPageComponent {
  constructor(
    private usersService: UsersService,
    private authService: AuthService,
    private usuario: MatDialog
  ) {}

  loading: boolean = false;
  isAdminFlag: boolean = false;
  private snackBar = inject(MatSnackBar);

  public dataSource: MatTableDataSource<User> = new MatTableDataSource<User>(
    []
  );

  displayedColumns: string[] = [
    'id',
    'accessId',
    'username',
    'email',
    'nombreCompleto',
    'role',
    'habilitado',
    'modificar',
    //'habilitar',
    'borrar',
  ];

  errorMsg: string = '';
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  archivoSeleccionado: any = null;

  ngOnInit(): void {
    //console.log('Invocar servicio de ventas...');
    this.loading = true;
    this.isAdminFlag = this.authService.isAdmin();
    this.usersService.getUsers().subscribe({
      next: (res: any) => {
        //console.log(res.usuarios);
        this.dataSource = new MatTableDataSource<User>(res.usuarios);
        this.dataSource.paginator = this.paginator;
        this.loading = false;
      },
      error: (e: any) => {
        console.error(e);
        Swal.fire(
          'Error en la carga',
          'Razón: ' + e.message + '. Consulta con el administrador, por favor.',
          'error'
        );
        this.loading = false;
      },
    });
  }

  onFileSelected(event: any): void {
    this.archivoSeleccionado = event.target.files[0];
  }

  onLoadFile() {
    const formData = new FormData();
    if (this.archivoSeleccionado === null) return;
    formData.append('archivo', this.archivoSeleccionado);
    this.loading = true;
    this.usersService.batchLoad(formData).subscribe({
      next: (response: any) => {
        console.log(response);
        this.snackBar.openFromComponent(MessageSnackBarComponent, {
          duration: 3500,
          data: response,
        });
        this.usersService.getUsers().subscribe({
          next: (res: any) => {
            console.log(res.usuarios);
            this.dataSource = new MatTableDataSource<User>(res.usuarios);
            this.dataSource.paginator = this.paginator;
          },
          error: (e: any) => {
            console.error(e);
            Swal.fire(
              'Error en la carga',
              'Razón: ' +
                e.message +
                '. Consulta con el administrador, por favor.',
              'error'
            );
          },
        });
        this.loading = false;
      },
      error: (e: any) => {
        console.error(e);
        let status: number = e.status;
        this.errorMsg = e.error;
        if (status >= 500) {
          Swal.fire(
            'Error al cargar en lotes',
            'Razón: ' +
              this.errorMsg +
              '. Consulta con el administrador, por favor',
            'error'
          );
        } else if (status >= 400 && status < 500) {
          Swal.fire('Error al cargar en lotes', this.errorMsg, 'error');
        } else {
          Swal.fire(
            'Error al agregar usuario',
            'Error desconocido. Consulta con el administrador, por favor.',
            'error'
          );
        }
        this.loading = false;
      },
    });
  }

  openAgregarUsuario() {
    const dialogRef = this.usuario.open(AddUserComponent, {
      enterAnimationDuration: 250,
      exitAnimationDuration: 250,
    });

    // después de cerrar, refresca las ventas
    dialogRef.afterClosed().subscribe(() => {
      this.loading = true;
      setTimeout(() => {
        this.usersService.getUsers().subscribe({
          next: (users: User[]) => {
            /* this.dataSource.data = users;
            this.loading = false; */
            this.usersService.getUsers().subscribe({
              next: (res: any) => {
                //console.log(res.usuarios);
                this.dataSource = new MatTableDataSource<User>(res.usuarios);
                this.dataSource.paginator = this.paginator;
                this.loading = false;
              },
              error: (e: any) => {
                console.error(e);
                Swal.fire(
                  'Error en la carga',
                  'Razón: ' +
                    e.message +
                    '. Consulta con el administrador, por favor.',
                  'error'
                );
                this.loading = false;
              },
            });
          },
          error: (e: any) => {
            //console.error(e.message);
            this.loading = false;
            Swal.fire(
              'Error en la carga',
              'Razón: ' +
                e.message +
                '. Consulta con el administrador, por favor.',
              'error'
            );
            this.loading = false;
          },
        });
      }, 1800);
    });
  }

  openModificarUsuario(usuarioID: string) {
    const dialogRef = this.usuario.open(ModifyUserComponent, {
      data: usuarioID,
      enterAnimationDuration: 250,
      exitAnimationDuration: 250,
    });

    // después de cerrar, refresca las ventas
    dialogRef.afterClosed().subscribe(() => {
      this.loading = true;
      setTimeout(() => {
        this.usersService.getUsers().subscribe({
          next: (usuarios: User[]) => {
            /* this.dataSource.data = usuarios;
            this.loading = false; */
            this.usersService.getUsers().subscribe({
              next: (res: any) => {
                //console.log(res.usuarios);
                this.dataSource = new MatTableDataSource<User>(res.usuarios);
                this.dataSource.paginator = this.paginator;
                this.loading = false;
              },
              error: (e: any) => {
                console.error(e);
                Swal.fire(
                  'Error en la carga',
                  'Razón: ' +
                    e.message +
                    '. Consulta con el administrador, por favor.',
                  'error'
                );
                this.loading = false;
              },
            });
          },
          error: (e: any) => {
            //console.error(e.message);
            Swal.fire(
              'Error en la carga',
              'Razón: ' +
                e.message +
                '. Consulta con el administrador, por favor.',
              'error'
            );
            this.loading = false;
          },
        });
      }, 1800);
    });
  }

  openBorrarUsuario(usuarioID: string) {
    const dialogRef = this.usuario.open(DeleteUserComponent, {
      data: usuarioID,
      enterAnimationDuration: 250,
      exitAnimationDuration: 250,
    });

    // después de cerrar, refresca las ventas
    dialogRef.afterClosed().subscribe(() => {
      this.loading = true;
      setTimeout(() => {
        this.usersService.getUsers().subscribe({
          next: (usuarios: User[]) => {
            /* this.dataSource.data = usuarios;
            this.loading = false; */
            this.usersService.getUsers().subscribe({
              next: (res: any) => {
                //console.log(res.usuarios);
                this.dataSource = new MatTableDataSource<User>(res.usuarios);
                this.dataSource.paginator = this.paginator;
                this.loading = false;
              },
              error: (e: any) => {
                console.error(e);
                Swal.fire(
                  'Error en la carga',
                  'Razón: ' +
                    e.message +
                    '. Consulta con el administrador, por favor.',
                  'error'
                );
                this.loading = false;
              },
            });
          },
          error: (e: any) => {
            //console.error(e.message);
            Swal.fire(
              'Error en la carga',
              'Razón: ' +
                e.message +
                '. Consulta con el administrador, por favor.',
              'error'
            );
            this.loading = false;
          },
        });
      }, 1800);
    });
  }
}
