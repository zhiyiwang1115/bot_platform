<template>
    <ContentField>UserBot</ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'
import $ from 'jquery'
//need to import global store if we need token
import { useStore } from 'vuex';


export default{
    components: {
        ContentField,
    },
    setup(){
        const store = useStore();
        // $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/add/",
        //     type: "post",
        //     data: {
        //         title: "test8",
        //         description: "test2",
        //         content: "test3",
        //     },
        //     //pay attention, this should be headers rather than header
        //     headers:{
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp){
        //         console.log(resp);
        //     }
        // });

        $.ajax({
            url: "http://127.0.0.1:3000/user/bot/remove/",
            type: "post",
            data: {
                bot_id: 1,
            },
            //pay attention, this should be headers rather than header
            headers:{
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp){
                console.log(resp);
            },
            error(resp){
                console.log(resp);
            }
        });

        //         $.ajax({
        //     url: "http://127.0.0.1:3000/user/bot/update/",
        //     type: "post",
        //     data: {
        //         bot_id: 6,
        //         title: "test_revised",
        //         description: "test_revised_2",
        //         content: "test_revised_3",
        //     },
        //     //pay attention, this should be headers rather than header
        //     headers:{
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp){
        //         console.log(resp);
        //     },
        //     error(resp){
        //         console.log(resp);
        //     }
        // });

        $.ajax({
            url: "http://127.0.0.1:3000/user/bot/getlist/",
            type: "get",
            //pay attention, this should be headers rather than header
            headers:{
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp){
                console.log(resp);
            },
            error(resp){
                console.log(resp);
            }
        });
    }

    
}
</script>


<!-- "scoped" keyword makes the css only apply to current component -->
<style scoped>

</style>