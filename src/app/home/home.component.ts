import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import * as $ from 'jquery';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  l: string;
  constructor( public translate: TranslateService) {
    translate.addLangs(['pt','en']);
   }

  ngOnInit(): void {

    this.l = localStorage.getItem('language');
    if(this.l===null) this.l='pt';

    this.translate.setDefaultLang(this.l);

    $(document).ready(function() {
      $('select').val( localStorage.getItem('language') );
      
    });

  }

  switchLang(lang: string) {
    localStorage.setItem('language',lang);
    this.translate.use(lang);
    this.translate.setDefaultLang(lang);
  }
}
