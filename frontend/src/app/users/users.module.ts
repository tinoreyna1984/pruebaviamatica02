import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UsersLayoutComponent } from './layout/users-layout/users-layout.component';
import { UsersPageComponent } from './pages/users-page/users-page.component';
import { AddUserComponent } from './components/add-user/add-user.component';
import { ModifyUserComponent } from './components/modify-user/modify-user.component';
import { DeleteUserComponent } from './components/delete-user/delete-user.component';



@NgModule({
  declarations: [
    UsersLayoutComponent,
    UsersPageComponent,
    AddUserComponent,
    ModifyUserComponent,
    DeleteUserComponent
  ],
  imports: [
    CommonModule
  ]
})
export class UsersModule { }
