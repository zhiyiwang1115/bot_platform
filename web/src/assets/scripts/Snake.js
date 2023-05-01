import { GameObject } from "./GameObject";
import { Cell } from "./Cell";

export class Snake extends GameObject{
    //different info means different snakes
    constructor(info, gamemap){
        super();
        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;
        this.cells = [new Cell(info.r, info.c)] //store whole snake, cell[0] is snake head
        this.speed = 5; //5 units per second
        this.direction = -1; //the direciton input by player or backend 0 - up 1 - right 2 - down 3 - left
        this.status = "idle"; //idle - no action, move - currently moving, die - already dead
        this.next_cell = null; //target of next move
        this.dr = [-1,0,1,0];
        this.dc = [0,1,0,-1];
        this.step = 0;//when step is less than 10, then snake will be one cell longer; after than, the snake will be one cell longer after each 3 steps
        this.eps = 1e-2; //error threshold
        this.eye_direction = this.id===0 ? 0 : 2; //left-down snake's eyes are initially up; right-up snake's eyes are initially down
        //offset of snake's eye for four directions (left eye and right eye)
        this.eye_dx = [[-1,1],[1,1],[1,-1],[-1,-1]];
        this.eye_dy = [[-1,-1],[-1,1],[1,1],[1,-1]];
    }


    start(){

    }

    set_direction(d){
        this.direction = d;
    }

    check_tail_increasing(){
        if(this.step<=10)return true;
        if(this.step%3===1)return true;
        return false;
    }

    next_step(){
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r+this.dr[d], this.cells[0].c+this.dc[d]);
        this.eye_direction = d;
        this.direction = -1; //empty the operation
        this.status = "move";
        this.step++;

        //move just the head, remain others the same
        const k = this.cells.length;
        for(let i = k;i>0;i--){
            this.cells[i]=JSON.parse(JSON.stringify(this.cells[i-1]));
        }

        if(!this.gamemap.check_valid(this.next_cell)){ //next step is invalid, snake dies
            //if a nake is in status "die", it would never become "idle" so that the gamemap would never be ready for next move
            this.status = "die";
        }
    }

    update_move(){
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx*dx+dy*dy);
        //if next move and current head positon is close, don't need to move
        if(distance<this.eps){
            this.status = "idle"; //finished moving
            this.cells[0] = this.next_cell; //add a new snake head
            this.next_cell = null;
            if(!this.check_tail_increasing())this.cells.pop();
        }else{
             //timedelta is in million seconds
             const move_distance = this.speed * this.timedelta/1000;
             this.cells[0].x += move_distance * dx / distance; //distance to move on x-axis
             this.cells[0].y += move_distance * dy / distance; //distance to move on y-axis

             if(!this.check_tail_increasing()){
                const k = this.cells.length;
                const tail = this.cells[k-1], tail_target = this.cells[k-2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                this.cells[k-1].x += move_distance * tail_dx / distance;
                this.cells[k-1].y += move_distance * tail_dy / distance;
             }
        }
    }

    update(){
        if(this.status==="move"){
            this.update_move();
        }
        this.render();
    }

    render(){
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;
        //factor to make snake slim
        const factor = 0.8;

        ctx.fillStyle = this.color;
        if(this.status==="die")ctx.fillStyle = "white";
        for(const cell of this.cells){
            //draw a circle
            ctx.beginPath();
            ctx.arc(cell.x*L, cell.y*L, L/2*factor, 0, 2*Math.PI); //center of circle, radius, angle from 0 to 2pi
            ctx.fill();
        }
        //make snake more goodlooking by adding a rectangular between two adjacent circles
        for(let i = 1;i<this.cells.length;i++){
            const a = this.cells[i-1], b = this.cells[i];
            if(Math.abs(a.x-b.x)<this.eps && Math.abs(a.y-b.y)<this.eps)continue;
            //vertical
            if(Math.abs(a.x-b.x)<this.eps){
                ctx.fillRect((a.x-0.5*factor)*L,Math.min(a.y, b.y)*L,L*factor,Math.abs(a.y-b.y)*L);
            }else{
                //hotizontal
                ctx.fillRect(Math.min(a.x, b.x)*L,(a.y-0.5*factor)*L,Math.abs(a.x-b.x)*L,L*factor);
            }
        }

        ctx.fillStyle = "black";
        for(let i = 0;i<2;i++){
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i]* 0.15) * L ;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i]* 0.15) * L ;
            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L*0.05, 0, 2*Math.PI);
            ctx.fill();
        }
    }

}