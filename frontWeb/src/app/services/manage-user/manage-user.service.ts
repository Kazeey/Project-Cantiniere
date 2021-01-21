import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { constantes } from '../../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class ManageUserService {

  constructor(private http:HttpClient) { }

  private urlUserJava = constantes.urlAPIJAVA + "/user/update/"; // url souhaitée pour la requête de l'API
  private urlUser = constantes.urlAPINode + "users/"; // url souhaitée pour la requête de l'API
  private urlAccount = constantes.urlAPINode + "account/"; // url souhaitée pour la requête de l'API

  getAllUsers()
  {
    return this.http.get(this.urlUser + "getAllUsers"); // Récupère les données renvoyées par l'API
  }

  getUserById(userId)
  {
    return this.http.post(this.urlUser + "getUserById", {userId:userId}); // Récupère les données renvoyées par l'API
  }

  getUserBySearchField(userName)
  {
    return this.http.post(this.urlUser + "getUserBySearchField", {userName:userName}); // Récupère les données renvoyées par l'API
  }

  saveUser(id, name, firstname, sex, mail, phone, role, address, town, postalCode, wallet, status)
  {
    this.http.post(this.urlAccount + "update", {
      id : id, 
      name : name, 
      firstname : firstname, 
      sex : sex, 
      email : mail, 
      phone : phone, 
      role : role, 
      address : address, 
      town : town, 
      postalCode : postalCode, 
      wallet : wallet, 
      status : status
    }).subscribe(res => {}); // Modifie un utilisateur

    return this.getAllUsers();
  }
}
