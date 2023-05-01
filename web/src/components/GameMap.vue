<template>
    <!-- let parent point to this div -->
  <div class="gamemap" ref="parent">
    <!-- let canvas points to this canvas tag -->
    <canvas ref="canvas">

    </canvas>
  </div>
</template>

<script>
import { GameMap } from '@/assets/scripts/GameMap';
import { ref, onMounted } from 'vue';

export default {
    setup(){
        //can be viewed as pointer
        //only variable in ref can be used in template
        let parent = ref(null);
        let canvas = ref(null);

        //onMounted is put into setup as well
        onMounted(()=>{
            //need to use value attribute to get value
            new GameMap(canvas.value.getContext('2d'), parent.value);
        });
        

        return{
            parent,
            canvas,
        }
    }
}
</script>

<style scoped>
div.gamemap{
    width: 100%;
    height: 100%;
    /* below three lines together center the canvas*/
    display: flex;
    justify-content: center; /*horizontally center*/
    align-items: center; /*vertically center*/
}
</style>