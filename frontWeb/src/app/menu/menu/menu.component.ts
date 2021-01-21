import { Component, OnInit, EventEmitter, Output, Input } from '@angular/core';

@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss']
})
export class MenuComponent implements OnInit {
  @Input() menu: any;
  @Input() isAuthenticate: boolean;

  @Output() onQuantityChange = new EventEmitter<any>();

  products: any = [];
  
  quantity: number = 0;

  constructor() { }

  ngOnInit(): void { }

  onMeal(event: any) {
    this.onQuantityChange.emit(event);
  }
}
