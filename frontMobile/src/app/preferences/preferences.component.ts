import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { PreferencesService } from '../services/preferences/preferences.service';
import { ManageUserService } from '../services/manage-user/manage-user.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-preferences',
  templateUrl: './preferences.component.html',
  styleUrls: ['./preferences.component.scss']
})
export class PreferencesComponent implements OnInit {

  constructor(
    private preferencesService:PreferencesService,
    private manageUserService:ManageUserService,
    private modalService: NgbModal,
    private cookieService: CookieService
    ) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  public isConnected:boolean = false;

  userId: any;
  statut:any;

  // Variable+ de modification des préférences 
  public listPreferences; 

  public canSee:boolean;
  public state;

  public phraseConnexion; // Phrase affichée dans la zone d'erreur
  public phraseNotifications:string = ""; 
  
  // Variable de modification des paramètres
  public listParameters; 
  public notifsCheck:boolean;

  public usersData;
  public imageModifiee;
  public url:any ;
  public imgPath:any = null;

  public closeResult = '';

  ngOnInit(): void 
  {
    this.isConnected = verification();
    this.state = localStorage.getItem("role");
    
    if (this.isConnected == true && this.state == "admin")
    {
      this.canSee = true;
    }
    else if (this.isConnected == true && this.state == "client")
    {
      this.canSee = true;
    }
    else
    {
      this.isConnected = false;
    }

    let toCheck = localStorage.getItem('allowNotifications');

    if(toCheck == "true")
    {
      this.notifsCheck = true
      this.phraseNotifications = "Désactiver les notifications";
    }
    else
    {
      this.notifsCheck = false;
      this.phraseNotifications = "Activer les notifications";
    }

    this.userId = localStorage.getItem("userId");
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }

  getUsersData(userId){
    this.usersData = this.manageUserService.getUserById(userId)
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

  notifications()
  {
    let toCheck = localStorage.getItem('allowNotifications');
    
    if(toCheck == "true")
    {
      this.notifsCheck = false;
      localStorage.setItem('allowNotifications', 'false');
      this.phraseNotifications = "Désactiver les notifications";
    }
    else
    {
      this.notifsCheck = true;
      this.phraseNotifications = "Activer les notifications";
      localStorage.setItem('allowNotifications', 'true');
      this.cookieService.set("notificationsProjetCantiniere", "true")
    }
  }
 /* ngOnDestroy():void
  {
    this.isConnected = false;
  }*/
  changePassword(champUn, champDeux)
  {
    if(champUn && champDeux)
    {
      if(champUn == champDeux)
      {
        this.preferencesService.changePassword(champUn, this.userId);
      }
      else
      {
        let errorZone:HTMLElement = document.getElementById("errorZone");
        errorZone.innerHTML = "test"
        errorZone.style.display = "Les mots de passe ne correspondent pas.";
      }
    }
    else
    {

    }
  }


}
