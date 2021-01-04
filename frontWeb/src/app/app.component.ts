import { Component, OnInit } from '@angular/core';

import { methods as menus }  from '../../../config/menus'; 
import { verification } from '../../../config/verification';
import { constantes } from '../../../config/constantes';
import { AuthenticationService } from './services/authentication/authentication.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
  isCollapsed = false;
 
  // TODO : Récupérer la valeur du user qui tente de se connecter
  // Valeurs possibles pour statut = "client"/"admin"/"visiteur", change l'affichage en fonction
  statut:String = "visiteur"; 

  // Si true, redirige vers l'accès utilisateur
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  // Assignation des differents menus après vérifications de l'utilisateur
  communs = menus.menusCommuns;
  
  isDisplayAuthentication = false; // Variable d'affichage du modal d'authentification
  displayComponent = true; //  Variable d'affichage des components pour la déconnexion
  box:HTMLElement = null; // Menu "Se connecter" sur la gauche

  nbEssaisConnexion = constantes.nbEssaisConnexion; 
  phraseConnexion:String = ""; // Phrase affichée dans la zone d'erreur

  toSend = ""; // Faire passer une donnée statique entre plusieurs routes

  // Passe ces 2 variables dans le localStorage pour les garder, ou du moins le userId
  connectedProfil : any;

  constructor(private AuthenticationService:AuthenticationService) { }
  
  ngOnInit():void // A chaque instanciation de la page, a voir pour la définir dans un fichier de config pour faciliter le bousin
  {
    this.showStorage()
    
    this.isConnected = verification();

    // A modifier en même temps que l'utilisation de la requête pour avoir le rôle (on fera certainement un passage JAVA > NODE > FRONT)
    if(this.isConnected)
    {
      this.statut = "admin"; // Pour le test on le passe a admin
    }
  }

  ngOnDestroy():void // A utiliser en tant que deconnexion
  {
    this.statut = "visiteur"; // Repasse le client/admin en simple visiteur 
    this.displayComponent = false;
    this.resetStorage() // Vide toute les valeurs du localStorage
  }

  resetStorage()
  {
    localStorage.clear(); // Vide le localStorage
    console.log("localStorage cleared.");
  }

  showStorage()
  {
    console.log({...localStorage}); // TODO : tester chaque valeur du localStorage
  }

  toggleDisplayAuthentication() // Permet d'afficher le conteneur de connexion
  {
    this.isDisplayAuthentication = !this.isDisplayAuthentication; // Assigne le contraire de la variable pour l'affichage de la connexion
    this.box = document.getElementById("routingConnectionBox"); // Récupère le bouton "se connecter" pour en modifier le style

    // Modification du style du bouton "Se connecter" lorsque l'on clique dessus
    if(this.box.style.backgroundColor == "rgb(24, 144, 255)")
    {
      this.box.style.backgroundColor = "#001529";
    }
    else
    {
      this.box.style.backgroundColor = "#1890ff";
    }
  }

  setMessage(message, nbEssais)
  {
    let errorZone:HTMLElement = document.getElementById("errorZone");
    this.phraseConnexion = message;

    if(nbEssais)
    {
      this.phraseConnexion += nbEssais;
    }
    
    errorZone.style.display = "block";

    if(message == "")
    {
      errorZone.style.display = "none";
    }    
  }

  verifInput(mail, password) 
  {
    this.setMessage("", null);

    if(mail) // vérifie la présence d'un mail
    {       
      const indexOfAt = mail.indexOf('@'); // vérifie la présence d'un "@"
      const indexOfPoint = mail.indexOf('.', indexOfAt); // vérifie la présence d'un "." après le "@", ce "." correspond au domaine qui gère l'envoi du mail
    
      if( indexOfAt > 0 && indexOfPoint > 0) // S'il y a un "@" et un "." après, alors lancement de la fonction de création de l'utilisateur
      { 
        if(password)
        {
          this.setMessage("", null);
          this.checkConnection(mail, password);
        }
        else
        {
          this.setMessage("Vous n'avez pas renseigné de mot de passe.", null);
        }
      }
      else // Sinon message d'erreur
      { 
        this.setMessage("Le format de l'addresse mail inscrite est incorrect ! Veuillez la modifier.", null);
      }
    }
    else
    {
      this.setMessage("Veuillez renseigner une adresse mail.", null);
    }
  }

  checkConnection(mail, password) // Fonction enclenchée lorsque l'on clique sur le bouton "Se connecter"
  {
    // A terme, appeler l'api JAVA pour vérfier les paramètres envoyés, sûrement retourner un IdUtilisateur
    let testMail:String = "quentinpeze@hotmail.fr";
    let testPassword:String = "test";
    
    let role:String = "admin"; // Rôle de l'utilisateur récupéré depuis l'API

    if(mail == testMail)
    {
      // TODO : Récupérer le statut du compte si possible pour voir si le compte est bloqué, sinon le définir dans variable locale
      // Avec si + 4 essais stockage données dans un tableau avec la valeur bloquée, et ensuite on compare
      if(password && password == testPassword)
      {
        let time;

        // Vérifie la valeur demandée lors de la config pour le temps de connexion d'un compte.
        switch (constantes.timeConnexion) 
        {
          case 0:
            time = 900000;
            break;
          case 1:
            time = ((900000 * 4) * 24);
            break;
        }

        let timeDestruction = String(Date.now() + time); // set le timestamp de destruction a "timestamp actuel + 15 min"
        localStorage.setItem("timeDestruction", timeDestruction); // Insère le timestamp de destruction dans le localStorage
        localStorage.setItem("connected", "true"); //Insère le fait que l'utilisateur soit connecté dans le localStorage
        // localStorage.setItem("idUser", id); // TODO : récupérer l'id utilisateur et le passer dans le localStorage
        
        this.login(mail, password);
        this.showStorage();

        // Adapte l'UI en fonction du rôle de l'utilisateur
        this.statut = role;
        this.setMessage("", null);
        this.isDisplayAuthentication = !this.isDisplayAuthentication;
      }
      else
      {
        // Vérifie le nombre d'essais de connexion de l'utilisateur
        if(this.nbEssaisConnexion >= 0)
        {
          this.setMessage("Nombre d'essais restants : ", String(" " + this.nbEssaisConnexion));
          this.nbEssaisConnexion--;
        }
        else
        {
          let time;

          switch (constantes.timeBlocked) 
          {
            case 0:
              time = "15 min";
              break;
            case 1:
              time = "24 h";
              break;
          }
          
          this.setMessage("Votre compte est bloqué. Veuillez réessayer dans ", time);
          this.nbEssaisConnexion--;
          return;
        }
      }
    }
    else
    {
      this.setMessage("Email incorrect(s).", null);
    }
  }

  login(email, password)
  {
    this.AuthenticationService.login(email, password)
    .subscribe(res => {
      console.log(res);
      this.connectedProfil = res;
    });

  }
}


