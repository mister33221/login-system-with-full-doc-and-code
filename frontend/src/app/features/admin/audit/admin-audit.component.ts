import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-audit',
  standalone: true,
  template: `
    <div class="p-6">
      <h2 class="text-lg font-semibold mb-3">Audit (ADMIN/AUDITOR)</h2>
      <div class="mb-3 text-sm space-x-2">
        Filters:
        <input class="border px-2 py-1 rounded" placeholder="user" />
        <input class="border px-2 py-1 rounded" placeholder="resource" />
        <input class="border px-2 py-1 rounded" placeholder="action" />
      </div>
      <table class="w-full text-sm border">
        <thead class="bg-slate-100">
          <tr>
            <th class="border px-2 py-1 text-left">Time</th>
            <th class="border px-2 py-1 text-left">User</th>
            <th class="border px-2 py-1 text-left">Action</th>
            <th class="border px-2 py-1 text-left">Result</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="border px-2 py-1">2025-12-07</td>
            <td class="border px-2 py-1">admin</td>
            <td class="border px-2 py-1">login</td>
            <td class="border px-2 py-1">allow</td>
          </tr>
          <tr>
            <td class="border px-2 py-1">2025-12-07</td>
            <td class="border px-2 py-1">alice</td>
            <td class="border px-2 py-1">role:update</td>
            <td class="border px-2 py-1">deny</td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
})
export class AdminAuditComponent {}
