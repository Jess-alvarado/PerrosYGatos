import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { OwnerProfile, OwnerCreateRequest } from '../models/owner-model';

@Injectable({ providedIn: 'root' })
export class OwnerService {

  private http = inject(HttpClient);
  private api = `${environment.apiUrl}/api`;

  getMyProfile() {
    return this.http.get<OwnerProfile>(`${this.api}/owners/profile`);
  }

  createProfile(request: OwnerCreateRequest) {
    return this.http.post<OwnerProfile>(`${this.api}/owners/profile`, request);
  }

  updateProfile(request: Partial<OwnerCreateRequest>) {
    return this.http.patch<OwnerProfile>(`${this.api}/owners/profile`, request);
  }
}
