<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top: 20px">
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width:100%">
                    </div>
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top: 20px">
                    <div class="card-header">
                        <span style="font-size: 130%">My Bots</span> 
                        <!-- float-end class means right aligned -->
                        <!-- open modal based on id -->
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-bot-modal">Create a Bot</button>
                    
                    
                    </div>
                    <div class="card-body">
                        <table class="table table-striped table-hover">
                            <!-- when defining table, need to include thead, tr and th for header -->
                            <thead>
                                <tr>
                                    <th>Title</th>
                                    <th>Create Time</th>
                                    <th>Operations</th>
                                </tr>
                            </thead>
                             <!-- when defining table, need to include tbody, tr and th for body -->
                            <tbody>
                                <!-- use v-for for for loop, need to bind a unique id for the loop to key attribute -->
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.title }}</td>
                                    <td>{{ bot.createTime }}</td>
                                    <td>
                                        <!-- here each button control one modal, so the id of different modal should be different
                                        this can be implemented by adding bot id in the modal tag id -->
                                        <button type="button" class="btn btn-secondary" style="margin-right:10px" data-bs-toggle="modal" :data-bs-target="'#update-bot-modal-'+bot.id">Update</button>
                                        
                                        
                                        
                                        <!-- modal when updating bots -->
                                        <div class="modal fade" :id="'update-bot-modal-'+bot.id" tabindex="-1">
                                            <div class="modal-dialog modal-xl">
                                                <div class="modal-content">
                                                <div class="modal-header">
                                                    <h1 class="modal-title fs-5" id="exampleModalLabel">Update a Bot</h1>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                </div>
                                                <div class="modal-body">
                                                    <div class="mb-3">
                                                        <label for="add-bot-title" class="form-label">Title</label>
                                                        <input v-model="bot.title" type="text" class="form-control" id="add-bot-title" placeholder="Please add bot title">
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="add-bot-description" class="form-label">Description</label>
                                                        <!-- here row is used to expand the size of the textarea -->
                                                        <textarea v-model="bot.description" class="form-control" id="add-bot-description" rows="3" placeholder="Please add bot description"></textarea>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label for="add-bot-code" class="form-label">Code</label>
                                                        <!-- here row is used to expand the size of the textarea -->
                                                        <!-- <textarea v-model="bot.content" class="form-control" id="add-bot-code" rows="7" placeholder="Please add bot code"></textarea> -->
                                                        <VAceEditor v-model:value="bot.content"
                                                                        @init="editorInit" lang="c_cpp"
                                                                        :theme="aceConfig.theme" style="height: 300px"
                                                                        :options="aceConfig.options" class="ace-editor" />
                                                  </div>
                                                </div>
                                                <div class="modal-footer">
                                                    <div class="error-message">{{botadd.error_message}}</div>
                                                    <button type="button" class="btn btn-primary" @click="update_bot(bot)">Save</button>
                                                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                                                </div>
                                                </div>
                                            </div>
                                        </div>
                                        
                                        
                                        
                                        <button type="button" class="btn btn-danger" @click="remove_bot(bot)">Delete</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>


        
        <!-- modal when creating bots -->
        <div class="modal fade" id="add-bot-modal" tabindex="-1">
            <div class="modal-dialog modal-xl">
                <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">Create a Bot</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="add-bot-title" class="form-label">Title</label>
                        <input v-model="botadd.title" type="text" class="form-control" id="add-bot-title" placeholder="Please add bot title">
                    </div>
                    <div class="mb-3">
                        <label for="add-bot-description" class="form-label">Description</label>
                        <!-- here row is used to expand the size of the textarea -->
                        <textarea v-model="botadd.description" class="form-control" id="add-bot-description" rows="3" placeholder="Please add bot description"></textarea>
                    </div>
                    <div class="mb-3">
                        <label for="add-bot-code" class="form-label">Code</label>
                        <!-- here row is used to expand the size of the textarea -->
                        <!-- <textarea v-model="botadd.content" class="form-control" id="add-bot-code" rows="7" placeholder="Please add bot code"></textarea> -->
                        <VAceEditor v-model:value="botadd.content"
                                    @init="editorInit" lang="c_cpp"
                                    :theme="aceConfig.theme" style="height: 300px"
                                    :options="aceConfig.options" class="ace-editor" />
                     </div>
                </div>
                <div class="modal-footer">
                    <div class="error-message">{{botadd.error_message}}</div>
                    <button type="button" class="btn btn-primary" @click="add_bot">Save</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                </div>
                </div>
            </div>
        </div>





    </div>
