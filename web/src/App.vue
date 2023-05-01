<template>
  <NavBar />
  <!-- router-view controls components router -->
  <router-view></router-view>
</template>

<script>
import NavBar from '@/components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"

export default{
  //import components in vue, the key word must be "components"
  components: {
    NavBar
  }
}
</script>

<style>
body{
  background-image: url("@/assets/background.jpg");
  background-size: cover;
}
</style>
