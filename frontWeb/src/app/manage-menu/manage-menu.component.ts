import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { ManageMenuService } from '../services/manage-menu/manage-menu.service';
import { constantes } from '../../../../config/constantes';

@Component({
  selector: 'app-manage-menu',
  templateUrl: './manage-menu.component.html',
  styleUrls: ['./manage-menu.component.scss']
})
export class ManageMenuComponent implements OnInit {

  constructor(private manageMenuService:ManageMenuService) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  visibility:boolean = false;

  // Variable de modification des menus
  public listManageMenus; 

  ngOnInit(): void 
  {
    this.visibility = verification();
  }
}
