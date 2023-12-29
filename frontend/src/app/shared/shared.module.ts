import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Error404Component } from './pages/error404/error404.component';
import { MessageSnackBarComponent } from './components/message-snack-bar/message-snack-bar.component';
import { SpinnerComponent } from './components/spinner/spinner.component';



@NgModule({
  declarations: [
    Error404Component,
    MessageSnackBarComponent,
    SpinnerComponent
  ],
  imports: [
    CommonModule
  ],
  exports: [
    MessageSnackBarComponent,
    SpinnerComponent,
  ]
})
export class SharedModule { }
