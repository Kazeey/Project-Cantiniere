import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { PreferencesService } from '../services/preferences/preferences.service';
import { constantes } from '../../../../config/constantes';

@Component({
  selector: 'app-preferences',
  templateUrl: './preferences.component.html',
  styleUrls: ['./preferences.component.scss']
})
export class PreferencesComponent implements OnInit {

  constructor(private preferencesService:PreferencesService) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  // Variable de modification des préférences 
  public listPreferences; 

  public canSee:boolean;

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
      this.canSee = false
    }
    else
    {
      this.isConnected = false;
    }
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }
}
