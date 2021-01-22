import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { ManageUserService } from '../services/manage-user/manage-user.service';
import { AuthenticationService } from '../services/authentication/authentication.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-manage-user',
  templateUrl: './manage-user.component.html',
  styleUrls: ['./manage-user.component.scss']
})
export class ManageUserComponent implements OnInit {

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  public isConnected:boolean = false;
  public canSee:boolean;

  public isLookingFor:boolean =false;

  public closeResult = '';
  public quantity;

  // Variable de modification des utilisateurs
  public listUsers; 
  public simpleUser;
  public numbers;
  public update;  
  
  constructor(private manageUserService:ManageUserService, private modalService: NgbModal, private authenticationService:AuthenticationService) { }

  ngOnInit(): void 
  {
    // this.numbers = Array(3).fill(0); // Pour forcer une boucle pour le débug du css
    this.isConnected = verification();
    let state = localStorage.getItem("role");
    
    if (this.isConnected == true && state == "admin")
    {
      this.canSee = true;
    }
    else if (this.isConnected == true && state == "client")
    {
      this.canSee = false
    }
    else
    {
      this.isConnected = false;
    }
    this.listUsers = this.displayAllUsers();
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
    this.isLookingFor = false;
  }

  displayAllUsers()
  {
    return this.manageUserService.getAllUsers();
  }

  displaySearchedUser(userName)
  {
    if(userName == "")
    {
      this.isLookingFor = false;
    }
    else
    {
      this.isLookingFor = true;
    }
    this.listUsers = this.manageUserService.getUserBySearchField(userName);
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

  saveUser(id, name, firstname, sex, mail, phone, role, address, town, postalCode, wallet, status, userName)
  {
    this.manageUserService.saveUser(id, name, firstname, sex, mail, phone, role, address, town, postalCode, wallet, status)
    .subscribe(res => this.listUsers = this.displayAllUsers());

    this.quantity = "";
  }

  blockUser(userMail, formMail)
  {
    if(userMail == formMail)
    {
      console.log(userMail);
      this.authenticationService.blockAccount(userMail)
      .subscribe(res => {});

      this.quantity = "";
      this.listUsers = this.displayAllUsers();
    }
    else
    {
      // TODO : In*sérer notif pour dire que ça va pas
    }
  }

  activeUser(userMail, formMail)
  {
    if(userMail == formMail)
    {
      console.log(userMail);
      this.authenticationService.activeAccount(userMail)
      .subscribe(res => {});

      this.quantity = "";
      this.listUsers = this.displayAllUsers();
    }
    else
    {
      // TODO : Insérer notif pour dire que ça va pas
    }
  }
}
