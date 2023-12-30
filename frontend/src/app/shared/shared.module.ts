import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Error404Component } from './pages/error404/error404.component';
import { MessageSnackBarComponent } from './components/message-snack-bar/message-snack-bar.component';
import { SpinnerComponent } from './components/spinner/spinner.component';
import { MaterialModule } from '../material/material.module';



@NgModule({
  declarations: [
    Error404Component,
    MessageSnackBarComponent,
    SpinnerComponent
  ],
  imports: [
    CommonModule,
    MaterialModule
  ],
  exports: [
    MessageSnackBarComponent,
    SpinnerComponent,
  ]
})
export class SharedModule { }
