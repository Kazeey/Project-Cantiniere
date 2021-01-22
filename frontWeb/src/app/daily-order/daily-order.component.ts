import { Component, NgModule, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { DailyOrderService } from '../services/daily-order/daily-order.service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-daily-order',
  templateUrl: './daily-order.component.html',
  styleUrls: ['./daily-order.component.scss'],
  providers : [
    DailyOrderService
  ]
})
export class DailyOrderComponent implements OnInit 
{
  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;
  public canSee:boolean;

  // Variable qui reçoit la liste des commandes de la journée
  public listDailyOrders; 

  public myDate: string;

  constructor(private dailyOrderService:DailyOrderService, private datePipe: DatePipe) { }

  ngOnInit(): void 
  {
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
    
    this.listDailyOrders = this.displayDailyOrders()
    this.myDate = this.datePipe.transform(new Date(),"yyyy-MM-dd")
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }

  displayDailyOrders()
  {
    //return this.dailyOrderService.getDailyOrder();
  }
}
