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

  constructor(private manageUserService:ManageUserService) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  visibility:boolean = false;

  // Variable de modification des utilisateurs
  public listManageUsers; 

  ngOnInit(): void 
  {
    this.visibility = verification();
  }

}
