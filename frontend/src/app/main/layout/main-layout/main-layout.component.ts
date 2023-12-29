import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/auth/services/auth.service';

@Component({
  selector: 'app-main-layout',
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']
})
export class MainLayoutComponent implements OnInit {
  constructor(private authService: AuthService){}

  links: any;

  ngOnInit(): void {
    this.links = this.authService.getRoutes();
    console.log(this.links);
  }

  onLogout(){
    this.authService.logout();
  }
}
