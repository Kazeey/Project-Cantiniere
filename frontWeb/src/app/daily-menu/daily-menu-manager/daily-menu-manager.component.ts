import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-daily-menu-manager',
  templateUrl: './daily-menu-manager.component.html',
  styleUrls: ['./daily-menu-manager.component.scss']
})
export class DailyMenuManagerComponent implements OnInit {
  meals: String[] = ["Quiche", "Steak Frite", "Fish & Chips"];
  drinks: String[] = ["Coca Cola", "Ice Tea"];

  constructor() { }

  ngOnInit(): void {
  }

}
