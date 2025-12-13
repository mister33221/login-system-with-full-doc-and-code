import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AdminRolesComponent } from './admin-roles.component';

describe('AdminRolesComponent', () => {
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, AdminRolesComponent],
    });
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should load permissions and roles', () => {
    const fixture = TestBed.createComponent(AdminRolesComponent);
    fixture.detectChanges();

    const permsReq = httpMock.expectOne('/api/permissions');
    permsReq.flush([{ id: 'p1', code: 'role:view', resource: 'role', action: 'view', description: '' }]);

    const rolesReq = httpMock.expectOne('/api/roles');
    rolesReq.flush([
      { id: 'r1', code: 'ADMIN', name: 'Admin', description: '', permissions: ['role:view'] },
    ]);

    fixture.detectChanges();

    const component = fixture.componentInstance;
    expect(component.roles.length).toBe(1);
    expect(component.permissions.length).toBe(1);
  });
});
