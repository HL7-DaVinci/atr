import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  headerMenus:any = [
    {routerLink:'home',linkName:'Home',linkIconName:'home'},
    {routerLink:'config',linkName:'Config',linkIconName:'settings_applications'}
  ];  
  constructor() { }

  ngOnInit() {
  }

}
