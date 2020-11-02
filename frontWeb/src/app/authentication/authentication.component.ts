import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';


@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.scss']
})

export class AuthenticationComponent implements OnInit {

  public sub : Subscription;
  public staticValue : any;
  public value;
  public inputValue;

  constructor( private activatedRoute:ActivatedRoute, private router:Router) 
  {
    
  }

  ngOnInit(): void // à l'initialisation 
  {
    // Pour récupérer un paramètre statique
    // Récupère d'abord un objet, puis extraction de la data avant insertion dans une variable
    this.sub = this.activatedRoute.data.subscribe(returnedData => {
        this.staticValue = Object.values(returnedData)[0];   
    });

    // Pour récupérer un paramètre dynamique
    this.value = this.activatedRoute.snapshot.queryParamMap.get('toSend');
  }

  ngOnDestroy(): void // à la destruction
  {
    if(this.sub)
    {
      this.sub.unsubscribe();
      this.inputValue = null;
    }
  }

  checkForConnection(mail, password)
  {
    // Récupère les valeurs du formulaire de connexion 
    this.inputValue = [
        ["mail", mail],
        ["password", password]
    ];

    for(let i = 0; i < this.inputValue.length; i++)
    {
      localStorage.setItem(this.inputValue[i][0], this.inputValue[i][1]);
    }

  }

}
