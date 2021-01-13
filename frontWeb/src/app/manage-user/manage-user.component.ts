import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { ManageUserService } from '../services/manage-user/manage-user.service';
import { constantes } from '../../../../config/constantes';

@Component({
  selector: 'app-manage-user',
  templateUrl: './manage-user.component.html',
  styleUrls: ['./manage-user.component.scss']
})
export class ManageUserComponent implements OnInit {

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  // Variable de modification des utilisateurs
  public listUsers; 
  public simpleUser;

  constructor(private manageUserService:ManageUserService) { }

  ngOnInit(): void 
  {
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

  displayOneUser(userId)
  {
    this.simpleUser = this.manageUserService.getUserById(userId);
  }
}
