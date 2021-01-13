import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Users }from '../interfaces/users'

import { constantes } from '../../../../config/constantes';

@Injectable({
  providedIn: 'root'
})
export class AccueilService {

  constructor(private http:HttpClient) { }

  private url = constantes.urlAPINode + "users/"; // url souhaitée pour la requête de l'API

  getUserById(userId): Observable<Object>
  {
    return this.http.get(this.url + "getUserById/?userId=" + userId); // Récupère les données renvoyées par l'API
  }

  
}