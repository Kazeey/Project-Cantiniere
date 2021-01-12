import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { HistoricService } from '../services/historic/historic.service';
import { constantes } from '../../../../config/constantes';
import { AccueilService } from '../../app/services/accueil.service';
import { Users } from '../../app/interfaces/users';
import { Orders } from '../../app/interfaces/order';

@Component({
  selector: 'app-historic',
  templateUrl: './historic.component.html',
  styleUrls: ['./historic.component.scss']
})
export class HistoricComponent implements OnInit {

  constructor(private historicService:HistoricService,
              private accueilService:AccueilService) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  usersData: any = {
    name : "Ramery",
    firstname : "Matthias",
    sex: 2,
    email: "matthias.ramery@gmail.com",
    role: "lunchLady",
    address: "test",
    town: "violaines",
    postalCode: 62138,
    wallet: 3,
    status: 0,
    imageId: 1
  };

  ordersData: any = {
    creationDate: "15-09-2012",
    quantity : {
      meal : {
        label : "Tartelette",
        priceDF : 6.83
      }
    }
  };

  ngOnInit(): void 
  {
    this.isConnected = verification();
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }

  getUsersData(paramsUser){
    this.accueilService.getUserById(paramsUser)
    .subscribe(res =>{
      console.log(res);
      //this.usersData = res;
    })
  }

  getOrdersData(paramsOrder){
    this.historicService.getOrderByUser(paramsOrder)
    .subscribe(res =>{
      console.log(res);
      //this.ordersData = res;
    })
  }

}
