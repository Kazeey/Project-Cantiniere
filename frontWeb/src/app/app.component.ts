import { Component, OnInit } from '@angular/core';

import { methods as menu }  from '../../../config/menus'; 
import { verification } from '../../../config/verification';
import { constantes } from '../../../config/constantes';

import { AuthenticationService } from '../app/services/authentication/authentication.service';
import { ManageUserService } from './services/manage-user/manage-user.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent implements OnInit{

  isCollapsed = false;

  public title = "Projet Cantinière";
 
  // Valeurs possibles pour statut = "client"/"admin"/"visiteur", change l'affichage en fonction
  public statut:string; 
  public displayMenu:String; 

  // Si true, redirige vers l'accès utilisateur
  // Pour éviter tout problème d'affichage avec la connexion
  public isConnected:boolean = false;
  public canSee:boolean;

  public usersData;
  public userId;

  // Assignation des differents menus après vérifications de l'utilisateur
  public communs = menu.menusCommuns;
  
  public isDisplayAuthentication = false; // Variable d'affichage du modal d'authentification
  public displayComponent = true; //  Variable d'affichage des components pour la déconnexion
  public box:HTMLElement = null; // Menu "Se connecter" sur la gauche

  public nbEssaisConnexion = constantes.nbEssaisConnexion; 
  public phraseConnexion:String = ""; // Phrase affichée dans la zone d'erreur

  public toSend = ""; // Faire passer une donnée statique entre plusieurs routes

  public role:string;
  public closeResult = '';
  
  constructor(private AuthenticationService:AuthenticationService,
    private manageUserService:ManageUserService, private modalService: NgbModal )
  {
    
  }
  
  ngOnInit():void // A chaque instanciation de la page, a voir pour la définir dans un fichier de config pour faciliter le bousin
  {
    this.showStorage()
    this.isConnected = verification();

    if(localStorage.getItem('role'))
    {
      this.statut = localStorage.getItem('role');
    }
    else
    {
      this.ngOnDestroy();
    }
  }

  ngOnDestroy():void // A utiliser en tant que deconnexion
  {
    this.statut = "visiteur"; // Repasse le client/admin en simple visiteur 
    this.displayComponent = false;
    this.nbEssaisConnexion = constantes.nbEssaisConnexion; // Reset du nombre d'essai a la constante d'import
    this.resetStorage() // Vide toute les valeurs du localStorage
    this.usersData = null;
  }

  resetStorage()
  {
    localStorage.clear(); // Vide le localStorage
    console.log("localStorage cleared.");
    this.showStorage();
  }

  showStorage()
  {
    console.log({...localStorage});
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

  open(content) 
  {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
      this.closeResult = `Closed`;
    }, 
    (reason) => {
      this.closeResult = `Dismissed`;
    });
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
          this.userId = localStorage.getItem("userId");
          this.usersData = this.getUserData(this.userId);
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
    let role:String = "admin"; // Rôle de l'utilisateur récupéré depuis l'API
    this.AuthenticationService.login(mail, password)
    .subscribe(res => {
      this.AuthenticationService.checkEmail(mail)
      .subscribe(resMail => {
        if(resMail == "true")
        {
          let data = JSON.parse(res)
          
          if(data.userError)
          {
            // Vérifie le nombre d'essais de connexion de l'utilisateur
            if(this.nbEssaisConnexion >= 0)
            {
              this.setMessage("Nombre d'essais restants : ", String(" " + this.nbEssaisConnexion));
              this.nbEssaisConnexion--;
            }
            else
            {          
              this.AuthenticationService.blockAccount(mail)
              .subscribe(res => {});
              this.setMessage("Votre compte est bloqué. Veuillez contacter l'administration.", null);
              this.nbEssaisConnexion--;
              return;
            }
          }
          else
          {    
            let time;

            // Vérifie le rôle de l'utilisateur (retourné par l'api Node)
            switch(data.role)
            {
              case 0:
                this.statut = "client";
                break;
              case 1:
                this.statut = "admin";
                break;
            }

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
            localStorage.setItem("userId", data.result[0].id); // TODO : récupérer l'id utilisateur et le passer dans le localStorage 
            localStorage.setItem("role", this.statut); // TODO : récupérer l'id utilisateur et le passer dans le localStorage  

            this.showStorage();

            // Adapte l'UI en fonction du rôle de l'utilisateur
            this.setMessage("", null);
            this.isDisplayAuthentication = !this.isDisplayAuthentication;
            this.isConnected = true;
          }      
        }
        else
        {
          this.setMessage("Adresse mail incorrect ou bloqué.", null);
        }
      });
    });
  }

  forgotPassword(mail)
  {
    this.AuthenticationService.forgotPassword(mail)
    .subscribe(res => {});

    this.setMessage("Si l'adresse mail existe, un mail a été envoyé (Pensez a vérifier vos spams).", null);
  }

  getUserData(userId){
    return this.manageUserService.getUserById(userId);
  }
}


// Dans les fichiers HTML :
// *ngIf = canSee est pour les admins
// *ngIf = !canSee est pour les clients
