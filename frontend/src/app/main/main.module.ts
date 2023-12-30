import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { AboutComponent } from './pages/about/about.component';
import { MainRoutingModule } from './main-routing.module';
import { MainPageComponent } from './pages/main-page/main-page.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../material/material.module';
import { SharedModule } from '../shared/shared.module';



@NgModule({
  declarations: [
    MainLayoutComponent,
    AboutComponent,
    MainPageComponent
  ],
  imports: [
    CommonModule,
    MainRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    SharedModule
  ]
})
export class MainModule { }
