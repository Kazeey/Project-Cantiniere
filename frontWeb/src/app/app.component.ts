import { Component, OnInit } from '@angular/core';

import { methods as menu }  from '../../../config/menus'; 
import { verification } from '../../../config/verification';
import { constantes } from '../../../config/constantes';
import { AccueilService } from '../app/services/accueil.service';
import { Users } from '../app/interfaces/users';
import Swal from 'sweetalert2/dist/sweetalert2.js';
import { ParametersComponent } from './parameters/parameters.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent implements OnInit{

  constructor(private accueilService: AccueilService,
    private AuthenticationService:AuthenticationService ){
  }

  isCollapsed = false;
 
  // Valeurs possibles pour statut = "client"/"admin"/"visiteur", change l'affichage en fonction
  statut:String = "visiteur"; 

  // Si true, redirige vers l'accès utilisateur
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  usersData: any = {
    name : "Ramery",
    firstname : "Matthias",
    sex: 2,
    email: "matthias.ramery@gmail.com",
    role: "lunchLady",
    address: "test",
    town: "violaines",
    postalCode: 62138,
    wallet: 3,
    status: 0,
    imageId: 1
  };

  // Assignation des differents menus après vérifications de l'utilisateur
  communs = menu.menusCommuns;
  
  isDisplayAuthentication = false; // Variable d'affichage du modal d'authentification
  displayComponent = true; //  Variable d'affichage des components pour la déconnexion
  box:HTMLElement = null; // Menu "Se connecter" sur la gauche

  nbEssaisConnexion = constantes.nbEssaisConnexion; 
  phraseConnexion:String = ""; // Phrase affichée dans la zone d'erreur

  toSend = ""; // Faire passer une donnée statique entre plusieurs routes

    ngOnInit():void // A chaque instanciation de la page, a voir pour la définir dans un fichier de config pour faciliter le bousin
  {
    this.showStorage()
    
    this.isConnected = verification();
  }

  ngOnDestroy():void // A utiliser en tant que deconnexion
  {
    this.statut = "visiteur"; // Repasse le client/admin en simple visiteur 
    this.displayComponent = false;
    this.nbEssaisConnexion = constantes.nbEssaisConnexion; // Reset du nombre d'essai a la constante d'import
    this.resetStorage() // Vide toute les valeurs du localStorage
  }

  resetStorage()
  {
    localStorage.clear(); // Vide le localStorage
    console.log("localStorage cleared.");
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
                role = "client";
                break;
              case 1:
                role = "admin";
                break;
              default : "visiteur";
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
            localStorage.setItem("idUser", data.result[0].id); // TODO : récupérer l'id utilisateur et le passer dans le localStorage  

            this.showStorage();

            // Adapte l'UI en fonction du rôle de l'utilisateur
            this.statut = role;
            this.setMessage("", null);
            this.isDisplayAuthentication = !this.isDisplayAuthentication;
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

  getUsersData(paramsUser){
    this.accueilService.getUserById(paramsUser)
    .subscribe(res =>{
      console.log(res);
      //this.usersData = res;
    })
  } 

  gererNotifs(agreed :boolean){
    console.log('test');
    if(agreed == true){
      this.tinyAlert();
    }else{
      this.successNotification();
    }
  }
  tinyAlert(){
    Swal.fire("Bonjour !");
  }

  successNotification(){
    Swal.fire("test", "Notification", "Réussi avec succès");
  }

  alertConfirmation(){
    Swal.fire({
      title: 'Are you sure?',
      text: 'This process is irreversible.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, go ahead.',
      cancelButtonText: 'No, let me think'
    }).then((result) => {
      if (result.value) {
        Swal.fire(
          'Removed!',
          'Product removed successfully.',
          'success'
        )
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        Swal.fire(
          'Cancelled',
          'Product still in our database.)',
          'error'
        )
      }
    })
  } 
}