</template>

<script>
import $ from 'jquery'
//need to import global store if we need token
import { useStore } from 'vuex';
//ref is usually binded with variables while reactive is usually binded with objects
//here list of bot is binded using ref while created bot is binded with reactive
import { ref, reactive } from 'vue';
//import Modal from bootstrap to hide modal with api
import { Modal } from 'bootstrap/dist/js/bootstrap';
//integrate the editor with ace editor
import { VAceEditor } from 'vue3-ace-editor';
//for highlight and autofill vaceEditor
//may have problem when import this pack, need to do "npm install --save-dev file-loader"
import "ace-builds/webpack-resolver";
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';


export default{
    components:{
        VAceEditor,
    },
    setup(){
        //for highlight and autofill vaceEditor
    const aceConfig = reactive({
            theme: 'chrome', //theme
            arr: [
                /*all themes*/
                "ambiance",
                "chaos",
                "chrome",
                "clouds",
                "clouds_midnight",
                "cobalt",
                "crimson_editor",
                "dawn",
                "dracula",
                "dreamweaver",
                "eclipse",
                "github",
                "gob",
                "gruvbox",
                "idle_fingers",
                "iplastic",
                "katzenmilch",
                "kr_theme",
                "kuroir",
                "merbivore",
                "merbivore_soft",
                "monokai",
                "mono_industrial",
                "pastel_on_dark",
                "solarized_dark",
                "solarized_light",
                "sqlserver",
                "terminal",
                "textmate",
                "tomorrow",
                "tomorrow_night",
                "tomorrow_night_blue",
                "tomorrow_night_bright",
                "tomorrow_night_eighties",
                "twilight",
                "vibrant_ink",
                "xcode",
            ],
            readOnly: false, //read only
            options: {
                enableBasicAutocompletion: true,
                enableSnippets: true,
                enableLiveAutocompletion: true,
                tabSize: 2,
                showPrintMargin: false,
                fontSize: 16
            }
        });

        const store = useStore();
        let bots = ref([]);

        const botadd = reactive({
            title: "",
            description: "",
            content: "",
            error_message: "",
        })

        const refresh_bots = () => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/getlist/",
                type: "get",
                //pay attention, this should be headers rather than header
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    bots.value = resp;
                },
                error(resp){
                    console.log(resp);
                }
            });
        }

        refresh_bots();

        const add_bot = () => {
            //empty the last error message first
            botadd.error_message = "";
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/add/",
                type: "post",
                data: {
                    title: botadd.title,
                    description: botadd.description,
                    content: botadd.content,
                },
                //pay attention, this should be headers rather than header
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    if(resp.error_message==="success"){
                        botadd.title = "";
                        botadd.description = "";
                        botadd.content = "";
                        //note must add # before id; close modal by id
                        Modal.getInstance("#add-bot-modal").hide();
                        refresh_bots();
                    }else{
                        botadd.error_message = resp.error_message;
                    }
                },
                error(resp){
                    console.log(resp);
                }
            });
        }

        const remove_bot = (bot) => {
            $.ajax({
            url: "http://127.0.0.1:3000/user/bot/remove/",
            type: "post",
            data: {
                bot_id: bot.id,
            },
            //pay attention, this should be headers rather than header
            headers:{
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp){
                if(resp.error_message==="success"){
                    refresh_bots();
                }
            },
            error(resp){
                console.log(resp);
            }
        });
        }


        const update_bot = (bot) => {
            //empty the last error message first
            botadd.error_message = "";
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/update/",
                type: "post",
                data: {
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,
                },
                //pay attention, this should be headers rather than header
                headers:{
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    if(resp.error_message==="success"){
                        //note must add # before id; close modal by id
                        Modal.getInstance('#update-bot-modal-'+bot.id).hide();
                    }else{
                        botadd.error_message = resp.error_message;
                    }
                    refresh_bots();
                },
                error(resp){
                    console.log(resp);
                }
            });
        }

        return{
            bots,
            botadd,
            add_bot,
            remove_bot,
            update_bot,
            aceConfig,
        }

    }

    
}
</script>


<!-- "scoped" keyword makes the css only apply to current component -->
<style scoped>
div.error-message{
    color: red;
}
</style>