import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { HistoricService } from '../services/historic/historic.service';
import { constantes } from '../../../../config/constantes';

@Component({
  selector: 'app-historic',
  templateUrl: './historic.component.html',
  styleUrls: ['./historic.component.scss']
})
export class HistoricComponent implements OnInit {

  constructor(private historicService:HistoricService) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  // Variable qui reçoit l'historique 
  public listHistoric; 

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
