import { Component, ViewChild } from '@angular/core';
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

@Component({
  selector: 'app-users-page',
  templateUrl: './users-page.component.html',
  styleUrls: ['./users-page.component.css']
})
export class UsersPageComponent {
  constructor(
    private usersService: UsersService,
    private authService: AuthService,
    private usuario: MatDialog,
  ) {}
  
  loading: boolean = false;
  isAdminFlag: boolean = false;

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

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  ngOnInit(): void {
    //console.log('Invocar servicio de ventas...');
    this.loading = true;
    this.isAdminFlag = this.authService.isAdmin();
    this.usersService.getUsers().subscribe(
      {
        next: (usuarios: User[]) => {
          this.dataSource = new MatTableDataSource<User>(usuarios);
          this.dataSource.paginator = this.paginator;
          this.loading = false;
        },
        error: (e:any) => {
          //console.error(e.message);
          Swal.fire('Error en la carga', "Razón: " + e.message + ". Consulta con el administrador, por favor.", 'error' );
        }
      }
      
    );
  }

  
  openAgregarUsuario(){
    const dialogRef = this.usuario.open(AddUserComponent, {
      enterAnimationDuration: 250,
      exitAnimationDuration: 250,
    });

    // después de cerrar, refresca las ventas
    dialogRef.afterClosed().subscribe(() => {
      this.loading = true;
      setTimeout(() => {
        this.usersService.getUsers().subscribe(
          {
            next: (users: User[]) => {
              this.dataSource.data = users;
              this.loading = false;
            },
            error: (e:any) => {
              //console.error(e.message);
              this.loading = false;
              Swal.fire('Error en la carga', "Razón: " + e.message + ". Consulta con el administrador, por favor.", 'error' );
            }
          }
        );
      }, 1800);
    });
  }

  openModificarUsuario(usuarioID: string){
    const dialogRef = this.usuario.open(ModifyUserComponent, {
      data: usuarioID,
      enterAnimationDuration: 250,
      exitAnimationDuration: 250,
    });

    // después de cerrar, refresca las ventas
    dialogRef.afterClosed().subscribe(() => {
      this.loading = true;
      setTimeout(() => {
        this.usersService.getUsers().subscribe(
          {
            next: (usuarios: User[]) => {
              this.dataSource.data = usuarios;
              this.loading = false;
            },
            error: (e:any) => {
              //console.error(e.message);
              Swal.fire('Error en la carga', "Razón: " + e.message + ". Consulta con el administrador, por favor.", 'error' );
            }
          }
        );
      }, 1800);
    });}

  openBorrarUsuario(usuarioID: string){
    const dialogRef = this.usuario.open(DeleteUserComponent, {
      data: usuarioID,
      enterAnimationDuration: 250,
      exitAnimationDuration: 250,
    });

    // después de cerrar, refresca las ventas
    dialogRef.afterClosed().subscribe(() => {
      this.loading = true;
      setTimeout(() => {
        this.usersService.getUsers().subscribe(
          {
            next: (usuarios: User[]) => {
              this.dataSource.data = usuarios;
              this.loading = false;
            },
            error: (e:any) => {
              //console.error(e.message);
              Swal.fire('Error en la carga', "Razón: " + e.message + ". Consulta con el administrador, por favor.", 'error' );
            }
          }
        );
      }, 1800);
    });
  }
}
