import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-authentication',
  templateUrl: './authentication.component.html',
  styleUrls: ['./authentication.component.scss']
})

export class AuthenticationComponent implements OnInit {

  public value;
  public sub;

  constructor( private activatedRoute:ActivatedRoute, private router:Router) 
  {
    console.log(this.router.getCurrentNavigation().extras.state)
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(returnedData => {
        this.value = Object.values(returnedData)[0];   
    });
  }

}
