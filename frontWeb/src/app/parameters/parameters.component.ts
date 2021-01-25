import { ManageUserService } from '../services/manage-user/manage-user.service';
import { constantes } from '../../../../config/constantes';
import { Component, OnInit, PipeTransform } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { verification } from '../../../../config/verification';
import { ParametersService } from '../services/parameters/parameters.service';


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

  constructor(private parametersService:ParametersService,
              private modalService: NgbModal) { }

 
  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  public isConnected:boolean = false;

  public listConstraints = null;

  public returnedValues = null;

  public canSee;

  closeResult = '';

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
      this.canSee = false;
    }
    else
    {
      this.isConnected = false;
    }
    
    this.listConstraints = this.displayAllConstraints();
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
    this.returnedValues = null;
    this.canSee = false;
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
