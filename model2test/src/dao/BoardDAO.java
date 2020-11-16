package dao;

import static db.JdbcUtil.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.sql.DataSource;
import vo.BoardBean;

public class BoardDAO {
	DataSource ds;
	Connection con;
	private static BoardDAO boardDAO;
	
	private BoardDAO() {
		
	}
	
	public static BoardDAO getInstance() {
		if(boardDAO == null) {
			boardDAO = new BoardDAO();
		}
		return boardDAO;
	}
	
	public void setConnection(Connection con) {
		this.con = con;
	}
	
	//글의 개수 구하기
	public int selectListCount() {
		int listCount = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = con.prepareStatement("select count(*) from board");
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				listCount = rs.getInt(1);
			}
		} catch(Exception ex) {
			System.out.println("getListCount 에러: " + ex);
		} finally {
			close(rs);
			close(pstmt);
		}
		return listCount;
	}
	
	//글 목록 보기
	public ArrayList<BoardBean> selectArticleList(int page, int limit) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String board_list_sql = "select * from board order by BOARD_RE_REF desc, BOARD_RE_SEQ asc limit ?, 10";
		ArrayList<BoardBean> articleList = new ArrayList<BoardBean>();
		BoardBean board = null;
		int startrow = (page - 1) * 10; //읽기 시작할 row번호
		
		try {
			pstmt = con.prepareStatement(board_list_sql);
			pstmt.setInt(1, startrow);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				board = new BoardBean();
				board.setBOARD_NUM(rs.getInt("BOARD_NUM"));
				board.setBOARD_NAME(rs.getString("BOARD_NAME"));
				board.setBOARD_SUBJECT(rs.getString("BOARD_SUBJECT"));
				board.setBOARD_CONTENT(rs.getString("BOARD_CONTENT"));
				board.setBOARD_FILE(rs.getString("BOARD_FILE"));
				board.setBOARD_RE_REF(rs.getInt("BOARD_RE_REF"));
				board.setBOARD_RE_LEV(rs.getInt("BOARD_RE_LEV"));
				board.setBOARD_RE_SEQ(rs.getInt("BOARD_RE_SEQ"));
				board.setBOARD_READCOUNT(rs.getInt("BOARD_READCOUNT"));
				board.setBOARD_DATE(rs.getDate("BOARD_DATE"));
				articleList.add(board);
			}
		} catch(Exception ex) {
			System.out.println("getBoardList 에러: " + ex);
		} finally {
			close(rs);
			close(pstmt);
		}
		return articleList;
	}
	
	//글 내용 보기
	public BoardBean selectArticle(int board_num) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BoardBean boardBean = null;
		
		try {
			pstmt = con.prepareStatement("select * from board where BOARD_NUM = ?");
			pstmt.setInt(1, board_num);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				boardBean = new BoardBean();
				boardBean.setBOARD_NUM(rs.getInt("BOARD_NUM"));
				boardBean.setBOARD_NAME(rs.getString("BOARD_NAME"));
				boardBean.setBOARD_SUBJECT(rs.getString("BOARD_SUBJECT"));
				boardBean.setBOARD_CONTENT(rs.getString("BOARD_CONTENT"));
				boardBean.setBOARD_FILE(rs.getString("BOARD_FILE"));
				boardBean.setBOARD_RE_REF(rs.getInt("BOARD_RE_REF"));
				boardBean.setBOARD_RE_LEV(rs.getInt("BOARD_RE_LEV"));
				boardBean.setBOARD_RE_SEQ(rs.getInt("BOARD_RE_SEQ"));
				boardBean.setBOARD_READCOUNT(rs.getInt("BOARD_READCOUNT"));
				boardBean.setBOARD_DATE(rs.getDate("BOARD_DATE"));
			}
		} catch(Exception ex) {
			System.out.println("getDetail 에러: " + ex);
		} finally {
			close(rs);
			close(pstmt);
		}
		return boardBean;
	}
	
	//글 등록
	public int insertArticle(BoardBean article) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int num = 0;
		String sql = "";
		int insertCount = 0;
		
		try {
			pstmt = con.prepareStatement("select max(board_num) from board");
			rs = pstmt.executeQuery();
			
			if(rs.next()) num = rs.getInt(1) + 1;
			else num = 1;
			
			sql = "insert into board (board_num, board_name, board_pass, board_subject,";
			sql += "board_content, board_file, board_re_ref," +
			"board_re_lev, board_re_seq, board_readcount," + "board_date) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
//			sql = "insert into board values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, article.getBOARD_NAME());
			pstmt.setString(3, article.getBOARD_PASS());
			pstmt.setString(4, article.getBOARD_SUBJECT());
			pstmt.setString(5, article.getBOARD_CONTENT());
			pstmt.setString(6, article.getBOARD_FILE());
			pstmt.setInt(7, num);
			pstmt.setInt(8, 0);
			pstmt.setInt(9, 0);
			pstmt.setInt(10, 0);
			
			insertCount = pstmt.executeUpdate();
		} catch(Exception ex) {
			System.out.println("boardInsert 에러: " + ex);
		} finally {
			close(rs);
			close(pstmt);
		}
		return insertCount;
	}
}
