import { Component, OnInit } from '@angular/core';
import { verification } from '../../../../config/verification';

@Component({
  selector: 'app-daily-order',
  templateUrl: './daily-order.component.html',
  styleUrls: ['./daily-order.component.scss']
})
export class DailyOrderComponent implements OnInit {

  constructor() { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  visibility:boolean = false;

  ngOnInit(): void 
  {
    this.visibility = verification(); 
  }
}
