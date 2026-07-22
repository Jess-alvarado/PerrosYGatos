export interface OwnerProfile {
  id: number;
  userId: number;
  phone: string;
  address: string;
  birthDate: string;
}

export interface OwnerCreateRequest {
  phone: string;
  address: string;
  birthDate: string;
}
