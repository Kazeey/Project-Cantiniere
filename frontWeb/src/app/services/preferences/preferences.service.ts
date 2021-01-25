import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class PreferencesService {

  constructor(private http:HttpClient) { }

  private url = constantes.urlAPINode; // url souhaitée pour la requête de l'API

  changePassword(newPassword, userId)
  {
    this.http.post(this.url + "account/changePassword", {newPassword : newPassword, userId : userId}).subscribe(res => {});
  }
}
