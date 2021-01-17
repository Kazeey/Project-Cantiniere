import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class ManageUserService {

  constructor(private http:HttpClient) { }

  private url = constantes.urlAPINode + "users/"; // url souhaitée pour la requête de l'API

  getAllUsers()
  {
    return this.http.get(this.url + "getAllUsers"); // Récupère les données renvoyées par l'API
  }

  getUserById(userId)
  {
    return this.http.post(this.url + "getUserById", {userId:userId}); // Récupère les données renvoyées par l'API
  }

  getUserBySearchField(userName)
  {
    return this.http.post(this.url + "getUserBySearchField", {userName:userName}); // Récupère les données renvoyées par l'API
  }
}
