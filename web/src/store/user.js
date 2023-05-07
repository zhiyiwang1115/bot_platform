import $ from 'jquery'
//used to store global user data, similar to redux in react.js
//it would need to include state, getters, mutations, actions, modules
//state: used to store data
//getters: seldom used
//mutations: some basic synchronization operation on state
//actions: asychronized operation should be put here (e.g. ajax)
//modules: import sub data store (see index.js under store)
export default{
    state: {
        id: "",
        username: "",
        phto: "",
        token: "",
        is_login: false,
        //variable to control the login page and navbar would not show before verifying local storage
        //otherwise there would be a flash
        pulling_info: true,
    },
    getters: {
    },
    mutations: {
        updateUser(state, user){
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token){
            state.token = token;
        },
        logout(state){
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        },
        updatePullingInfo(state, pulling_info){
            state.pulling_info = pulling_info;
        },
    },
    actions: {
        //in the action operation, when invoking the functions in mutations, we need invoke context.commit()
        login(context, data){
            $.ajax({
                //must including http, also cannot be https because of no certificaiton!!!
                url: "http://127.0.0.1:3000/user/account/token/",
                type: "post",
                data: {
                  username: data.username,
                  password: data.password,
                },
                success(resp){
                    if(resp.error_message==="success"){
                        //also store token in the local storage (do not need to import)
                        //the local storage is similar to a dictionary
                        localStorage.setItem("jwt_token", resp.token);
                        context.commit("updateToken", resp.token);
                        data.success(resp); //callback function
                    }
                },
                error(resp){
                  data.error(resp);
                },
              });
        },

        getinfo(context,data){
            $.ajax({
                //must including http, also cannot be https!!!
                url: "http://127.0.0.1:3000/user/account/info/",
                type: "get",
                headers: {
                    //use context to extract data in state
                  Authorization: "Bearer " + context.state.token,
                },
                success(resp){
                    if(resp.error_message==="success"){
                        context.commit("updateUser",{
                            //copy the information in resp
                            ...resp,
                            is_login: true,
                        });
                        data.success(resp);
                    }
                    else{
                        data.error(resp);
                    }
                },
                error(resp){
                    data.error(resp);
                },
              });
        },
        
        logout(context){
            //remove jwt token from local storage
            localStorage.removeItem("jwt_token");
            context.commit("logout");
        },

        updatePullingInfo(context){
            context.commit("updatePullingInfo");
        }

    },
    modules: {
    }
}