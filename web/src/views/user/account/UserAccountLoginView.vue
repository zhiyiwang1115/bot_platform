<template>
    <ContentField>
        <!-- grid layout in bootstrap, split the whole with 12 parts, col-6 will take 6 parts -->
        <!-- justify-content-md-center is used to center -->
        <div class="row justify-content-md-center">
            <div class="col-6">
                <!-- 'prevent' keyword prevents default action of form -->
                <form @submit.prevent="login">
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
                <div class="error-message">{{error_message}}</div>
                <button type="submit" class="btn btn-primary">Submit</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'
//import useStore for global store
import {useStore} from 'vuex'
//similar to state in react
import {ref} from 'vue'
//import router to navigate
import router from '@/router/index'

export default{
    components: {
        ContentField,
    },
    //setup is like defining all varibales and functions required on the page as preparation
    setup(){
        //get global store
        const store = useStore();
        let username = ref('');
        let password = ref('');
        let error_message = ref('');

        const login = ()=>{
            console.log("here");
            //need use dispatch to invoke function in actions
            store.dispatch("login",{
                //attention: .value is important to add for ref variables
                username: username.value,
                password: password.value,
                success(resp){
                    store.dispatch("getinfo",{
                        success(){
                             //use push to navigate in the js
                            router.push({name: 'home'});
                            console.log(store.state.user);
                        },
                        error(resp){
                            error_message.value = "something goes wrong, please try again"
                        }
                    });
                },
                error(resp){
                    error_message.value = "username or password is wrong";
                }
            });
        };

        return {
            username,
            password,
            error_message,
            login,
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