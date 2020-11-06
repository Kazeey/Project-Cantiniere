import { Component, OnInit } from '@angular/core';
import { verification } from '../../../../config/verification';
import { HttpClient } from '@angular/common/http';

import { DailyOrderService } from '../services/daily-order/daily-order.service';
import { constantes } from '../../../../config/constantes';

@Component({
  selector: 'app-daily-order',
  templateUrl: './daily-order.component.html',
  styleUrls: ['./daily-order.component.scss'],
  providers : [
    DailyOrderService
  ]
})
export class DailyOrderComponent implements OnInit {

  constructor(private dailyOrderService:DailyOrderService) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  // Variable qui reçoit la liste des commandes de la journée
  public listDailyOrders; 

  ngOnInit(): void 
  {
    this.isConnected = verification(); 
    this.listDailyOrders = this.displayDailyOrders()
  }

  displayDailyOrders()
  {
    return this.dailyOrderService.getDailyOrder();
  }
}
