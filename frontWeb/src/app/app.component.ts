import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  isCollapsed = false;
 
  statut = "admin"; // TODO : faire en sorte que l'opération de vérification de connexion arrive ici

  constructor(){
    console.log(this.statut);
  }
}
