import { Component, OnInit } from '@angular/core';

import { verification } from '../../../../config/verification';
import { ManageMenuService } from '../services/manage-menu/manage-menu.service';

@Component({
  selector: 'app-manage-menu',
  templateUrl: './manage-menu.component.html',
  styleUrls: ['./manage-menu.component.scss']
})
export class ManageMenuComponent implements OnInit {

  constructor(private manageMenuService: ManageMenuService) { }

  // Si true, affiche le contenu du component 
  // Pour éviter tout problème d'affichage avec la connexion
  isConnected:boolean = false;
  public canSee:boolean;

  // Variable de modification des menus
  menus: any = [];
  menu: any;

  // Use with Modal
  isVisible = false;
  editMode = false;
  modalTitleStr: string = "Créer un menu";

  // Menu field
  label: string;
  description: string;
  priceDF: number;
  availableWeek: any = [];
  week: string;

  // Search
  tempMenus: any;

  ngOnInit(): void 
  {
    this.isConnected = verification();
    let state = localStorage.getItem("role");
    
    if (this.isConnected == true && state == "admin")
    {
      this.canSee = true;
    }
    else if (this.isConnected == true && state == "client")
    {
      this.canSee = false
    }
    else
    {
      this.isConnected = false;
    }
    
    this.getMenus();
  }

  ngOnDestroy(): void
  {
    this.isConnected = false; 
  }

  updateViews() {
    this.menus = [];

    this.getMenus();
  }

  /* Manage Menu */

  getMenus() {
    this.manageMenuService.getAllMenus()
      .subscribe(res => {
        this.menus = res;

        this.tempMenus = this.menus;
      })
  }

  onAddMenu() {
    this.editMode = false;

    this.showMenuModal("Créer un menu");
  }

  onEditMenu(menu: any) {
    this.label = menu.label;
    this.priceDF = menu.priceDF;
    this.availableWeek = menu.availableForWeeks;

    this.editMode = true;

    this.menu = menu;

    this.showMenuModal("Modifier un menu");
  }

  onDeleteMenu(menu: any) {
    this.manageMenuService.deleteMenu(menu.id)
      .subscribe(res => {
        this.updateViews();
      });
  }

  /* Show & Manage Modal For Menu */

  showMenuModal(title: string): void {
    this.modalTitleStr = title;

    this.isVisible = true;
  }

  handleMenuOk(): void {
    if (this.editMode) {
      this.menu.label = this.label;
      this.menu.description = this.description;
      this.menu.priceDF = this.priceDF;
      this.menu.availableForWeeks = this.availableWeek;

      this.manageMenuService.updateMenu(this.menu.id, this.menu)
      .subscribe(res => {
        this.updateViews()
      })
    } 
    else {
      this.menu = {
        label: this.label,
        description: this.description,
        priceDF: this.priceDF,
        status: 0,
        imageId: 75,
        availableForWeeks: this.availableWeek
      }

      this.manageMenuService.addMenu(this.menu)
      .subscribe(res => {
        this.updateViews()
      })
    }
    this.isVisible = false;

    this.resetField();
  }

  handleMenuCancel(): void {
    this.isVisible = false;

    this.resetField();
  }

  onAddAvailableWeek() {
    this.availableWeek.push(this.week);

    this.week = "";
  }

  getAvailableWeek() {
    let str = "";

    for (let week of this.availableWeek) {
      str = str + week + " ";
    }

    return str;
  }

  resetField() {
    this.menu = null;
    this.label = null;
    this.priceDF = null;
    this.availableWeek = [];
  }

  searchInMenus(value) {
    this.menus = []

    if (value.length > 0) {
      for (let i = 0; i < this.tempMenus.length; i++) {
        if (this.tempMenus[i].label.toLowerCase().includes(value.toLowerCase())) {
          this.menus.push(this.tempMenus[i])
        }
      }
    } else {
      this.menus = this.tempMenus
    }
  }

}
