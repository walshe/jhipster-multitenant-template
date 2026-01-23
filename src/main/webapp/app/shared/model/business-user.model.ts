import { type IBusiness } from '@/shared/model/business.model';
import { type IUser } from '@/shared/model/user.model';

import { type BusinessRole } from '@/shared/model/enumerations/business-role.model';
export interface IBusinessUser {
  id?: number;
  role?: keyof typeof BusinessRole;
  createdAt?: Date | null;
  updatedAt?: Date | null;
  business?: IBusiness | null;
  user?: IUser | null;
}

export class BusinessUser implements IBusinessUser {
  constructor(
    public id?: number,
    public role?: keyof typeof BusinessRole,
    public createdAt?: Date | null,
    public updatedAt?: Date | null,
    public business?: IBusiness | null,
    public user?: IUser | null,
  ) {}
}
