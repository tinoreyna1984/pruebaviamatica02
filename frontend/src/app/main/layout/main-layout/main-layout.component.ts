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
  userRealName: string | null = '';

  ngOnInit(): void {
    this.links = this.authService.getRoutes();
    this.userRealName = this.authService.getUserRealNameFromToken();
    console.log(this.links);
  }

  onLogout(){
    this.authService.logout();
  }
}
