export function useVendorAuth() {
  const { isAnonymous } = useUserDetails();
  const router = useRouter();
  const isAuthModalOpen = ref(false);

  const checkVendorAccess = () => {
    if (isAnonymous()) {
      isAuthModalOpen.value = true;
      return false;
    }
    return true;
  };

  const closeAuthModal = () => {
    isAuthModalOpen.value = false;
  };

  const navigateToSignup = () => {
    router.push("/user/signup");
  };

  const navigateToSignin = () => {
    router.push("/user/signin");
  };

  return {
    isAuthModalOpen,
    checkVendorAccess,
    closeAuthModal,
    navigateToSignup,
    navigateToSignin,
  };
}
