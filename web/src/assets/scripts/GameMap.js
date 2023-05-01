import { GameObject } from "./GameObject";
import { Wall } from "./Wall";

export class GameMap extends GameObject {
    //ctx is canva, parent is the parent of ctx used to dynamically adjust the width and length of canva
    constructor(ctx, parent){
        super();
        this.ctx = ctx;
        this.parent = parent;
        //the absolute length of each unit, which be adjusted according to browser size
        this.L = 0;
        this.rows = 13;
        this.cols = 13;
        this.inner_walls_cnt = 20;
        this.walls = [];
    }

    check_connectivity(g, sx, sy, tx, ty){
        if(sx==tx && sy==ty)return true;
        g[sx][sy] = true;
        let dx = [0,-1,1,0];
        let dy = [1,0,0,-1];
        for(let i = 0;i<4;i++){
            let nx = sx + dx[i], ny = sy + dy[i];
            if(g[nx][ny])continue;
            if(this.check_connectivity(g,nx,ny,tx,ty))return true;
        }
        return false;
    }

    //the reason why wall color would cover map color is because the creation of walls is later than the one of map in the list
    //when rendering every object is rendered according to the order in the list
    create_walls(){
        const g = [];
        for(let r = 0;r<this.rows;r++){
            g.push([]);
            for(let c = 0;c<this.cols;c++){
                g[r].push(false);
            }
        }

        //construct wall on surroundings
        for(let r = 0;r<this.rows;r++){
            g[r][0] = g[r][this.cols-1] = true;
        }

        for(let c = 0;c<this.cols;c++){
            g[0][c] = g[this.rows-1][c] = true;
        }

        //create random walls on the map
        for(let i = 0;i<this.inner_walls_cnt/2;i++){
            for(let j = 0;j<1000;j++){
                //math.random() will randomly pick a floating number from 0 to 1
                let r = parseInt(Math.random()*this.rows);
                let c = parseInt(Math.random()*this.cols);
                if(g[r][c])continue;
                if((r==this.rows-2 && c==1) || (c==this.cols-2 && r==1))continue;
                g[r][c] = g[c][r]= true;
                break;
            }
        }

        //deep copy in js because js pass by reference
        const copy_g = JSON.parse(JSON.stringify(g));
        if(!this.check_connectivity(copy_g,this.rows-2,1,1,this.cols-2))return false;

        for(let r = 0;r<this.rows;r++){
            for(let c = 0;c<this.cols;c++){
                if(g[r][c]){
                    this.walls.push(new Wall(r,c,this));
                }
            }
        }

        return true;
    }

    start(){
        for(let i = 0;i<1000;i++)
        {
            if(this.create_walls())break;
        }
    }

    //update size of map according to the browser size
    update_size(){
        //width and height of the div passed in and compute the length of unit square
        this.L = parseInt(Math.min(this.parent.clientWidth/this.cols, this.parent.clientHeight/this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    update(){
        this.update_size();
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