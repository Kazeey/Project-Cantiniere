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
  isConnected:boolean = false;
  userId: any;
  public usersData;
  public ordersData;
  public simpleUser;
  statut: any;
  public orders;
  index: number = 0;

  constructor(private historicService:HistoricService) { }

  ngOnInit(): void 
  {
    this.statut = localStorage.getItem("statut");
    this.userId = localStorage.getItem("userId");
    this.isConnected = verification();
    this.ordersData = this.getOrdersData(this.userId);
    this.orders = this.getAllOrders();
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
