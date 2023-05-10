<template>
    <ContentField>
        <!-- grid layout in bootstrap, split the whole with 12 parts, col-6 will take 6 parts -->
        <!-- justify-content-md-center is used to center -->
        <div class="row justify-content-md-center">
            <div class="col-6">
                <!-- 'prevent' keyword prevents default action of form -->
                <form @submit.prevent="register">
                <div class="mb-3">
                    <label for="username" class="form-label">Username</label>
                    <!-- v-model is used to bind ref variables with the content of input-->
                    <!-- remind that ref="username" is binding username with the 'input' component itself, which is different -->
                    <!-- type here is important, or it may cause not trigger submit -->
                    <input v-model="username" type="text" class="form-control" id="username" placeholder="Username">
                </div>
                <div class="mb-3">
                    <label for="password" class="form-label">Password</label>
                    <input v-model="password" type="password" class="form-control" id="password" placeholder="Password">
                </div>
                <div class="mb-3">
                    <label for="confirmedPassword" class="form-label">Confirmed Password</label>
                    <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword" placeholder="Confirmed Password">
                </div>
                <div class="error-message">{{error_message}}</div>
                <button type="submit" class="btn btn-primary">Submit</button>
                </form>
            </div>
        </div>
    </ContentField>  
</template>

<script>
import ContentField from '@/components/ContentField.vue'
//similar to state in react
import {ref} from 'vue'
//import router to navigate
import router from '@/router/index'
import $ from 'jquery'

export default{
    components: {
        ContentField,
    },
    //setup is like defining all varibales and functions required on the page as preparation
    setup(){
        //get global store
        let username = ref('');
        let password = ref('');
        let confirmedPassword = ref('')
        let error_message = ref('');

        const register = ()=>{
            $.ajax({
                //must including http, also cannot be https!!!
                url: "http://127.0.0.1:3000/api/user/account/register/",
                type: "post",
                data: {
                    username: username.value,
                    password: password.value,
                    confirmed_password: confirmedPassword.value,
                },
                success(resp){
                    if(resp.error_message === "success"){
                        router.push({name: "user_account_login"});
                    }else{
                        error_message.value = resp.error_message;
                    }
                },
                error(){
                    error_message.value = "something goes wrong, please try again";
                },
            });
        };

        return {
            username,
            password,
            confirmedPassword,
            error_message,
            register,
        }

    }
}
</script>


<!-- "scoped" keyword makes the css only apply to current component -->
<style scoped>
button{
    width: 100%;
}

div.error-message{
    color: red;
}
</style>