<template>
        <PlayGround></PlayGround>
</template>

<script>
import PlayGround from '@/components/PlayGround.vue'
import $ from 'jquery'

export default{
    components: {
        PlayGround,
    },
    setup(){
    

    $.ajax({
      //must including http, also cannot be https!!!
      url: "http://127.0.0.1:3000/user/account/register/",
      type: "post",
      data: {
        username: "s",
        password: "",
        confirmed_password: "pe",
      },
      success(resp){
        console.log(resp);
      },
      error(resp){
        console.log(resp);
      },
    });
    
  }
}

</script>


<!-- "scoped" keyword makes the css only apply to current component -->
<style scoped>

</style>