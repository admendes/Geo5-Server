import { Component, OnInit ,Input, EventEmitter, Output} from '@angular/core';

import { Direction } from '../../../models/Direction';

@Component({
  selector: 'app-direction',
  templateUrl: './direction.component.html',
  styleUrls: ['./direction.component.css']
})
export class DirectionComponent implements OnInit {
  @Input() direction : Direction
  constructor() { }

  ngOnInit(): void {
  }
  setClasses(){
    let classes = {
        direction: true,
        'visible': this.direction.visible
    }
    return classes
  }

  showHide(){
    this.direction.visible = !this.direction.visible;
  }

}
