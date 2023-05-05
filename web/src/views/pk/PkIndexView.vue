<template>
        <PlayGround></PlayGround>
</template>

<script>
import PlayGround from '@/components/PlayGround.vue'
import $ from 'jquery'
import { onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';

export default{
    components: {
        PlayGround,
    },
    setup(){
      const store = useStore();
      const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}/` //note here it should be `` rather than ''

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
          console.log(data);
        }

        socket.onclose = () => {
          console.log("disconnected");
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