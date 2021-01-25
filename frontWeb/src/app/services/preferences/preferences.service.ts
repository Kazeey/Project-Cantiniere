import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { constantes } from '../../../../../config/constantes';
import { ManageUserService } from '../manage-user/manage-user.service';

@Injectable({
  providedIn: 'root'
})
export class PreferencesService {

  constructor(
    private http:HttpClient,
    private manageUserService:ManageUserService
  ) { }

  private url = constantes.urlAPINode; // url souhaitée pour la requête de l'API

  changePassword(newPassword, userId)
  {
    this.http.post(this.url + "account/changePassword", {newPassword : newPassword, userId : userId}).subscribe(res => {});
  }

  updateImg(userId, url, imgPath)
  {
    this.http.post(this.url + "users/updateUserImg", {userId:userId, url:url, imgPath:imgPath})
    .subscribe(res =>{});
    
    return this.manageUserService.getUserById(userId);
  }
}
