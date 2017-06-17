package com.chess.servlet;

import com.chess.enums.ChessEnum;
import com.chess.enums.ColorEnum;
import com.chess.pojo.Chess;
import com.chess.pojo.ChessBoard;

import com.chess.util.ChessUtil;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Qapi on 2017/6/14.
 */
public class gameControl extends HttpServlet {
    private static ChessBoard chessBoard = ChessBoard.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        JSONObject jsonObject = null;
        // 根据way值判断所执行方法
        try {
            String way = req.getParameter("way");
            switch (way) {
                case "1":
                    reset();
                    break;
                case "2":
                    move(req);
                    break;
                default:
            }
            // 返回棋盘的JSON格式
            jsonObject = JSONObject.fromObject(chessBoard);
            out.println(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    /**
     * 重置游戏
     */
    private void reset() {
        chessBoard.emptyBoard().init();
    }

    /**
     * 移动棋子
     */
    private void move(HttpServletRequest req) {
        // 获取移动前后坐标
        int XA = Integer.parseInt(req.getParameter("XA"));
        int YA = Integer.parseInt(req.getParameter("YA"));
        int XB = Integer.parseInt(req.getParameter("XB"));
        int YB = Integer.parseInt(req.getParameter("YB"));
        Chess[][] board = chessBoard.getBoard();
        Chess chess = board[XA][YA]; // 所移动棋子
        // 判断移动是否符合规则
        if (chess != null && ChessUtil.moveOrNot(XB, YB, chess, board)) {
            // 符合规则且未杀死敌方将军
            if (board[XB][YB] == null || board[XB][YB].getIden() != ChessEnum.KING) {
                // 改变行权方
                ColorEnum color = chessBoard.getAuthority();
                color = color == ColorEnum.BLACK ? ColorEnum.RED : ColorEnum.BLACK;
                chessBoard.setAuthority(color);
            } else {
                // 一方将军死亡，结束游戏
                chessBoard.setEndFlag(Boolean.TRUE);
            }
            // 移动棋子
            board = ChessUtil.moveChessto(XB, YB, chess, board);
            chessBoard.setBoard(board);
        }
    }
}
