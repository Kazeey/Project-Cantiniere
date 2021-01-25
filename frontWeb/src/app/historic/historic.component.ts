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
  public state;

  public statut: any;
  public index: number = 0;

  constructor(private historicService:HistoricService) { }

  ngOnInit(): void 
  {
    this.userId = localStorage.getItem("userId");
    this.isConnected = verification();
    this.orders = this.getAllOrders();
    this.ordersData = this.getOrdersData(this.userId);
    
    this.state = localStorage.getItem("role");
    
    if (this.isConnected == true && this.state == "admin")
    {
      this.canSee = true;
    }
    else if (this.isConnected == true && this.state == "client")
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
   return this.historicService.getOrderByUser(userId);
  }

  getAllOrders()
  {
    return this.historicService.getAllOrders();
  }

}
