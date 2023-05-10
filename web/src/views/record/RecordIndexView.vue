<template>
    <ContentField>
        <table class="table table-striped table-hover" style="text-align: center;">
                            <!-- when defining table, need to include thead, tr and th for header -->
                            <thead>
                                <tr>
                                    <th>A</th>
                                    <th>B</th>
                                    <th>Result</th>
                                    <th>Time</th>
                                    <th>Operations</th>
                                </tr>
                            </thead>
                             <!-- when defining table, need to include tbody, tr and th for body -->
                            <tbody>
                                <!-- use v-for for for loop, need to bind a unique id for the loop to key attribute -->
                                <tr v-for="record in records" :key="record.record.id">
                                    <td>
                                        <img :src="record.a_photo" alt="" class="record-user-photo">
                                        &nbsp;
                                        <span class="record-user-username">{{ record.a_username }}</span>
                                    </td>
                                    <td>
                                        <img :src="record.b_photo" alt="" class="record-user-photo">
                                        <span class="record-user-username">{{ record.b_username }}</span>
                                    </td>
                                    <td>{{record.result}}</td>
                                    <td>{{record.record.createTime}}</td>
                                    <td><button type="button" class="btn btn-secondary" @click="openRecordContent(record)">Replay</button></td>
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
import router from '@/router/index';

export default{
    components: {
        ContentField,
    },
    setup(){
        const store = useStore();
        let records = ref([]);
        let current_page = 1;
        let total_records = 0;
        let pages = ref([]);

        const clickPage = page => {
            if(page===-2)page = current_page - 1;
            if(page===-1)page = current_page + 1;
            let max_pages = parseInt(Math.ceil(total_records/10));
            if(page>=1 && page<=max_pages){
                current_page = page;
                pullPage(page);
            }
        };

        const updatePages = () => {
            let max_pages = parseInt(Math.ceil(total_records/10));
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
                url: "http://127.0.0.1:3000/api/record/getlist/",
                type: "get",
                //pay attention, this should be headers rather than header
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                data:{
                    page,
                },
                success(resp){
                    records.value = resp.records;
                    total_records = resp.records_cnt;
                    updatePages();
                },
                error(resp){
                    console.log(resp);
                }
            });
        }
        pullPage(current_page);

        const stringTo2D = map => {
            let g = [];
            for(let i = 0, k = 0;i<13;i++){
                let line = [];
                for(let j = 0;j<14;j++,k++){
                    if(map[k]==='0')line.push(0);
                    else line.push(1);
                }
                g.push(line);
            }
            return g;
        }

        const openRecordContent = record => {
                    //need to change state when pk game map is opened
                store.commit("updateIsRecord",true);
                store.commit("updateSteps",{
                    a_steps: record.record.asteps,
                    b_steps: record.record.bsteps,
                });
                store.commit("updateLoser",record.record.loser);
                console.log(record.record);
                store.commit("updateGame",{
                    map: stringTo2D(record.record.map),
                });
                //jump to page with parameter
                router.push({
                    name: "record_content",
                    params: {
                        recordId: record.record.id,
                        a_id: record.record.aid,
                        a_sx: record.record.asx,
                        a_sy: record.record.asy,
                        b_id: record.record.bid,
                        b_sx: record.record.bsx,
                        b_sy: record.record.bsy,

                    }
                });
        }

        return{
            records,
            current_page,
            total_records,
            pullPage,
            openRecordContent,
            pages,
            clickPage,
        }
    }


}
</script>


<!-- "scoped" keyword makes the css only apply to current component -->
<style scoped>
img.record-user-photo{
    width: 4vh;
    border-radius: 50%;
}
</style>