export default{
    state: {
        //represent if it is a record 
        //since going to reuse gamemap for record
        is_record: false,
        a_steps: "",
        b_steps: "",
    },
    getters: {
    },
    mutations: {
        updateIsRecord(state,is_record){
            state.is_record = is_record;
        },
        updateSteps(state, data){
            state.a_steps = data.a_steps;
            state.b_steps = data.b_steps;
        },
    },
    actions: {
    },
    modules: {
    }
}