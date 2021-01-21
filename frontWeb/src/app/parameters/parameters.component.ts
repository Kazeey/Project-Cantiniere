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

  userId: any;
  statut:any;
  public usersData;
  public imageModifiee;
  url:any ;
  imgPath:any = null;

  constructor(private manageUserService:ManageUserService,
              private parametersService:ParametersService,
              private modalService: NgbModal) { }

 
  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  public isConnected:boolean = false;

  // Variable de modification des paramètres
  public listParameters; 
  public notifsCheck:boolean;

  public listConstraints = null;

  public returnedValues = null;

  public canSee;

  closeResult = '';

  ngOnInit(): void 
  {
    this.statut = localStorage.getItem("statut");
    this.userId = localStorage.getItem("userId");
    this.isConnected = verification();
    this.getUsersData(this.userId);
    let state = localStorage.getItem("role");
    
    // TODO comprendre ce que c'est 
    if (this.isConnected == true && state == "admin")
    {

    }
    else if (this.isConnected == true && state == "client")
    {

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

  getUsersData(userId){
    this.usersData = this.manageUserService.getUserById(userId)
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
