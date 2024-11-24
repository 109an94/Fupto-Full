import { useFetch } from "#app";

export function useAuthFetch(url, options = {}) {
  const { token } = useUserDetails();
  const config = useRuntimeConfig();

  options.headers = {
    ...options.headers,
    ...(token.value && { Authorization: `Bearer ${token.value}` }),
  };

  const fullUrl = `${config.public.apiBase}${url}`;
  console.log("요청 URL:", fullUrl);
  console.log(`Bearer ${token.value}`);

  return useFetch(`${config.public.apiBase}${url}`, options);
}
