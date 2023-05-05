<template>
  <div>
    <PlayGround v-if="$store.state.pk.status==='playing'"></PlayGround>
    <MatchGround v-if="$store.state.pk.status==='matching'"></MatchGround>
  </div>
</template>

<script>
import PlayGround from '@/components/PlayGround.vue'
import MatchGround from '@/components/MatchGround.vue'
import { onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';

export default{
    components: {
        PlayGround,
        MatchGround,
    },
    setup(){
      const store = useStore();
      const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}/` //note here it should be `` rather than ''

      //can use commit here as well
      store.commit("updateOpponent",{
        username: "Your Opponent",
        photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
      });
      let socket = null;
      //onMounted and steup is similar, could put these codes in setup as well
      onMounted(() => {
        socket = new WebSocket(socketUrl);

        //socket at frontend has similar opopen, onmessage and onclose function to define as backend
        socket.onopen = () => {
          console.log("connected!");
          //store socket after being connected
          store.dispatch("updateSocket", socket);
        }

        socket.onmessage = msg => {
          //message is different for different type of backend framework
          //in spring, data is stored in msg.data
          const data = JSON.parse(msg.data);
          if(data.event==="match-success"){
            store.commit("updateOpponent",{
              username: data.opponent_username,
              photo: data.opponent_photo,
            });
            //navigate to playground 2s after successful matching
            setTimeout(()=>{
              store.commit("updateStatus","playing");
            },2000);
            store.commit("updateGamemap",data.gamemap);
          }

        }

        socket.onclose = () => {
          console.log("disconnected");
          //otherwise, when navigate back from other pages, the page would still show playground
          store.commit("updateStatus","matching");
        }

      });

      onUnmounted(() => {
        //must disconnect when unmounted otherwise redundant connections
        socket.close();
      });
  }
}

</script>


<!-- "scoped" keyword makes the css only apply to current component -->
<style scoped>

</style>