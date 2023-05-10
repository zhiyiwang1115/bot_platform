import { GameObject } from "./GameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";

export class GameMap extends GameObject {
    //ctx is canva, parent is the parent of ctx used to dynamically adjust the width and length of canva
    constructor(ctx, parent, store){
        super();
        this.ctx = ctx;
        this.parent = parent;
        this.store = store;
        //the absolute length of each unit, which be adjusted according to browser size
        this.L = 0;
        this.rows = 13;
        this.cols = 14;
        this.inner_walls_cnt = 20;
        this.walls = [];

        this.snakes = [
            new Snake({id:0, color:"#4876EC", r: this.rows-2, c: 1},this),
            new Snake({id:1, color:"#F94848", r: 1, c: this.cols-2},this),
        ]
    }

    //map creation are put into backend

    // check_connectivity(g, sx, sy, tx, ty){
    //     if(sx==tx && sy==ty)return true;
    //     g[sx][sy] = true;
    //     let dx = [0,-1,1,0];
    //     let dy = [1,0,0,-1];
    //     for(let i = 0;i<4;i++){
    //         let nx = sx + dx[i], ny = sy + dy[i];
    //         if(g[nx][ny])continue;
    //         if(this.check_connectivity(g,nx,ny,tx,ty))return true;
    //     }
    //     return false;
    // }

    //the reason why wall color would cover map color is because the creation of walls is later than the one of map in the list
    //when rendering every object is rendered according to the order in the list
    create_walls(){
        // const g = [];
        // for(let r = 0;r<this.rows;r++){
        //     g.push([]);
        //     for(let c = 0;c<this.cols;c++){
        //         g[r].push(false);
        //     }
        // }

        // //construct wall on surroundings
        // for(let r = 0;r<this.rows;r++){
        //     g[r][0] = g[r][this.cols-1] = true;
        // }

        // for(let c = 0;c<this.cols;c++){
        //     g[0][c] = g[this.rows-1][c] = true;
        // }

        // //create random walls on the map
        // for(let i = 0;i<this.inner_walls_cnt/2;i++){
        //     for(let j = 0;j<1000;j++){
        //         //math.random() will randomly pick a floating number from 0 to 1
        //         let r = parseInt(Math.random()*this.rows);
        //         let c = parseInt(Math.random()*this.cols);
        //         if(g[r][c])continue;
        //         if((r==this.rows-2 && c==1) || (c==this.cols-2 && r==1))continue;
        //         g[r][c] = g[this.rows-1-r][this.cols-1-c]= true;
        //         break;
        //     }
        // }

        // //deep copy in js because js pass by reference
        // const copy_g = JSON.parse(JSON.stringify(g));
        // if(!this.check_connectivity(copy_g,this.rows-2,1,1,this.cols-2))return false;
        const g = this.store.state.pk.gamemap;
        for(let r = 0;r<this.rows;r++){
            for(let c = 0;c<this.cols;c++){
                if(g[r][c]){
                    this.walls.push(new Wall(r,c,this));
                }
            }
        }

        return true;
    }

    add_listening_events(){
        //if it is record, then not get any input from key board
        if(this.store.state.record.is_record){
            let k = 0;
            const a_steps = this.store.state.record.a_steps;
            const b_steps = this.store.state.record.b_steps;
            const loser = this.store.state.pk.loser;
            //every 300 ms change next direction
            //need to define interval id since we need to clear it afterwards
            const interval_id = setInterval(()=>{
                const [snake0, snake1] = this.snakes;
                if(k>=a_steps.length-1 || k>=b_steps.length-1){
                    //don't need to draw the last step
                    if(loser==="A" || loser==="all")snake0.status="die";
                    if(loser==="B" || loser==="all")snake1.status="die";
                    //clear the time interval
                    clearInterval(interval_id);
                }else{
                    snake0.set_direction(parseInt(a_steps[k]));
                    snake1.set_direction(parseInt(b_steps[k]));
                }
                k += 1;
            },300);
            return;
        }
        //focus canvas so that it can get user input
        this.ctx.canvas.focus();
        // const [snake0, snake1] = this.snakes;
        //bind the keyboard listener to canvas
        this.ctx.canvas.addEventListener("keydown", e => {
            // if(e.key==='w')snake0.set_direction(0);
            // else if(e.key==='d')snake0.set_direction(1);
            // else if(e.key==='s')snake0.set_direction(2);
            // else if(e.key==='a')snake0.set_direction(3);
            // else if(e.key==='ArrowUp')snake1.set_direction(0);
            // else if(e.key==='ArrowRight')snake1.set_direction(1);
            // else if(e.key==='ArrowDown')snake1.set_direction(2);
            // else if(e.key==='ArrowLeft')snake1.set_direction(3);

            //just need to retain one kinds of operation
            let d = -1;
            if(e.key==='ArrowUp')d = 0;
            else if(e.key==='ArrowRight')d = 1;
            else if(e.key==='ArrowDown')d = 2;
            else if(e.key==='ArrowLeft')d = 3;
            if(d>=0){
                this.store.state.pk.socket.send(JSON.stringify({
                    event: "move",
                    direction: d,
                }));
            }
        });
    }

    start(){
        // for(let i = 0;i<1000;i++)
        // {
        //     if(this.create_walls())break;
        // }
        this.create_walls();
        this.add_listening_events();
    }

    //check if both snakes on at idle status and have next direction to move, ready for next round
    check_ready(){
        for(const snake of this.snakes){
            if(snake.status!=='idle')return false;
            if(snake.direction===-1)return false;
        }
        return true;
    }

    //update size of map according to the browser size
    update_size(){
        //width and height of the div passed in and compute the length of unit square
        this.L = parseInt(Math.min(this.parent.clientWidth/this.cols, this.parent.clientHeight/this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    next_step(){
        for(const snake of this.snakes){
            snake.next_step();
        }
    }

    check_valid(cell){ //detech if target cell is valid, not conflict with wall or body of a snake
        for(const wall of this.walls){
            if(wall.r===cell.r && wall.c===cell.c)return false;
        }
        for(const snake of this.snakes){
            //when detecting, there is an edge case that snake tail may move or not
            let k = snake.cells.length;
            if(!snake.check_tail_increasing())k--; //when tail will move forward
            for(let i = 0;i<k;i++){
                if(snake.cells[i].r===cell.r && snake.cells[i].c===cell.c)return false;
            }
        }
        return true;
    }

    update(){
        this.update_size();
        if(this.check_ready()){
            this.next_step();

        }
        this.render();
    }

    render(){
        // this.ctx.fillStyle = 'blue';
        // this.ctx.fillRect(0,0,this.ctx.canvas.width,this.ctx.canvas.height);
        const color_even = "#AAD751"
        const color_odd = "#A2D149"
        for(let r = 0;r<this.rows;r++){
            for(let c = 0;c<this.cols;c++){
                if((r+c)%2==0){
                    this.ctx.fillStyle = color_even;
                }else{
                    this.ctx.fillStyle = color_odd;
                }
                //the axis of canvas: x is horizontal (left to right), y is vertical (from top to bottom) 
                this.ctx.fillRect(c*this.L,r*this.L, this.L, this.L);
            }
        }
    }

    on_destroy(){

    }
}