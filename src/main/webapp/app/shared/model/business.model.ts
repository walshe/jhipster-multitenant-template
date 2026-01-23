import { type IUser } from '@/shared/model/user.model';

export interface IBusiness {
  id?: number;
  name?: string;
  slug?: string;
  createdAt?: Date | null;
  updatedAt?: Date | null;
  owner?: IUser | null;
}

export class Business implements IBusiness {
  constructor(
    public id?: number,
    public name?: string,
    public slug?: string,
    public createdAt?: Date | null,
    public updatedAt?: Date | null,
    public owner?: IUser | null,
  ) {}
}
