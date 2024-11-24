import authGlobal from "~/middleware/auth.global.js";

export default defineNuxtPlugin((nuxtApp) => {
  const userDetails = useUserDetails();

  if (import.meta.client) {
    userDetails.loadUserFromStorage();
  }
  addRouteMiddleware("auth.global", authGlobal, { override: true });
});
