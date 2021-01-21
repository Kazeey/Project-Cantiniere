import { Component, OnInit, PipeTransform } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormControl } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { setTimeout } from 'timers';

import { verification } from '../../../../config/verification';
import { ParametersService } from '../services/parameters/parameters.service';
import { Observable } from 'rxjs';

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

  constructor(private parametersService:ParametersService, private modalService: NgbModal) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  public isConnected:boolean = false;

  // Variable de modification des paramètres
  public listConstraints = null;

  public returnedValues = null;

  closeResult = '';

  ngOnInit(): void 
  {
    this.isConnected = verification();
    
    if(this.isConnected == false)
    {
      localStorage.clear();
    }
    
    this.listConstraints = this.displayAllConstraints();
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
    this.returnedValues = null;
  }

  displayAllConstraints()
  {
    return this.parametersService.getAllConstraints();
  }
    
  open(content) 
  {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed`;
    }, 
    (reason) => {
      this.closeResult = `Dismissed`;
    });
  }

  addConstraint(orderTimeLimit, maximumOrderPerDay, rateVAT)
  {
    this.parametersService.addConstraint(orderTimeLimit, maximumOrderPerDay, rateVAT)
    .subscribe(res => this.listConstraints = this.displayAllConstraints());
  }

  editConstraint(constraintId, orderTimeLimit, maximumOrderPerDay, rateVAT)
  {
    this.parametersService.editConstraint(constraintId, orderTimeLimit, maximumOrderPerDay, rateVAT)
    .subscribe(res => this.listConstraints = this.displayAllConstraints());
    // Réassignation de la fonction pour actualisation dans le front
  }
 
  deleteConstraint(constraintId)
  {
    this.parametersService.deleteConstraint(constraintId)
    .subscribe(res => this.listConstraints = this.displayAllConstraints());
  }

}
