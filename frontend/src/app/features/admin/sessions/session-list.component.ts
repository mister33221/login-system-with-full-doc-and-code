import { Component } from '@angular/core';

@Component({
  selector: 'app-session-list',
  standalone: true,
  template: `
    <div class="p-6">
      <h2 class="text-lg font-semibold mb-3">Sessions</h2>
      <table class="w-full text-sm border">
        <thead class="bg-slate-100">
          <tr>
            <th class="border px-2 py-1 text-left">Device</th>
            <th class="border px-2 py-1 text-left">IP</th>
            <th class="border px-2 py-1 text-left">Status</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td class="border px-2 py-1">Chrome</td>
            <td class="border px-2 py-1">1.1.1.1</td>
            <td class="border px-2 py-1">Active</td>
          </tr>
          <tr>
            <td class="border px-2 py-1">Mobile</td>
            <td class="border px-2 py-1">2.2.2.2</td>
            <td class="border px-2 py-1">Revoked</td>
          </tr>
        </tbody>
      </table>
      <div class="mt-3 space-x-2">
        <button class="bg-slate-900 text-white px-3 py-1 rounded text-sm">Revoke Selected</button>
        <button class="bg-slate-200 text-slate-900 px-3 py-1 rounded text-sm">Revoke All</button>
      </div>
    </div>
  `,
})
export class SessionListComponent {}
