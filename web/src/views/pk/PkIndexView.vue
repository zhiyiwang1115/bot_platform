<template>
  <div>
    <PlayGround v-if="$store.state.pk.status==='playing'"></PlayGround>
    <MatchGround v-if="$store.state.pk.status==='matching'"></MatchGround>
    <ResultBoard v-if="$store.state.pk.loser!==''"/>
    <div class="user-color" v-if="$store.state.pk.status==='playing' && $store.state.user.id==$store.state.pk.a_id">Leftdown</div>
    <div class="user-color" v-if="$store.state.pk.status==='playing' && $store.state.user.id==$store.state.pk.b_id">Rightup</div>
  </div>
</template>

<script>
import PlayGround from '@/components/PlayGround.vue'
import MatchGround from '@/components/MatchGround.vue'
import ResultBoard from '@/components/ResultBoard.vue'
import { onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';

export default{
    components: {
        PlayGround,
        MatchGround,
        ResultBoard,
    },
    setup(){
      const store = useStore();
      const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}/` //note here it should be `` rather than ''

      //otherwise there would be a win/lose/draw box
      store.commit("updateLoser","");
      store.commit("updateIsRecord",false);
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
            },200);
            store.commit("updateGame",data.game);
          }
          else if(data.event==="move"){
            const game = store.state.pk.gameObject;
            const [snake0, snake1] = game.snakes;
            console.log(data);
            snake0.set_direction(data.a_direction);
            snake1.set_direction(data.b_direction);
          }
          else if(data.event==="result"){
            const game = store.state.pk.gameObject;
            const [snake0, snake1] = game.snakes;
            if(data.loser==="all" || data.loser==="A"){
              snake0.status = "die";
            }
            if(data.loser==="all" || data.loser==="B"){
              snake1.status = "die";
            }
            store.commit("updateLoser", data.loser);
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
div.user-color{
  text-align: center;
  color: black;
  font-size: 30px;
  font-weight: 600;
}
</style>