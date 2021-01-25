import { Component, OnInit } from '@angular/core';
import { verification } from '../../../../config/verification';

@Component({
  selector: 'app-daily-order',
  templateUrl: './daily-order.component.html',
  styleUrls: ['./daily-order.component.scss'],
})
export class DailyOrderComponent implements OnInit {

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  public isConnected:boolean = false;
  public canSee: boolean = false;

  constructor() { }

  ngOnInit() {
    let state = localStorage.getItem("role");
    
    if (this.isConnected == true && state == "admin")
    {
      this.canSee = true;
    }
    else if (this.isConnected == true && state == "client")
    {
      this.canSee = false
    }
    else
    {
      this.isConnected = false;
    }
  }

  ngOnDestroy():void
  {
    this.isConnected = false;
  }
}
