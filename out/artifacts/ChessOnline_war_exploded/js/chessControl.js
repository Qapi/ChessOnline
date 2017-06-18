/**
 * Created by Qapi on 2017/6/15.
 */
var vm = new Vue({
    el: '#main',
    data: {
        beginGame: false,     // 开始游戏按钮标志
        chessBoard: {         // 棋盘
            board: [],        // 虚拟棋格
            authority: null,  // 行权方
            endFlag: null,    // 结束标志
        },
        // 坐标
        XA: null,
        YA: null,
        XB: null,
        YB: null,
    },
    // 测试初始功能
    // mounted: function () {
    //     this.$nextTick(function () {
    //         this.initGame();
    //     })
    // },
    methods: {
        // 开始或重置游戏
        initGame: function () {
            let way = this.beginGame ? '1' : '';
            axios.get('gameControl.do', {
                params: {
                    way: way
                }
            }).then(res => {
                this.chessBoard = res.data;
                this.beginGame = true;
                this.empty();
            })
        },
        // 移动棋子
        moveChess: function (chess, XN, YN) {
            // 游戏尚未结束
            if (!this.chessBoard.endFlag) {
                if (this.XA == null && chess != null && chess.color == this.chessBoard.authority) {
                    console.log("原坐标：" + XN + "-" + YN);
                    this.XA = XN;
                    this.YA = YN;
                } else if (this.XA != null && (this.XA != XN || this.YA != YN)) {
                    console.log("目标坐标：" + XN + "-" + YN);
                    this.XB = XN;
                    this.YB = YN;
                    axios.get('gameControl.do', {
                        params: {
                            way: "2",
                            XA: this.XA,
                            YA: this.YA,
                            XB: this.XB,
                            YB: this.YB
                        }
                    }).then(res => {
                        this.chessBoard = res.data;
                        if (this.chessBoard.endFlag) {
                            this.endGame(this.chessBoard.authority);
                        }
                        this.empty();
                    })
                }
            }
        },
        // 结束游戏
        endGame(color){
            let msg = '红棋';
            if (color == 'BLACK') {
                msg = '黑棋';
            }
            // TODO 可用layer弹窗组件等代替
            alert("恭喜，获胜的是：" + msg);
        },
        // 清空坐标记录
        empty(){
            this.XA = null;
            this.YA = null;
            this.XB = null;
            this.YB = null;
        }
    }
})