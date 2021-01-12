import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { verification } from '../../../../config/verification';
import { ParametersService } from '../services/parameters/parameters.service';
import { constantes } from '../../../../config/constantes';
import { stringify } from '@angular/compiler/src/util';
import { AppComponent } from '../app.component';


@Component({
  selector: 'app-parameters',
  templateUrl: './parameters.component.html',
  styleUrls: ['./parameters.component.scss']
})
export class ParametersComponent implements OnInit {

  @Output() notifs = new EventEmitter<Boolean>();
  checked = false;

  constructor() { }

 
  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;
  url:any;
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

  onSelectFile(event) {
    if (event.target.files && event.target.files[0]) {
      var reader = new FileReader();

      reader.readAsDataURL(event.target.files[0]); // read file as data url

      reader.onload = (event) => { // called once readAsDataURL is completed
        this.url = event.target.result;
      }
    }
  }

  subscribeToNotifications(agreed: boolean){
    this.notifs.emit(agreed);
    this.checked = true;
    console.log('Notifications en place');
  }

  unsubscribeToNotifications(agreed: boolean){
    this.notifs.emit(agreed);
    this.checked = false;
    console.log('Notifications annulées');
  }
}
