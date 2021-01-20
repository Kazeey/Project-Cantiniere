import { Component, OnInit, PipeTransform } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormControl } from '@angular/forms';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

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

  constructor(private parametersService:ParametersService, private modalService: NgbModal) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  public isConnected:boolean = false;

  // Variable de modification des paramètres
  public listConstraints;

  closeResult = '';

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
    
  open(content) 
  {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed`;
    }, 
    (reason) => {
      this.closeResult = `Dismissed`;
    });
  }

  addConstraint()
  {
    
  }

  editConstraint()
  {

  }

  deleteConstraint()
  {

  }

}
