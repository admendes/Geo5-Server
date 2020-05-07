import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import {NgForm} from '@angular/forms';
import { randomImages } from '../mapapp/mapapp.component'; //lixo

const httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }
  
  @Injectable({
    providedIn: 'root'
  })
export class RequestService {

  url = 'https://picsum.photos/v2/list?page=2&limit=1'; //lixo
   
  getTodos():Observable<randomImages[]> { //lixo
    return this.http.get<randomImages[]>(`${this.url}`);
    }
      
    
  isLoggedIn() {
      return !!this.getJwtToken();
  }
  
    todosUrl:string = 'https://apdc-geoproj.ey.r.appspot.com';
    todosLimit = '?_limit=5';
    forecast:string = '';


    constructor(private http:HttpClient) { }


    ping() {
      this.http.get("http://example.com/api/things").subscribe(
        data => console.log(data),
        err => console.log(err)
      );
    }

    userLogs='/rest/login/user';
    getlogs( me : any ):Observable<string[]>{
        return this.http.post<string[]>(`${this.todosUrl}${this.userLogs}`,me,httpOptions);
    }

    userlogin='/rest/login';
    userAuthentication(body):Observable<JSON>{  
      console.log(httpOptions);

      return this.http.post<JSON>(`${this.todosUrl}${this.userlogin}`,body,httpOptions);
    }

    userlogout='/rest/logout';
    logOutUser(token){
      return this.http.post<JSON>(`${this.todosUrl}${this.userlogout}`,token,httpOptions);
    }

    userReg='/rest/register';
    userRegist(body):Observable<JSON>{ 
      httpOptions.headers.append(
        'Authorization', `Bearer ${localStorage.getItem('token')}`
      ); 
      return this.http.post<JSON>(`${this.todosUrl}${this.userReg}`,body,httpOptions);
    }
    
    getJwtToken() {
      return localStorage.getItem('tokenID');
    }
}