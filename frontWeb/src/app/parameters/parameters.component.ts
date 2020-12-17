import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { ParametersService } from '../services/parameters/parameters.service';
import { constantes } from '../../../../config/constantes';
import { NewsletterService } from '../services/Newsletter/newsletter.service';
import { SwPush } from '@angular/service-worker';

@Component({
  selector: 'app-parameters',
  templateUrl: './parameters.component.html',
  styleUrls: ['./parameters.component.scss']
})
export class ParametersComponent implements OnInit {

  readonly VAPID_PUBLIC_KEY = "BHa_MshqCGU69xD-g0mImqklDDZorFY11VC6ysjnbHsFtx68pN_zOtYDRVHs6-Fpyayiad8Y2Crj6KCaWuQlYHc";

  constructor(private swPush: SwPush,
              private parametersService:ParametersService,
              private newsletterService: NewsletterService) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  // Variable de modification des paramètres
  public listParameters; 

  ngOnInit(): void 
  {
    this.isConnected = verification();
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }

  subscribeToNotifications() {

    this.swPush.requestSubscription({
        serverPublicKey: this.VAPID_PUBLIC_KEY
    })
    .then(sub => this.newsletterService.addPushSubscriber(sub).subscribe())
    .catch(err => console.error("Erreur lors de l'activation des notifications !", err));
  }
}
