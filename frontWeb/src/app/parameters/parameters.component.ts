import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

import { verification } from '../../../../config/verification';
import { ParametersService } from '../services/parameters/parameters.service';
import { ManageUserService } from '../services/manage-user/manage-user.service';
import { constantes } from '../../../../config/constantes';
import { stringify } from '@angular/compiler/src/util';
import { AppComponent } from '../app.component';
import { trimTrailingNulls } from '@angular/compiler/src/render3/view/util';
import { Subscriber } from 'rxjs';


@Component({
  selector: 'app-parameters',
  templateUrl: './parameters.component.html',
  styleUrls: ['./parameters.component.scss']
})
export class ParametersComponent implements OnInit {

  userId: any;
  statut:any;
  public usersData;
  public imageModifiee;
  url:any ;
  imgPath:any = null;


  constructor(private manageUserService:ManageUserService,
              private parametersService:ParametersService) { }

 
  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;

  // Variable de modification des paramètres
  public listParameters; 
  public notifsCheck:boolean;

  ngOnInit(): void 
  {
    this.statut = localStorage.getItem("statut");
    this.userId = localStorage.getItem("userId");
    this.isConnected = verification();
    this.getUsersData(this.userId);
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }

  getUsersData(userId){
    this.usersData = this.manageUserService.getUserById(userId)
  }

  onSelectFile(event) {

    if (event.target.files && event.target.files[0]) {
      var reader = new FileReader();

      reader.readAsDataURL(event.target.files[0]); // read file as data url

      reader.onload = (event) => { // called once readAsDataURL is completed
        this.url = event.target.result;
      }
    }
    this.manageUserService.getUserById(this.userId)
    .subscribe(res => {
      let userId = res[0].id;
      this.updateImg(userId, this.url, this.imgPath)
      .subscribe(res =>{
       return this.getUsersData(userId);
      });
    });
  }

  updateImg(userId, url, imgPath){
    return this.parametersService.updateImg(userId, url, imgPath);
  }

  notifications(event){
    if(event.srcElement.checked == true){
      this.notifsCheck = true;
            console.log(this.notifsCheck);

    }else{
      this.notifsCheck = false;
      console.log(this.notifsCheck);
    }
  }
}
