import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { ManageProductService } from '../services/manage-product/manage-product.service';
import { constantes } from '../../../../config/constantes';

@Component({
  selector: 'app-manage-product',
  templateUrl: './manage-product.component.html',
  styleUrls: ['./manage-product.component.scss']
})
export class ManageProductComponent implements OnInit {

  constructor(private manageProductService:ManageProductService) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  // Variable de mofication des produits
  public listDailyOrders; 

  ngOnInit(): void 
  {
    this.isConnected = verification();

    if(this.isConnected == false)
    {
      localStorage.clear();
    }
    
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }


}
