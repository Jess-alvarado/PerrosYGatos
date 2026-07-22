import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Pet, PetRequest } from '../models/pet-model';

@Injectable({ providedIn: 'root' })
export class PetService {

  private http = inject(HttpClient);
  private api = `${environment.apiUrl}/api/pets`;

  getMyPets() {
    return this.http.get<Pet[]>(this.api);
  }

  getPetById(id: number) {
    return this.http.get<Pet>(`${this.api}/${id}`);
  }

  addPet(request: PetRequest) {
    return this.http.post<Pet>(this.api, request);
  }
}
