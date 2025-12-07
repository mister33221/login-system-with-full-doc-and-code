import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-roles',
  standalone: true,
  template: `
    <div class="p-6">
      <h2 class="text-lg font-semibold mb-3">Roles & Permissions (ADMIN)</h2>
      <div class="grid grid-cols-2 gap-4">
        <div class="border rounded p-3">
          <div class="font-medium mb-2">Role list</div>
          <ul class="space-y-1">
            <li class="border rounded px-2 py-1">ADMIN</li>
            <li class="border rounded px-2 py-1">USER</li>
            <li class="border rounded px-2 py-1">AUDITOR</li>
          </ul>
          <button class="mt-2 text-sm text-blue-600">+ New</button>
        </div>
        <div class="border rounded p-3">
          <div class="font-medium mb-2">Role form</div>
          <div class="space-y-2 text-sm text-slate-700">
            <div>Name: <input class="border rounded px-2 py-1 w-full" /></div>
            <div>Code: <input class="border rounded px-2 py-1 w-full" /></div>
            <div>Perms: <input class="border rounded px-2 py-1 w-full" /></div>
          </div>
          <button class="mt-3 bg-slate-900 text-white px-3 py-1 rounded text-sm">Save</button>
        </div>
      </div>
    </div>
  `,
})
export class AdminRolesComponent {}
