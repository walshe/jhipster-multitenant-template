import { type IBusiness } from '@/shared/model/business.model';
import { type IUser } from '@/shared/model/user.model';

import { type BusinessRole } from '@/shared/model/enumerations/business-role.model';
export interface IBusinessInvitation {
  id?: number;
  role?: keyof typeof BusinessRole;
  token?: string;
  invitedEmail?: string;
  createdAt?: Date | null;
  updatedAt?: Date | null;
  business?: IBusiness | null;
  invitedBy?: IUser | null;
}

export class BusinessInvitation implements IBusinessInvitation {
  constructor(
    public id?: number,
    public role?: keyof typeof BusinessRole,
    public token?: string,
    public invitedEmail?: string,
    public createdAt?: Date | null,
    public updatedAt?: Date | null,
    public business?: IBusiness | null,
    public invitedBy?: IUser | null,
  ) {}
}
