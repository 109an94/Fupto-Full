export function useFavorite() {
  const { isAnonymous, logout } = useUserDetails();
  const router = useRouter();

  const toggleFavorite = async (mappingId) => {
    if (isAnonymous()) {
      router.push(`/user/signin?returnURL=${encodeURIComponent(router.currentRoute.value.fullPath)}`);
      return false;
    }

    try {
      await use$Fetch(`/products/${mappingId}/favorite`, {
        method: "PATCH",
      });
      return true;
    } catch (error) {
      if (error.status === 401) {
        // 토큰 만료시 로그아웃 처리
        logout();
        router.push(`/user/signin?returnURL=${encodeURIComponent(router.currentRoute.value.fullPath)}`);
      }
      console.error("Failed to toggle favorite:", error);
      return false;
    }
  };

  return {
    toggleFavorite,
  };
}
