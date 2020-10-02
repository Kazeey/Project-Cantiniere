import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  isCollapsed = false;
 
  // TODO : faire en sorte que l'opération de vérification de connexion arrive ici
  // valeurs possibles pour statut = client/admin/visiteur, change l'affichage en fonction
  statut = "admin"; 
}

