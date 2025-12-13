import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AdminAuditComponent } from './admin-audit.component';

describe('AdminAuditComponent', () => {
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, AdminAuditComponent],
    });
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should load audit events', () => {
    const fixture = TestBed.createComponent(AdminAuditComponent);
    fixture.detectChanges();

    const req = httpMock.expectOne((r) => r.url === '/api/audit');
    req.flush([
      {
        id: '1',
        createdAt: new Date().toISOString(),
        username: 'admin',
        resource: 'auth',
        action: 'login',
        decision: 'allow',
        reason: null,
        ip: '1.1.1.1',
        userAgent: 'test',
      },
    ]);

    fixture.detectChanges();
    const component = fixture.componentInstance;
    expect(component.events.length).toBe(1);
  });
});
