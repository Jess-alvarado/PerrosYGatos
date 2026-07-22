import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import {
  BehaviorCase,
  BehaviorCaseRequest,
  CaseProposal,
  CaseProposalRequest,
  CaseStatus
} from '../models/case-model';

@Injectable({ providedIn: 'root' })
export class CaseService {

  private http = inject(HttpClient);
  private api = `${environment.apiUrl}/api/cases`;

  // Owner
  createCase(request: BehaviorCaseRequest) {
    return this.http.post<BehaviorCase>(this.api, request);
  }

  getMyCases() {
    return this.http.get<BehaviorCase[]>(`${this.api}/my`);
  }

  getCaseById(id: number) {
    return this.http.get<BehaviorCase>(`${this.api}/${id}`);
  }

  updateCaseStatus(caseId: number, newStatus: CaseStatus) {
    return this.http.patch<BehaviorCase>(
      `${this.api}/${caseId}/status`,
      null,
      { params: { newStatus } }
    );
  }

  // Professional
  getOpenCases() {
    return this.http.get<BehaviorCase[]>(this.api);
  }

  // Proposals
  createProposal(caseId: number, request: CaseProposalRequest) {
    return this.http.post<CaseProposal>(
      `${this.api}/${caseId}/proposals`, request);
  }

  getProposals(caseId: number) {
    return this.http.get<CaseProposal[]>(
      `${this.api}/${caseId}/proposals`);
  }

  acceptProposal(caseId: number, proposalId: number) {
    return this.http.patch<CaseProposal>(
      `${this.api}/${caseId}/proposals/${proposalId}/accept`, null);
  }

  withdrawProposal(caseId: number, proposalId: number) {
    return this.http.patch<CaseProposal>(
      `${this.api}/${caseId}/proposals/${proposalId}/withdraw`, null);
  }
}
