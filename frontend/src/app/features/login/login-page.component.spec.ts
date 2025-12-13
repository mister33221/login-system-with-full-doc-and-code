import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { LoginPageComponent } from './login-page.component';
import { Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AuthService } from '../../core/auth/auth.service';
import { of, throwError } from 'rxjs';

describe('LoginPageComponent', () => {
  let component: LoginPageComponent;
  let router: Router;
  let auth: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    auth = jasmine.createSpyObj<AuthService>('AuthService', ['login', 'logout', 'getAccessToken']);

    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule, LoginPageComponent],
      providers: [{ provide: AuthService, useValue: auth }],
    });
    const fixture = TestBed.createComponent(LoginPageComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    localStorage.clear();
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('should navigate on successful login', () => {
    auth.login.and.returnValue(of(void 0));
    component.username = 'john';
    component.password = 'pw';
    const navigateSpy = spyOn(router, 'navigate');

    component.login();

    expect(navigateSpy).toHaveBeenCalledWith(['/']);
  });

  it('should show error when missing credentials', () => {
    component.username = '';
    component.password = '';
    component.login();
    expect(component.error).toBe('請輸入帳號與密碼');
  });

  it('should show error on 401', () => {
    auth.login.and.returnValue(throwError(() => ({ status: 401 })));
    component.username = 'john';
    component.password = 'wrong';
    component.login();
    expect(component.error).toBe('帳號或密碼錯誤');
  });
});
