import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpParams } from '@angular/common/http';

interface AuditDto {
  id: string;
  createdAt: string;
  username: string | null;
  resource: string;
  action: string;
  decision: string;
  reason: string | null;
  ip: string | null;
  userAgent: string | null;
}

@Component({
  selector: 'app-admin-audit',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="p-6 space-y-4">
      <div class="flex items-center justify-between">
        <h2 class="text-lg font-semibold">Audit (ADMIN/AUDITOR)</h2>
        <button class="text-sm text-blue-700" (click)="load()">Reload</button>
      </div>

      <form class="grid grid-cols-1 md:grid-cols-5 gap-2 text-sm items-end" (ngSubmit)="load()">
        <label class="block">
          <span class="font-medium">User</span>
          <input class="border rounded px-2 py-1 w-full" [(ngModel)]="filters.user" name="user" />
        </label>
        <label class="block">
          <span class="font-medium">Resource</span>
          <input class="border rounded px-2 py-1 w-full" [(ngModel)]="filters.resource" name="resource" />
        </label>
        <label class="block">
          <span class="font-medium">Action</span>
          <input class="border rounded px-2 py-1 w-full" [(ngModel)]="filters.action" name="action" />
        </label>
        <label class="block">
          <span class="font-medium">Decision</span>
          <select class="border rounded px-2 py-1 w-full" [(ngModel)]="filters.decision" name="decision">
            <option value="">All</option>
            <option value="allow">allow</option>
            <option value="deny">deny</option>
          </select>
        </label>
        <button class="bg-slate-900 text-white px-3 py-2 rounded text-sm" type="submit">Search</button>
      </form>

      <div class="border rounded">
        <table class="w-full text-sm">
          <thead class="bg-slate-100">
            <tr>
              <th class="border px-2 py-1 text-left">Time</th>
              <th class="border px-2 py-1 text-left">User</th>
              <th class="border px-2 py-1 text-left">Resource</th>
              <th class="border px-2 py-1 text-left">Action</th>
              <th class="border px-2 py-1 text-left">Decision</th>
              <th class="border px-2 py-1 text-left">IP</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let e of events" class="border-t">
              <td class="border px-2 py-1">{{ e.createdAt | date: 'yyyy-MM-dd HH:mm:ss' }}</td>
              <td class="border px-2 py-1">{{ e.username || '-' }}</td>
              <td class="border px-2 py-1">{{ e.resource }}</td>
              <td class="border px-2 py-1">{{ e.action }}</td>
              <td class="border px-2 py-1">{{ e.decision }}</td>
              <td class="border px-2 py-1">{{ e.ip || '-' }}</td>
            </tr>
          </tbody>
        </table>
        <div *ngIf="events.length === 0" class="p-3 text-sm text-slate-500">No audit events</div>
      </div>
    </div>
  `,
})
export class AdminAuditComponent implements OnInit {
  events: AuditDto[] = [];
  filters = {
    user: '',
    resource: '',
    action: '',
    decision: '',
  };

  constructor(private readonly http: HttpClient) {}

  ngOnInit(): void {
    this.load();
  }

  load() {
    let params = new HttpParams();
    Object.entries(this.filters).forEach(([key, value]) => {
      if (value) {
        params = params.set(key, value);
      }
    });

    this.http.get<AuditDto[]>('/api/audit', { params }).subscribe((data) => {
      this.events = data;
    });
  }
}
