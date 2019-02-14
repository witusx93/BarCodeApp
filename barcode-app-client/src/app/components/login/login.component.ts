import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {LoginModel} from 'src/app/models/LoginModel';
import {AuthGuardService} from 'src/app/services/auth-guard/auth-guard.service';
import {AuthService} from 'src/app/services/auth/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {
  public loginModel: LoginModel;
  private resp: any;

  public isWrong: boolean;

  constructor(public route: ActivatedRoute,
    private authService: AuthService,
    private router: Router,
    private guard: AuthGuardService) { }

  ngOnInit() {
    if(localStorage.currentUser)
      this.router.navigate(['/main']);

    this.loginModel = new LoginModel;
    this.isWrong = false;
  }

  public login() {
    this.getAuthResponse()
        .then(() => {
        if(this.resp.status === 200) {
          this.guard.setUser(this.loginModel.username, this.resp.headers.get('Authorization'));
          this.router.navigate(['/admin/admin-view'])
        } else {
          this.guard.logout();
          this.isWrong = true;
        }
      });
  }

  private getAuthResponse() {
    return this.authService.login(this.encodeCreds())
    .toPromise()
    .then(
      (response) =>
        this.resp = response
      ).catch(err => console.log('Login attempt failed.'));
  }

  public resetFlag() {
    this.isWrong = !this.isWrong;
  }

  private encodeCreds(): LoginModel {
    let creds: LoginModel = {
      username: this.loginModel.username,
      password: this.loginModel.password
    }
    return creds;
  }

}