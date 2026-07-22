import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import {
  ProfessionalProfile,
  ProfessionalRequest
} from '../models/professional-model';

@Injectable({ providedIn: 'root' })
export class ProfessionalService {

  private http = inject(HttpClient);
  private api = `${environment.apiUrl}/api/professionals`;

  getMyProfile() {
    return this.http.get<ProfessionalProfile>(`${this.api}/profile`);
  }

  createProfile(request: ProfessionalRequest) {
    return this.http.post<ProfessionalProfile>(`${this.api}/profile`, request);
  }

  updateProfile(request: Partial<ProfessionalRequest>) {
    return this.http.patch<ProfessionalProfile>(`${this.api}/profile`, request);
  }

  getAllProfessionals() {
    return this.http.get<ProfessionalProfile[]>(this.api);
  }

  getProfessionalById(id: number) {
    return this.http.get<ProfessionalProfile>(`${this.api}/${id}`);
  }
}
