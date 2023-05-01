//global variable to store all game objects
const GAME_OBJECTS = [];

export class GameObject{
    constructor(){
        GAME_OBJECTS.push(this);
        //because rendering is not uniformly distributed
        this.timedelta = 0;
        this.has_started = false;
    }

    //only executed once 
    start(){

    }

    //execueted every frame except the first frame
    update(){

    }

    //executed before destroyed
    on_destroy(){

    }

    destroy(){
        this.on_destroy();
        //'in' is iterating index; 'of' is iterating object
        for(let i in GAME_OBJECTS){
            const obj = GAME_OBJECTS[i];
            if(obj===this){
                //delete the ith elment in an array
                GAME_OBJECTS.splice(i);
                break;
            }
        }
    }
}

let last_timestamp;
//recursive invoke step for next frame so that step would be called for every frame
//timestamp input is the current timestamp
const step = timestamp =>{
    for(let obj of GAME_OBJECTS){
        if(!obj.has_started){
            obj.has_started = true;
            obj.start();
        }else{
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }
    last_timestamp = timestamp;
    requestAnimationFrame(step)
}
//it will take a callback function as input, before the next frame is rendered by browser, the callback function would be called
requestAnimationFrame(step)