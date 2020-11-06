import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { DailyMenuService } from '../services/daily-menu/daily-menu.service';
import { constantes } from '../../../../config/constantes';

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

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  // Variable qui reçoit la liste des menus de la journée
  public listDailyMenus; 

  ngOnInit(): void {
    this.isConnected = verification(); 
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }
}
