import { Component, OnInit } from '@angular/core';
import { verification } from '../../../../config/verification';

@Component({
  selector: 'app-manage-menu',
  templateUrl: './manage-menu.component.html',
  styleUrls: ['./manage-menu.component.scss']
})
export class ManageMenuComponent implements OnInit {

  constructor() { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  visibility:boolean = false;

  ngOnInit(): void 
  {
    this.visibility = verification();
  }
}
