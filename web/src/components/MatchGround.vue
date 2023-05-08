<template>
    <div class="matchground">
        <div class="row">
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="user-username">{{$store.state.user.username}}</div>
            </div>
            <div class="col-4">
                <div class="user-select-bot">
                    <!-- biredtional data binding -->
                    <select v-model="select_bot" class="form-select" aria-label="Default select example">
                        <option selected value="-1">Play by Yourself</option>
                        <option v-for="bot in bots" :key="bot.id" :value="bot.id">{{bot.title}}</option>
                    </select>
                </div>
 
            </div>
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.pk.opponent_photo" alt="">
                </div>
                <div class="user-username">{{$store.state.pk.opponent_username}}</div>
            </div>
        </div>
        <!-- use semicolon for multiple inline css -->
        <div class="col-12" style="text-align: center; padding-top: 15vh">
            <button type="button" class="btn btn-warning btn-large" @click="click_match_btn" >{{match_btn_info}}</button>
        </div>

    </div>
</template>

<script>
import { ref } from 'vue';
import { useStore } from 'vuex';
import $ from 'jquery';

export default {
    setup(){
        let match_btn_info = ref("Start Matching");
        const store = useStore();
        let bots = ref([]);
        let select_bot = ref("-1");

        const refresh_bots = () => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/getlist/",
                type: "get",
                //pay attention, this should be headers rather than header
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    bots.value = resp;
                },
                error(resp){
                    console.log(resp);
                }
            });
        }

        refresh_bots();

        const click_match_btn = () => {
            if(match_btn_info.value==="Start Matching"){
                match_btn_info.value = "Cancel Matching";
                console.log(select_bot.value);
                //use send() api to send string via websocket
                store.state.pk.socket.send(JSON.stringify({
                    event: "start-matching",
                    bot_id: select_bot.value,
                }));
            }
            else{
                match_btn_info.value = "Start Matching";
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop-matching",
                }));
            }
        }

        return{
            match_btn_info,
            click_match_btn,
            bots,
            select_bot,
        }
    }
    
}
</script>

<style scoped>
div.matchground{
    /* browser width and browser height */
    width: 60vw;
    height: 70vh;
    margin: 40px auto;
    background-color: rgba(50,50,50,0.5);
}

div.user-photo{
    text-align: center;
    /* inner magin */
    padding-top: 10vh;
}
div.user-photo>img{
    border-radius: 50%;
    width: 20vh;
}
div.user-username{
    text-align: center;
    font-size: 24px;
    font-weight: 600;
    color: white;
    padding-top: 2vh;
}
div.user-select-bot{
    padding-top: 20vh;
}

div.user-select-bot > select{
    width: 60%;
    margin: 0 auto;
}
</style>