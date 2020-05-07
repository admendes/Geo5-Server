import { Component, OnInit } from '@angular/core';
import { RequestService } from '../../services/RequestService';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.css']
})
export class SignInComponent implements OnInit {
  isLoginError : boolean = false;

  constructor(private request:RequestService,private router : Router) { }

  ngOnInit(): void {
   // this.request.ping();
  }
  OnSubmit(userName,Password){
    const body={
      username:userName,
      password:Password
    }
    this.request.userAuthentication(body).subscribe((data : any)=>{
     console.log(data);
     localStorage.setItem('password',Password);
     localStorage.setItem('username',data.username);
     localStorage.setItem('tokenID',data);
     this.router.navigate(['/person']);
   },
   (err : HttpErrorResponse)=>{
     this.isLoginError = true;
   });
   console.log();
 }
}
