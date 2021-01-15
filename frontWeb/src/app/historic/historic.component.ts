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

  constructor(private historicService:HistoricService,
                     private manageUserService:ManageUserService) { }

  ngOnInit(): void 
  {
    this.userId = localStorage.getItem("userId");
    this.isConnected = verification();
    this.usersData = this.getUserData(this.userId);
    this.getOrdersData(this.userId);
    console.log(this.userId);
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }

  getUserData(userId)
  {
    this.simpleUser = this.manageUserService.getUserById(userId);    
  }

  getOrdersData(userId)
  {
   this.ordersData = this.historicService.getOrderByUser(userId);
   console.log(this.ordersData)
  }

}
