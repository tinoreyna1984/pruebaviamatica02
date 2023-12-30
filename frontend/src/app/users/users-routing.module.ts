import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UsersLayoutComponent } from './layout/users-layout/users-layout.component';


const routes: Routes = [
    {
      path: '',
      component: UsersLayoutComponent,
    },
  ];
  
  @NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
  })
  export class UsersRoutingModule { }