import { type Ref, computed, defineComponent, inject, ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useVuelidate } from '@vuelidate/core';
import { email, helpers, maxLength, minLength, required, sameAs } from '@vuelidate/validators';
import { useLoginModal } from '@/account/login-modal';
import RegisterService from '@/account/register/register.service';
import { EMAIL_ALREADY_USED_TYPE, LOGIN_ALREADY_USED_TYPE } from '@/constants';
import { useRoute } from 'vue-router';
import BusinessInvitationService from '@/entities/business-invitation/business-invitation.service';
import { useAlertService } from '@/shared/alert/alert.service';

const loginPattern = helpers.regex(/^[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$|^[_.@A-Za-z0-9-]+$/);

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Register',
  validations() {
    return {
      registerAccount: {
        login: {
          required,
          minLength: minLength(1),
          maxLength: maxLength(50),
          pattern: loginPattern,
        },
        email: {
          required,
          minLength: minLength(5),
          maxLength: maxLength(254),
          email,
        },
        password: {
          required,
          minLength: minLength(4),
          maxLength: maxLength(254),
        },
      },
      confirmPassword: {
        required,
        minLength: minLength(4),
        maxLength: maxLength(50),
        sameAsPassword: sameAs(this.registerAccount.password),
      },
    };
  },
  setup() {
    const { showLogin } = useLoginModal();
    const registerService = inject('registerService', () => new RegisterService(), true);
    const businessInvitationService = inject('businessInvitationService', () => new BusinessInvitationService(), true);
    const alertService = inject('alertService', () => useAlertService(), true);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);
    const route = useRoute();

    const error: Ref<string> = ref('');
    const errorEmailExists: Ref<string> = ref('');
    const errorUserExists: Ref<string> = ref('');
    const success: Ref<boolean> = ref(false);
    const invitationToken: Ref<string> = ref('');

    const confirmPassword: Ref<any> = ref(null);
    const registerAccount: Ref<any> = ref({
      login: undefined,
      email: undefined,
      password: undefined,
    });

    // Check for invitation token in URL on component mount
    onMounted(() => {
      const token = route.query.invitationToken as string;
      if (token) {
        invitationToken.value = token;
        // Pre-populate email if available in invitation
        businessInvitationService()
          .getByToken(token)
          .then(response => {
            // Optionally pre-fill the email field if it matches the invitation
            if (response.invitedEmail) {
              registerAccount.value.email = response.invitedEmail;
            }
          })
          .catch(error => {
            console.error('Error fetching invitation details:', error);
            // Token might be invalid, but we still allow registration
            alertService.showInfo('Invitation token detected. Proceed with registration.');
          });
      }
    });

    return {
      showLogin,
      currentLanguage,
      registerService,
      businessInvitationService,
      alertService,
      error,
      errorEmailExists,
      errorUserExists,
      success,
      invitationToken,
      confirmPassword,
      registerAccount,
      v$: useVuelidate(),
      t$: useI18n().t,
    };
  },
  methods: {
    register(): void {
      this.error = null;
      this.errorUserExists = null;
      this.errorEmailExists = null;
      this.registerAccount.langKey = this.currentLanguage;

      // If there's an invitation token, include it in the registration
      if (this.invitationToken) {
        this.registerAccount.invitationToken = this.invitationToken;
      }

      this.registerService
        .processRegistration(this.registerAccount)
        .then(() => {
          this.success = true;
          
          // If there was an invitation token, try to accept the invitation after successful registration
          if (this.invitationToken) {
            setTimeout(() => {
              // After successful registration, try to accept the invitation
              this.businessInvitationService()
                .acceptInvitation(this.invitationToken)
                .then(() => {
                  this.alertService.showSuccess('Successfully joined the business!');
                })
                .catch(error => {
                  console.error('Error accepting invitation:', error);
                  // This might be expected if the invitation was already accepted
                  this.alertService.showInfo('Registration successful! You may need to contact the business owner to be added to the business.');
                });
            }, 1000); // Small delay to ensure registration completes first
          }
        })
        .catch(error => {
          this.success = null;
          if (error.response?.status === 400 && error.response.data?.type === LOGIN_ALREADY_USED_TYPE) {
            this.errorUserExists = 'ERROR';
          } else if (error.response?.status === 400 && error.response.data?.type === EMAIL_ALREADY_USED_TYPE) {
            this.errorEmailExists = 'ERROR';
          } else {
            this.error = 'ERROR';
          }
        });
    },
  },
});
