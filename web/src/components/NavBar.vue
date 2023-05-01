<template>
    <nav class="navbar navbar-expand-lg bg-body-tertiary navbar-dark bg-dark">
    <div class="container">
        <!-- equivalent to link in react, but add component name for routing -->
        <router-link class="navbar-brand" :to="{name: 'home'}">King of Bots</router-link>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarText">
        <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
            <router-link :class="route_name=='pk_index' ? 'nav-link active' : 'nav-link' " :to="{name: 'pk_index'}">PK</router-link>
            </li>
            <li class="nav-item">
                <router-link :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'record_index'}">Record</router-link>
            </li>
            <li class="nav-item">
                <router-link :class="route_name=='ranklist_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'ranklist_index'}">Ranklist</router-link>
            </li>
        </ul>
        <ul class="navbar-nav">
            <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            TEST
          </a>
          <ul class="dropdown-menu">
            <router-link class="dropdown-item" :to="{name: 'user_bot_index'}">My Bots</router-link>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#">Logout</a></li>
          </ul>
        </li>

        </ul>
        </div>
    </div>
    </nav>
</template>

<script>
//used to acquire which path it is now
import {useRoute} from 'vue-router'
//real time compute
import {computed} from 'vue'

export default{
    //entrance
    setup(){
        const route = useRoute();
        //get current route name
        let route_name = computed(()=>route.name);
        return{
            route_name
        }
    }
}
</script>

<style scoped>

</style>