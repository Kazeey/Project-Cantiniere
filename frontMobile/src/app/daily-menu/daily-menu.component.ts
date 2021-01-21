import { Component, OnInit } from '@angular/core';
import { verification } from '../../../../config/verification';

@Component({
  selector: 'app-daily-menu',
  templateUrl: './daily-menu.component.html',
  styleUrls: ['./daily-menu.component.scss'],
})
export class DailyMenuComponent implements OnInit {

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  constructor() { }

  ngOnInit() {
    this.isConnected = verification();
  }

  ngOnDestroy():void
  {
    this.isConnected = false;
  }
}
