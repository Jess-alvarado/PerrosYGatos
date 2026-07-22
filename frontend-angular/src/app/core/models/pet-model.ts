export type PetType = 'DOG' | 'CAT';

export interface Pet {
  id: number;
  name: string;
  type: PetType;
  breed: string;
  age: number;
  sterilized: boolean;
  sex: string;
  personalityDescription: string;
}

export interface PetRequest {
  name: string;
  type: PetType;
  breed: string;
  age: number;
  sterilized: boolean;
  sex: string;
  behaviorDescription: string;
}
