import axios from 'axios';

export default class RegisterService {
  processRegistration(account: any): Promise<any> {
    // Don't include invitation token in the registration request as it's not part of the user account
    const registrationData = { ...account };
    if (registrationData.invitationToken) {
      // Remove invitation token from registration data as it's not part of the user account
      delete registrationData.invitationToken;
    }
    
    return axios.post('api/register', registrationData);
  }
}
