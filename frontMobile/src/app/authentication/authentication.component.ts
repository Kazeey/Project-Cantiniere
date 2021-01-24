import { Component, OnInit } from '@angular/core';

import { methods as menus } from '../../../../config/menus';
import { verification } from '../../../../config/verification';
import { constantes } from '../../../../config/constantes';
import { AuthenticationService } from '../services/authentication/authentication.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.scss'],
})
export class AuthenticationComponent implements OnInit {

  // Valeurs possibles pour statut = "client/admin/visiteur", change l'affichage en fonction
  statut:String = "visiteur";

  // Si true, redirige vers l'accès utilisateur
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  // Assignation des menus après vérification de l'utilisateur
  communs = menus.menusCommuns

  isDisplayAuthentication = false; // Variable d'affichage pour l'authentification
  displayComponent = true; // Variable d'affichage des components pour la déconnexion
  box:HTMLElement = null; // Affichage de la connexion sur la menu

  nbEssaisConnexion = constantes.nbEssaisConnexion;
  phraseConnexion:String = ""; // Affiché dans la zone d'erreur

  toSend = ""; // Si besoin de faire passer une donnée statique entre plusieurs routes

  constructor(private AuthenticationService:AuthenticationService,
              private router:Router) { }

  ngOnInit() 
  {
   /* this.showStorage();
    this.isConnected = verification();*/
  }

  endSession():void
  {
    this.statut = "visiteur";
    this.displayComponent = false;
    this.nbEssaisConnexion = constantes.nbEssaisConnexion;
    this.resetStorage();
  }

  resetStorage()
  {
    localStorage.clear();
    console.log("localStorage cleared");
  }

  showStorage()
  {
    console.log({...localStorage});
  }

  // TODO : Matthias
  // A voir si on remet la fonction toggleDisplayAuthentication ou non pour la version mobile

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
    this.setMessage("", null); // on reset la zone de message au cas ou

    if(mail)
    {
      const indexOfAt = mail.indexOf('@');
      const indexOfPoint = mail.indexOf('.', indexOfAt);

      if(indexOfAt > 0 && indexOfPoint > 0)
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
      else
      {
        this.setMessage("Le format de l'adresse mail inscrite est incorrect ! Veuillez la modifier.", null);
      }
    }
    else
    {
      this.setMessage("Veuillez renseigner une adresse mail.", null);
    }
  }

  checkConnection(mail, password)
  {
    let role:String = "admin";
    this.AuthenticationService.login(mail, password)
    .subscribe(res => {
      this.AuthenticationService.checkEmail(mail)
      .subscribe(resMail => {
        if(resMail == "true")
        {
          let data = JSON.parse(res);

          if(data.userError)
          {
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

            switch(data.role)
            {
              case 0 :
                role = "client";
                break;
              case 1:
                role = "admin";
                break;
              default : "visiteur";
            }

            switch (constantes.timeConnexion)
            {
              case 0:
                time = 900000;
                break;
              case 1:
                time = ((900000 * 4) * 24);
                break;
            }

            let timeDestruction = String(Date.now() + time);
            localStorage.setItem("timeDestruction", timeDestruction);
            localStorage.setItem("connected", "true");
            localStorage.setItem("idUser", data.result[0].id);
            localStorage.setItem("role", data.role);

            this.showStorage();

            this.statut = role;
            this.setMessage("", null);
            this.isDisplayAuthentication = ! this.isDisplayAuthentication;
            this.router.navigate(['dailyMenu']);
      
          }
        }
        else
        {
          this.setMessage("Adresse maill incorrecte ou bloquée.", null);
        }
      });


    });
    
  }

  forgotPassword(mail)
  {
    this.AuthenticationService.forgotPassword(mail)
    .subscribe(res => {});

    this.setMessage("Si l'adresse mail existe, un mail a été envoyé (Pensez à vérifier vos spams).", null);
  }
}
