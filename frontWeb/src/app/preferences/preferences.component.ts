import { Component, OnInit } from '@angular/core';
import { verification } from '../../../../config/verification';

@Component({
  selector: 'app-preferences',
  templateUrl: './preferences.component.html',
  styleUrls: ['./preferences.component.scss']
})
export class PreferencesComponent implements OnInit {

  constructor() { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  visibility:boolean = false;

  ngOnInit(): void 
  {
    this.visibility = verification();
  }

}
