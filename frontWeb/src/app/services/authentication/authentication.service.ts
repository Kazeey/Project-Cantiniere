import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Menu } from '../../interfaces/menu'

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(private http:HttpClient) {}

  private url = constantes.urlAPINode + "login"; // url souhaitée pour la requête de l'API

  login(mail, password): Observable<Object>
  {
    return this.http.get(this.url + "?email=" + mail + "&password=" + password); // Récupère les données renvoyées par l'API
  }

  // TODO : A voir suite au retour de l'information
  forgotPassword(mail): Observable<Object>
  {
    return this.http.get(this.url + "/forgotpassword?email="+ mail);
  }
}
