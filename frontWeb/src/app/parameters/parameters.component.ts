import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { ParametersService } from '../services/parameters/parameters.service';
import { constantes } from '../../../../config/constantes';

@Component({
  selector: 'app-parameters',
  templateUrl: './parameters.component.html',
  styleUrls: ['./parameters.component.scss']
})
export class ParametersComponent implements OnInit {

 

  constructor() { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  // Variable de modification des paramètres
  public listParameters; 

  ngOnInit(): void 
  {
    this.isConnected = verification();
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }
}
