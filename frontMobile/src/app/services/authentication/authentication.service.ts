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

  login(mail, password)
  {
    return this.http.post(this.url, {email : mail, password : password}, {responseType:"text"}); // Récupère les données renvoyées par l'API
  }

  checkEmail(mail)
  {
    return this.http.get(this.url + "/checkEmail?email=" + mail, {responseType:"text"});
  }

  forgotPassword(mail): Observable<Object>
  {
    return this.http.get(this.url + "/forgotpassword?email="+ mail);
  }

  blockAccount(mail): Observable<Object>
  {
    return this.http.get(this.url + "/blockaccount?email=" + mail);
  }
  
  activeAccount(mail): Observable<Object>
  {
    return this.http.get(this.url + "/activeaccount?email=" + mail);
  }
}
