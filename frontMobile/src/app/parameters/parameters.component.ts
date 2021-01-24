
import { verification } from '../../../../config/verification';
import { Component, OnInit, PipeTransform } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { constantes } from '../../../../config/constantes';
import { ManageUserService } from '../services/manage-user/manage-user.service';



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

  
  userId: any;
  statut:any;
  public usersData;
  public imageModifiee;
  url:any ;
  imgPath:any = null;

  constructor(private parametersService:ParametersService, private modalService: NgbModal, private manageUserService:ManageUserService) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  public isConnected:boolean = false;
  public notifsCheck:boolean;
    
  displayComponent = true; // Variable d'affichage des components pour la déconnexion

  nbEssaisConnexion = constantes.nbEssaisConnexion;

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

    this.usersData = this.getUsersData(this.userId);
    
    this.listConstraints = this.displayAllConstraints();
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
    this.returnedValues = null;
    this.statut = "visiteur";
    this.displayComponent = false;
    this.nbEssaisConnexion = constantes.nbEssaisConnexion;
    this.resetStorage();
  }

  resetStorage()
  {
    localStorage.clear();
    console.log("localStorage cleared");
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

  getUsersData(userId){
  return this.manageUserService.getUserById(userId)
  }

  onSelectFile(event) {

    if (event.target.files && event.target.files[0]) {
      var reader = new FileReader();

      reader.readAsDataURL(event.target.files[0]); // read file as data url

      reader.onload = (event) => { // called once readAsDataURL is completed
        this.url = event.target.result;
      }
    }
    this.manageUserService.getUserById(this.userId)
    .subscribe(res => {
      let userId = res[0].id;
      this.updateImg(userId, this.url, this.imgPath)
      .subscribe(res =>{
       return this.getUsersData(userId);
      });
    });
  }

  updateImg(userId, url, imgPath){
    return this.parametersService.updateImg(userId, url, imgPath);
  }

  notifications(event){
    if(event.srcElement.checked == true){
      this.notifsCheck = true;
            console.log(this.notifsCheck);

    }else{
      this.notifsCheck = false;
      console.log(this.notifsCheck);
    }
  }

}
