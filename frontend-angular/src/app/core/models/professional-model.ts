export interface ProfessionalProfile {
  id: number;
  userId: number;
  phone: string;
  address: string;
  birthDate: string;
  profession: string;
  bio: string;
  experienceYears: number;
  petTypes: string;
  rating: number;
  reviewCount: number;
  profilePictureUrl?: string;
  instagram?: string;
  website?: string;
  availability?: string;
  status: string;
}

export interface ProfessionalRequest {
  phone: string;
  address: string;
  birthDate: string;
  profession: string;
  bio: string;
  experienceYears: number;
  petTypes: string;
  profilePictureUrl?: string;
  instagram?: string;
  website?: string;
  availability?: string;
}
