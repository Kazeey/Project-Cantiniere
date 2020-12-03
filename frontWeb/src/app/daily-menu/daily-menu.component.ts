import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { DailyMenuService } from '../services/daily-menu/daily-menu.service';
import { constantes } from '../../../../config/constantes';
import { Menu } from '../interfaces/menu'

@Component({
  selector: 'app-daily-menu',
  templateUrl: './daily-menu.component.html',
  styleUrls: ['./daily-menu.component.scss'],
  providers : [
    DailyMenuService
  ]
})
export class DailyMenuComponent implements OnInit {

  constructor(private dailyMenuService:DailyMenuService) { }

  public weekNumber = 2;

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  // Variable qui reçoit la liste des menus de la journée
  dailyMenus: any;

  ngOnInit(): void {
    this.isConnected = verification(); 
    this.getDailyMenus(this.weekNumber);
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }

  getDailyMenus(weekNumber) {
    this.dailyMenuService.getDailyMenu(weekNumber)
      .subscribe(res => {
        console.log(res);
        this.dailyMenus = res;
      })
  }
}
