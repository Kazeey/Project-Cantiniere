import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { ManageUserService } from '../services/manage-user/manage-user.service';
import { constantes } from '../../../../config/constantes';
import {NgbModal, ModalDismissReasons} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-manage-user',
  templateUrl: './manage-user.component.html',
  styleUrls: ['./manage-user.component.scss']
})
export class ManageUserComponent implements OnInit {

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;
  isLookingFor:boolean =false;
  closeResult = '';

  // Variable de modification des utilisateurs
  public listUsers; 
  public simpleUser;
  public numbers;

  constructor(private manageUserService:ManageUserService, private modalService: NgbModal) { }

  ngOnInit(): void 
  {
    this.numbers = Array(3).fill(0);
    this.isConnected = verification();
    this.listUsers = this.displayAllUsers();
    console.log(this.listUsers)
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
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
    this.simpleUser = this.manageUserService.getUserBySearchField(userName);
  }
  
  open(content) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }
}
