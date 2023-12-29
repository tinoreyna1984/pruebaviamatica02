import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { AboutComponent } from './pages/about/about.component';
import { MainPageComponent } from './pages/main-page/main-page.component';

const routes: Routes = [
    {
      path: '',
      component: MainLayoutComponent,
      children: [
        {
          path: 'main',
          component: MainPageComponent,
        },
        {
          path: 'users',
          loadChildren: () => import('../users/users.module').then( m => m.UsersModule ),
        },
        {
          path: 'about',
          component: AboutComponent,
        },
        {
          path: '**',
          redirectTo: 'main',
        },
      ]
    },
  ];
  
  @NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
  })
  export class MainRoutingModule { }