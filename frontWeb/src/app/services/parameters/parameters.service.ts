import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ManageUserService } from '../manage-user/manage-user.service';


import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class ParametersService {

  constructor(private http:HttpClient,
              private manageUserService:ManageUserService) { }

  private urlUser = constantes.urlAPINode + "users/"; // url souhaitée pour la requête de l'API

  updateImg(userId, url, imgPath){
        this.http.post(this.urlUser + "updateUserImg", {userId:userId, url:url, imgPath:imgPath}).subscribe(res =>{}); // Récupère les données renvoyées par l'API
        return this.manageUserService.getUserById(userId);
      }
}
