import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface PermissionDto {
  id: string;
  code: string;
  resource: string;
  action: string;
  description: string;
}

interface RoleDto {
  id: string;
  code: string;
  name: string;
  description: string;
  permissions: string[];
}

@Component({
  selector: 'app-admin-roles',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="p-6 space-y-4">
      <div class="flex items-center justify-between">
        <h2 class="text-lg font-semibold">Roles & Permissions</h2>
        <button class="text-sm text-blue-700" (click)="startCreate()">+ New Role</button>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="md:col-span-1 border rounded p-3">
          <div class="font-medium mb-2">Role list</div>
          <ul class="space-y-1">
            <li
              *ngFor="let r of roles"
              (click)="selectRole(r)"
              class="px-2 py-1 border rounded cursor-pointer"
              [class.bg-slate-100]="r.id === selectedRoleId"
            >
              <div class="font-semibold">{{ r.code }}</div>
              <div class="text-xs text-slate-500">{{ r.name }}</div>
            </li>
          </ul>
        </div>

        <div class="md:col-span-2 border rounded p-3">
          <div class="flex items-center justify-between mb-2">
            <div class="font-medium">Role form</div>
            <div class="text-xs text-slate-500" *ngIf="selectedRoleId">Editing {{ form.value.code }}</div>
          </div>

          <form [formGroup]="form" class="space-y-3" (ngSubmit)="save()">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-3">
              <label class="block text-sm">
                <span class="font-medium">Code</span>
                <input class="w-full border rounded px-2 py-1" formControlName="code" [readonly]="!isCreating" />
              </label>
              <label class="block text-sm">
                <span class="font-medium">Name</span>
                <input class="w-full border rounded px-2 py-1" formControlName="name" />
              </label>
            </div>
            <label class="block text-sm">
              <span class="font-medium">Description</span>
              <input class="w-full border rounded px-2 py-1" formControlName="description" />
            </label>

            <div>
              <div class="font-medium text-sm mb-2">Permissions</div>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-2 max-h-48 overflow-auto border rounded p-2">
                <label
                  class="flex items-start space-x-2 text-sm"
                  *ngFor="let p of permissions"
                  [title]="p.description"
                >
                  <input
                    type="checkbox"
                    [checked]="(form.value.permissionIds || []).includes(p.id)"
                    (change)="togglePermission(p.id, $event)"
                  />
                  <span>
                    <span class="font-medium">{{ p.code }}</span>
                    <span class="block text-slate-500 text-xs">{{ p.resource }}:{{ p.action }}</span>
                  </span>
                </label>
              </div>
            </div>

            <div class="flex items-center gap-3">
              <button type="submit" class="bg-slate-900 text-white px-3 py-1 rounded text-sm">
                {{ isCreating ? 'Create' : 'Save' }}
              </button>
              <button
                type="button"
                *ngIf="selectedRoleId"
                class="text-sm text-red-600"
                (click)="deleteRole()"
              >
                Delete
              </button>
              <span class="text-sm text-green-700" *ngIf="message">{{ message }}</span>
            </div>
          </form>
        </div>
      </div>
    </div>
  `,
})
export class AdminRolesComponent implements OnInit {
  roles: RoleDto[] = [];
  permissions: PermissionDto[] = [];
  selectedRoleId: string | null = null;
  message = '';
  isCreating = false;

  form: FormGroup;

  constructor(private readonly http: HttpClient, private readonly fb: FormBuilder) {
    this.form = this.fb.group({
      code: [''],
      name: [''],
      description: [''],
      permissionIds: [[] as string[]],
    });
  }

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
    this.http.get<PermissionDto[]>('/api/permissions').subscribe((perms) => (this.permissions = perms));
    this.http.get<RoleDto[]>('/api/roles').subscribe((rs) => (this.roles = rs));
  }

  selectRole(role: RoleDto) {
    this.isCreating = false;
    this.selectedRoleId = role.id;
    this.form.patchValue({
      code: role.code,
      name: role.name,
      description: role.description,
      permissionIds: this.permissions
        .filter((p) => role.permissions.includes(p.code))
        .map((p) => p.id),
    });
  }

  startCreate() {
    this.isCreating = true;
    this.selectedRoleId = null;
    this.form.reset({
      code: '',
      name: '',
      description: '',
      permissionIds: [],
    });
  }

  togglePermission(id: string, event: Event) {
    const input = event.target as HTMLInputElement | null;
    if (!input) return;
    const current: string[] = this.form.value.permissionIds ?? [];
    const updated = input.checked ? [...current, id] : current.filter((p) => p !== id);
    this.form.patchValue({ permissionIds: updated });
  }

  save() {
    const payload = this.form.value;
    if (this.isCreating) {
      this.http.post<RoleDto>('/api/roles', payload).subscribe((created) => {
        this.message = 'Created';
        this.roles = [...this.roles, created];
        this.selectRole(created);
      });
    } else if (this.selectedRoleId) {
      this.http.put<RoleDto>(`/api/roles/${this.selectedRoleId}`, payload).subscribe((updated) => {
        this.message = 'Saved';
        this.roles = this.roles.map((r) => (r.id === updated.id ? updated : r));
        this.selectRole(updated);
      });
    }
  }

  deleteRole() {
    if (!this.selectedRoleId) return;
    this.http.delete<void>(`/api/roles/${this.selectedRoleId}`).subscribe(() => {
      this.roles = this.roles.filter((r) => r.id !== this.selectedRoleId);
      this.startCreate();
      this.message = 'Deleted';
    });
  }
}
