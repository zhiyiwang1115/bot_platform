<template>
    <ContentField>
        <table class="table table-striped table-hover" style="text-align: center;">
                            <!-- when defining table, need to include thead, tr and th for header -->
                            <thead>
                                <tr>
                                    <th>Username</th>
                                    <th>Rating</th>
                                </tr>
                            </thead>
                             <!-- when defining table, need to include tbody, tr and th for body -->
                            <tbody>
                                <!-- use v-for for for loop, need to bind a unique id for the loop to key attribute -->
                                <tr v-for="user in users" :key="user.id">
                                    <td>
                                        <img :src="user.photo" alt="" class="user-photo">
                                        &nbsp;
                                        <span class="user-username">{{ user.username }}</span>
                                    </td>    
                                    <td>{{user.rating}}</td>
                                </tr>
                            </tbody>
                        </table>
    <nav aria-label="...">
    <ul class="pagination" style="float: right;">
        <!-- previous page would pass -2 -->
        <li class="page-item" @click="clickPage(-2)">
        <a class="page-link" href="#">Previous</a>
        </li>
        <li :class="'page-item '+page.is_active" v-for="page in pages" :key="page.number" @click="clickPage(page.number)">
            <a class="page-link" href="#">
                {{page.number}}
            </a>
        </li>

        <!-- <li class="page-item"><a class="page-link" href="#">1</a></li>
        <li class="page-item"><a class="page-link" href="#">2</a></li>
        <li class="page-item active" aria-current="page">
        <a class="page-link" href="#">3</a>
        </li>
        <li class="page-item"><a class="page-link" href="#">4</a></li>
        <li class="page-item"><a class="page-link" href="#">5</a></li> -->
        <li class="page-item">
        <!-- next page would pass -1 -->
        <a class="page-link" href="#" @click="clickPage(-1)">Next</a>
        </li>
    </ul>
    </nav> 
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue'
import { useStore } from 'vuex';
import $ from 'jquery';
import { ref } from 'vue';

export default{
    components: {
        ContentField,
    },
    setup(){
        const store = useStore();
        let users = ref([]);
        let current_page = 1;
        let total_users = 0;
        let pages = ref([]);

        const clickPage = page => {
            if(page===-2)page = current_page - 1;
            if(page===-1)page = current_page + 1;
            let max_pages = parseInt(Math.ceil(total_users/3));
            if(page>=1 && page<=max_pages){
                current_page = page;
                pullPage(page);
            }
        };

        const updatePages = () => {
            let max_pages = parseInt(Math.ceil(total_users/3));
            let new_pages = [];
            for(let i = current_page - 2;i<=current_page+2;i++){
                if(i>=1 && i<=max_pages){
                    new_pages.push({
                        number: i,
                        is_active: i===current_page ? "active" : "",
                    });
                }
            }
            pages.value = new_pages;
        }

        const pullPage = page => {
            //current_page becomes page
            current_page = page;
            $.ajax({
                url: "http://127.0.0.1:3000/api/ranklist/getlist/",
                type: "get",
                //pay attention, this should be headers rather than header
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                data:{
                    page,
                },
                success(resp){
                    users.value = resp.users;
                    total_users = resp.users_cnt;
                    updatePages();
                },
                error(resp){
                    console.log(resp);
                }
            });
        }
        pullPage(current_page);

        return{
            users,
            current_page,
            total_users,
            pullPage,
            pages,
            clickPage,
        }
    }


}
</script>


<!-- "scoped" keyword makes the css only apply to current component -->
<style scoped>
img.user-photo{
    width: 4vh;
    border-radius: 50%;
}
</style>