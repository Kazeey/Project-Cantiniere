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
  // Valeurs possibles pour statut = client/admin/visiteur, change l'affichage en fonction
  statut = "visiteur"; 

  // Assignation des differents menus après vérifications de l'utilisateur
  communs = menus.menusCommuns;

  // Variable d'affichage du modal d'authentification
  isDisplayAuthentication = false;

  toSend = "";

  ngOnInit():void
  {
    
  }

  ngOnDestroy():void // A utiliser en tant que deconnexion
  {
    this.statut = "visiteur"; // Repasse le client/admin en simple visiteur 
    this.resetStorage() // Vide toute les valeurs du localStorage
  }

  resetStorage()
  {
    localStorage.clear(); // Vide le localStorage
  }

  showStorage()
  {
    console.log({...localStorage}); // TODO : tester chaque valeur du localStorage
  }
 
  toggleDisplayAuthentication() // Permet d'afficher le conteneur de connexion
  {
    this.isDisplayAuthentication = !this.isDisplayAuthentication; // Assigne le contraire de la variable pour l'affichage de la connexion
    let box:HTMLElement = document.getElementById("routingConnectionBox"); // Récupère le bouton "se connecter" pour en modifier le style
    
    // Modification du style du bouton 
    if(box.style.backgroundColor == "rgb(24, 144, 255)")
    {
      box.style.backgroundColor = "#001529";
    }
    else
    {
      box.style.backgroundColor = "#1890ff";
    }
  }

  testTTLConnection() // Permet de tester la validité du localStorage pour les variables de session, sûrement à mettre sur chaque page
  {
    let timeNow = String(Date.now()); // Récupère le timestamp pour comparer par rapport au TTL des variables de session

    if(parseInt(timeNow) > parseInt(localStorage.getItem("timeDestruction")))
    {
      localStorage.setItem("connected", "false");
    }
  }

  checkConnection(mail, password) // Fonction enclenchée lorsque l'on clique sur le bouton "Se connecter"
  {
    let errorZone:HTMLElement = document.getElementById("errorZone");

    // A terme, appeler l'api JAVA pour vérfier les paramètres envoyés, sûrement retourner un IdUtilisateur
    let testMail:String = "test";
    let testPassword:String = "test";

    if(mail == testMail && password == testPassword)
    {
      let timeDestruction = String(Date.now() + 900000); // set le timestamp de destruction a "timestamp actuel + 15 min"
      localStorage.setItem("timeDestruction", timeDestruction); // Insère le timestamp de destruction dans le localStorage
      localStorage.setItem("connected", "true");
    }
    else
    {

    }
    
    // Vérifie les inputs du formulaire
    // Si oui
    // Ajoute le TTL des variables de stockage dans le localStorage
    // Si non
    // Retourne un message d'erreur avec nombre de vérification restantes, à gérer depuis l'API node 

  }
}


