import { Component, OnInit, PipeTransform } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormControl } from '@angular/forms';

import { verification } from '../../../../config/verification';
import { ParametersService } from '../services/parameters/parameters.service';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';

interface Constraint {
  id: number; 
  orderTimeLimit: String;
  maximumOrderPerDay: number;
  rateVAT : number;
}

@Component({
  selector: 'app-parameters',
  templateUrl: './parameters.component.html',
  styleUrls: ['./parameters.component.scss']
})
export class ParametersComponent implements OnInit {

  constructor(private parametersService:ParametersService) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  public isConnected:boolean = false;

  // Variable de modification des paramètres
  public listConstraints;

  ngOnInit(): void 
  {
    this.isConnected = verification();
    this.listConstraints = this.getAllConstraints();
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }

  getAllConstraints()
  {
    return this.parametersService.getAllConstraints();
  }

}
