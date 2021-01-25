import { Component, OnInit } from '@angular/core';
import { verification } from '../../../../config/verification';
import { HistoricService } from '../services/historic/historic.service';

@Component({
  selector: 'app-historic',
  templateUrl: './historic.component.html',
  styleUrls: ['./historic.component.scss'],
})
export class HistoricComponent implements OnInit {

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;
  userId: any;
  public usersData;
  public ordersData;
  public orders;
  statut: any;
  
  constructor(private historicService:HistoricService) { }

    ngOnInit() {
    this.statut = localStorage.getItem("role");
    this.userId = localStorage.getItem("idUser");
    this.isConnected = verification();
    this.ordersData = this.getOrdersData(this.userId);
    this.orders = this.getAllOrders();
    
    }

 /* ngOnDestroy():void
  {
    this.isConnected = false;
  }*/

  getOrdersData(userId)
  {
   return this.historicService.getOrderByUser(userId).subscribe(res=>{
     console.log(res);
   });
  }

  getAllOrders()
  {
    return this.historicService.getAllOrders();
  }

}
