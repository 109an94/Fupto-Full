export default ()=>{
    const id = useState('id', ()=>null);
    const username = useState('username', ()=>null);
    const email = useState('email', ()=>null);
    const roles = useState('roles', ()=>[]);
    const token = useState('token', ()=>[]);

    const isAnonymous = ()=>{
        return username.value === null;
    }

    const setAuthentication = (loginInfo)=>{
        id.value = loginInfo.id;
        username.value = loginInfo.username;
        email.value = loginInfo.email;
        roles.value = loginInfo.roles;
        token.value = loginInfo.token;

        if(!import.meta.env.SSR){
            localStorage.setItem('id', loginInfo.id);
            localStorage.setItem('username', loginInfo.username);
            localStorage.setItem('email', loginInfo.email);
            localStorage.setItem('roles', JSON.stringify(loginInfo.roles)); //[]->"[]"
            localStorage.setItem('token', loginInfo.token);
        }
    }

    const loadUserFromStorage = async () =>{

        if(!import.meta.env.SSR){
            if(!localStorage.getItem("username")) return;

            id.value = localStorage.getItem("id");
            username.value = localStorage.getItem("username");
            email.value = localStorage.getItem("email");
            roles.value = JSON.parse(localStorage.getItem("roles")); //[ROLE_ADMIN]
            token.value = localStorage.getItem("token");

            // await new Promise(resolve => setTimeout(resolve, 10));
        }
    }

    const logout = ()=>{
        id.value = null;
        username.value = null;
        email.value = null;
        roles.value = [];
        token.value = null;
        if (process.client){
            localStorage.removeItem('id');
            localStorage.removeItem('username');
            localStorage.removeItem('email');
            localStorage.removeItem('roles');
            localStorage.removeItem('token');
        }
    }

    const hasRole = (role)=>roles.value.includes(role);

    //마운트시 로컬스토리지에서 데이터 입력
    // if (process.client){
    //     onMounted(()=>{
    //         loadUserFromStorage();
    //     })
    // }

    return {
        id,
        username,
        email,
        roles,
        token,
        isAnonymous,
        setAuthentication,
        hasRole,
        logout,
        loadUserFromStorage
    }
}