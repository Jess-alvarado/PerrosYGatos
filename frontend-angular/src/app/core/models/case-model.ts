export type CaseStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' | 'CANCELLED' | 'ABANDONED';
export type ProposalStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'WITHDRAWN' | 'CANCELLED';

export interface CasePetSnapshot {
  id: number;
  originalPetId: number;
  name: string;
  type: string;
  breed: string;
  age: number;
  sterilized: boolean;
  sex: string;
  personalityDescription: string;
}

export interface BehaviorCase {
  id: number;
  ownerId: number;
  title: string;
  description: string;
  detailedDescription?: string;
  hasChildren: boolean;
  hasOtherPets: boolean;
  hasAggression: boolean;
  isAloneFrequently: boolean;
  behaviorDuration: string;
  status: CaseStatus;
  pets: CasePetSnapshot[];
  createdAt: string;
  updatedAt: string;
}

export interface BehaviorCaseRequest {
  title: string;
  description: string;
  detailedDescription?: string;
  hasChildren: boolean;
  hasOtherPets: boolean;
  hasAggression: boolean;
  isAloneFrequently: boolean;
  behaviorDuration: string;
  pets: CasePetSnapshot[];
}

export interface CaseProposal {
  id: number;
  professionalId: number;
  approach: string;
  estimatedPrice: number;
  estimatedSessions: number;
  status: ProposalStatus;
  createdAt: string;
}

export interface CaseProposalRequest {
  approach: string;
  estimatedPrice: number;
  estimatedSessions: number;
}
