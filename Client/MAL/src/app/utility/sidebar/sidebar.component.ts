import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  sidemenus:any = [
    {routerLink:'home',linkName:'Home',linkIconName:'home'},
    {routerLink:'run-test',linkName:'Run Test',linkIconName:'play_arrow'},
    {routerLink:'config',linkName:'Config',linkIconName:'settings_applications'}
  ];  
  constructor() { }

  ngOnInit() {
  }

}
