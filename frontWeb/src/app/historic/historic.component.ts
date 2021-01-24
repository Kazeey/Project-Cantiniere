import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { HistoricService } from '../services/historic/historic.service';
import { ManageUserService } from '../services/manage-user/manage-user.service';
import { constantes } from '../../../../config/constantes';

@Component({
  selector: 'app-historic',
  templateUrl: './historic.component.html',
  styleUrls: ['./historic.component.scss']
})
export class HistoricComponent implements OnInit {

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  public isConnected:boolean = false;
  public canSee:boolean;

  public userId: any;
  public usersData;
  public ordersData;
  public simpleUser;
  public orders;

  public statut: any;
  public index: number = 0;

  constructor(private historicService:HistoricService) { }

  ngOnInit(): void 
  {
    this.statut = localStorage.getItem("statut");
    this.userId = localStorage.getItem("userId");
    this.isConnected = verification();
    
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

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }

  getOrdersData(userId)
  {
   return this.historicService.getOrderByUser(userId)
  }

  getAllOrders()
  {
    return this.historicService.getAllOrders();
  }

}
