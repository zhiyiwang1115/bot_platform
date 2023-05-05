export default{
    state: {
        status: "matching", //two statuses (matching and playing) to display index
        socket: null,
        opponent_username: "",
        opponent_photo: "",
        gamemap: null,
    },
    getters: {
    },
    mutations: {
        updateSocket(state, socket){
            state.socket = socket;
        },
        updateOpponent(state, opponent){
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status){
            state.status = status;
        },
        updateGamemap(state, gamemap){
            state.gamemap = gamemap;
        }
    },
    actions: {
        updateSocket(context, socket){
            context.commit("updateSocket", socket);
        },
    },
    modules: {
    }
}