import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MenuService } from 'ng-zorro-antd/menu';
import { methods as menus }  from '../../../config/menus'; // Import des differents menus depuis le dossier config, commun au front et au web

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
  isCollapsed = false;
 
  // TODO : faire en sorte que l'opération de vérification de connexion arrive ici
  // valeurs possibles pour statut = client/admin/visiteur, change l'affichage en fonction
  statut = "visiteur"; 

  // assignation des differents menus après vérifications de l'utilisateur
  communs = menus.menusCommuns;

  toSend = "";
}


