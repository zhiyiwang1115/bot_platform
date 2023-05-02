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
      url: "http://127.0.0.1:3000/user/account/token/",
      type: "post",
      data: {
        username: "a",
        password: "pa",
      },
      success(resp){
        console.log(resp);
      },
      error(resp){
        console.log(resp);
      },
    });
    $.ajax({
      //must including http, also cannot be https!!!
      url: "http://127.0.0.1:3000/user/account/info/",
      type: "get",
      headers: {
        Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJjMjk5N2E3NmZlYTM0NGZiOTQyNzgzOTI3MmE1MDg2YSIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTY4MzAyMzg3MCwiZXhwIjoxNjg0MjMzNDcwfQ.nHvs8Zsmouovv6pAYXg-kYOfPShmR7KdTT-CORkGqi4"
      },
      success(resp){
        console.log(resp);
      },
      error(resp){
        console.log(resp);
      },
    });
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