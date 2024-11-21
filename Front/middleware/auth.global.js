import useUserDetails from "~/composables/useUserDetails.js";

export default defineNuxtRouteMiddleware( async (to)=>{
    const userDetails = useUserDetails()
    const isRefresh = () => {
        const navEntries = performance.getEntriesByType('navigation')
        return navEntries.length > 0 && navEntries[0].type === 'reload'
    }

    //페이지 로드시 로컬 스토리지에서 사용자 정보를 로드
    await userDetails.loadUserFromStorage()
    if (process.client){

        if (to.path.startsWith('/admin')) {
            // 새로고침이 아닌 경우에만 권한 체크
            if (!isRefresh()) {
                if (!userDetails.hasRole("ROLE_ADMIN")) {
                    return navigateTo("/error403")
                }
                if (to.path !== '/admin/signin' && userDetails.isAnonymous()) {
                    return navigateTo(`/admin/signin?returnURL=${to.fullPath}`)
                }
            } else {
                // 새로고침인 경우, 사용자 정보가 로드될 때까지 짧게 대기
                await new Promise(resolve => setTimeout(resolve, 50))

                // 대기 후 다시 권한 체크
                if (!userDetails.hasRole("ROLE_ADMIN")) {
                    return navigateTo("/error403")
                }
            }
        }
    }
})