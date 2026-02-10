import axios from 'axios';

import buildPaginationQueryOpts from '@/shared/sort/sorts';

import { type IBusinessInvitation } from '@/shared/model/business-invitation.model';

const baseApiUrl = 'api/business-invitations';

export default class BusinessInvitationService {
  
  find(id: number): Promise<IBusinessInvitation> {
    return new Promise<IBusinessInvitation>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  retrieve(paginationQuery?: any): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}?${buildPaginationQueryOpts(paginationQuery)}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  delete(id: number): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .delete(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  create(entity: IBusinessInvitation): Promise<IBusinessInvitation> {
    return new Promise<IBusinessInvitation>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  createForBusiness(businessId: number, invitedEmail: string, role: string): Promise<IBusinessInvitation> {
    return new Promise<IBusinessInvitation>((resolve, reject) => {
      axios
        .post(`api/businesses/${businessId}/invitations?invitedEmail=${invitedEmail}&role=${role}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  update(entity: IBusinessInvitation): Promise<IBusinessInvitation> {
    return new Promise<IBusinessInvitation>((resolve, reject) => {
      axios
        .put(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  partialUpdate(entity: IBusinessInvitation): Promise<IBusinessInvitation> {
    return new Promise<IBusinessInvitation>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  getByToken(token: string): Promise<IBusinessInvitation> {
    return new Promise<IBusinessInvitation>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/by-token/${token}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  acceptInvitation(token: string): Promise<IBusinessInvitation> {
    return new Promise<IBusinessInvitation>((resolve, reject) => {
      axios
        .post(`api/business-invitations/accept`, { token: token })
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
