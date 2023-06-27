package com.greedy.section01.problem;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProblemOfUsingSqlTests {

    private Connection con;

    @BeforeEach
    public void setConnection() throws ClassNotFoundException, SQLException {

        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "C##GREEDY";
        String password = "GREEDY";

        Class.forName(driver);

        con = DriverManager.getConnection(url, user, password);
    }

    @AfterEach
    public void closeConnection() throws SQLException {

        con.close();
    }

    /* JDBC API를 이용해 직접 SQL을 다룰 때 발생하는 문제점
    * 1. 데이터 변환, SQL 작성, JDBC API코드 등을 반복적으로 일일히 다 작성해야 한다.
    * 2. SQL에 의존적인 개발을 하게 된다.
    * 3. 패러다임 불일치 문제 (상속, 연관관계, 객체 그래프 탐색)
    * 4. 동일성 보장
    * */
    @Test
    public void 직접_SQL_을_작성하여_메뉴_조회하는_기능_테스트() throws SQLException {

        //given

        //when
        String query = "SELECT MENU_CODE, MENU_NAME, MENU_PRICE, CATEGORY_CODE, ORDERABLE_STATUS FROM TBL_MENU";

        Statement stmt = con.createStatement();
        ResultSet rset = stmt.executeQuery(query);

        List<Menu> menuList = new ArrayList<Menu>();
        while (rset.next()) {
            Menu menu = new Menu();
            menu.setMenuCode(rset.getInt("MENU_CODE"));
            menu.setMenuName(rset.getString("MENU_NAME"));
            menu.setMenuPrice(rset.getInt("MENU_PRICE"));
            menu.setCategoryCode(rset.getInt("CATEGORY_CODE"));
            menu.setOrderableStatus(rset.getString("ORDERABLE_STATUS"));

            menuList.add(menu);
        }

        rset.close();
        stmt.close();

        //then
        assertNotNull(menuList);
        menuList.forEach(menu -> System.out.println(menu));
    }

    @Test
    public void 직접_SQL_작성하여_메뉴_추가하는_기능_테스트() throws SQLException {

        //given
        Menu menu = new Menu();
        menu.setMenuName("멸치알리조또");
        menu.setMenuPrice(10000);
        menu.setCategoryCode(9);
        menu.setOrderableStatus("Y");

        //when
        String query = "INSERT INTO TBL_MENU(MENU_CODE, MENU_NAME, MENU_PRICE, CATEGORY_CODE, ORDERABLE_STATUS)" +
                "VALUES (SEQ_MENU_CODE.NEXTVAL, ?, ?, ?, ?)";
        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setString(1, menu.getMenuName());
        pstmt.setInt(2, menu.getMenuPrice());
        pstmt.setInt(3, menu.getCategoryCode());
        pstmt.setString(4, menu.getOrderableStatus());

        int result = pstmt.executeUpdate();

        pstmt.close();

        //then
        assertEquals(1, result);
    }

    /* 만약 데이터베이스가 아닌 자바 컬렉션에 데이터를 저장하거나 꺼내오는 방식이라고 가정하면?
     * list.add(menu);
     * list.get(1);
     *
     * JPA에서는?
     * Menu menu = entityManager.find(Menu.class, 1);
     * entityManager.persist(menu);
     * */
    /* 2. SQL에 의존적인 개발을 하게 된다. */
    @Test
    public void 조회_항목_변경에_따른_의존성_확인_테스트() throws SQLException {

        /* 최초 요구사항은 메뉴 코드와 메뉴 이름을 조회하는 것이다. */
        //given

        //when
        String query = "SELECT MENU_CODE, MENU_NAME FROM TBL_MENU";

        Statement stmt = con.createStatement();
        ResultSet rset = stmt.executeQuery(query);

        List<Menu> menuList = new ArrayList<>();
        while (rset.next()) {
            Menu menu = new Menu();
            menu.setMenuCode(rset.getInt("MENU_CODE"));
            menu.setMenuName(rset.getString("MENU_NAME"));

            menuList.add(menu);
        }

        stmt.close();
        rset.close();

        //then
        assertNotNull(menuList);
        menuList.forEach(System.out::println);

    }

    @Test
    public void 연관된_객체_문제_테스트() throws SQLException {

        //given

        //when
        String query =
                "SELECT A.MENU_CODE, A.MENU_NAME, A.MENU_PRICE, B.CATEGORY_CODE, B.CATEGORY_NAME, A.ORDERABLE_STATUS " +
                        "FROM TBL_MENU A " +
                        "JOIN TBL_CATEGORY B ON (A.CATEGORY_CODE = B.CATEGORY_CODE)";
        Statement stmt = con.createStatement();
        ResultSet rset = stmt.executeQuery(query);

        List<MenuAndCategory> menuAndCategoryList = new ArrayList<>();
        while (rset.next()) {
            MenuAndCategory menuAndCategory = new MenuAndCategory();
            menuAndCategory.setMenuCode(rset.getInt("MENU_CODE"));
            menuAndCategory.setMenuName(rset.getString("MENU_NAME"));
            menuAndCategory.setMenuPrice(rset.getInt("MENU_PRICE"));
            menuAndCategory.setCategory(new Category(rset.getInt("CATEGORY_CODE"),
                    rset.getString("CATEGORY_NAME")));
            menuAndCategory.setOrderableStatus(rset.getString("ORDERABLE_STATUS"));

            menuAndCategoryList.add(menuAndCategory);
        }
        rset.close();
        stmt.close();

        //then
        assertNotNull(menuAndCategoryList);
        menuAndCategoryList.forEach(System.out::println);

    }

    /*
     * 3. 패러다임 불일치의 문제가 발생할 수 있다.
     * 관계형 데이터 베이스는 데이터를 중심으로 구조화 되어 있고, 집합적인 사고를 요구한다.
     * 객체지향에서 이야기하는 추상화, 상속, 다형성 같은 개념이 존재하지 않는다.
     * 지향하는 목적 자체가 다르기 때문에 이를 표현하는 방법이 다르고,
     * 그렇기 때문에 객체 구조를 테이블 구조에 저장하는데 한계가 있다.
     * */

    /*
     * 3-1. 상속 문제
     * 객체지향언어의 상속 개념과 유사한 것이 데이터베이스의 서브타입 엔터티 이다.
     * 유사한 것 같지만 다른 부분은 데이터베이스의 상속은 상속 개념을 데이터로 추상화하여 슈퍼타입과 서브타입으로 구분하고
     * 슈퍼타입의 모든 속성을 서브타입이 공유하지 못하며 물리적으로도 다른 테이블로 분리가 된 형태가 된다.
     * (설계에 따라 속성으로 추가되기도 함)
     * 두 개의 서로 다른 테이블을 조회하기 위해서는 공유하는 컬럼(FK)을 이용해 조인해야 한다.
     * 하지만 객체 지향 상속은 슈퍼타입의 소성을 공유해서 사용하기 때문에 패러다임 불일치 현상이 발생하게 된다.
     *
     * INSERT 예시
     * JDBC를 이용하면
     * INSERT INTO 회원...
     * INSERT INTO 법인회원...
     *
     * JPA를 이용하는 경우에는
     * entityManager.persist(법인회원);
     * */

    /*
     * 3-2. 연관관계
     * 객체지향에서 말하는 가지고 있다(has-a) 라고 표현되는 연관관계는 데이터베이스의 저장 구조와는 다른 형태이다.
     *
     * 데이터베이스 테이블에 맞춘 객체모델
     * public class Menu {
     *       private int menuCode;
     *       private String menuName;
     *       private int menuPrice;
     *       private int categoryCode;
     *       private String orderableStatus;
     * }
     *
     * public class Category {
     *       private int categoryCode;
     *       private String categoryName;
     * }
     *
     * 객체지향에 어울리는 구조로 생각해보면
     * public class Menu {
     *   private int menuCode;
     *   private String menuName;
     *   private int menuPrice;
     *   private Category category;
     *   private String orderableStatus;
     * }
     *
     * 메뉴와 카테고리의 관계 설정정    * Menu menu = new Menu();
     * Category category = new Category();
     *
     * menu.setCategory(category);
     *
     * menu.getCategory().getCategoryName();
     *
     * JPA는 또 이런 문제가 해결이 된다.
     * Menu menu = entityManager.find(Menu.class, menuCode);
     * menu.getCategory().getCategoryName();     //연관 객체를 함께 조인해서 조회하는 것이 보장된다.
     * */

    /*
     * 3-3. 객체 그래프 탐색
     * 위와 같이 연관관계가 연이어서 여러 개의 객체간 복잡한 관계가 형성이 된 경우에는 문제가 심각해질 수 있다.
     * */

    /*
     * 4. 동일성 보장
     * 객체의 비교는 동일성 비교와 동등성 비교라는 두 가지 방법으로 구분이 된다.
     * JDBC를 이용하여 조회한 두 개의 Menu객체는 동일한 로우를 조회하더라도 같은 값을 가지는 동등성을 가지지만
     * 동일성을 가지지는 않는다. (==로 비교가 불가능하다.)
     * 반면 JPA를 이용하여 조회한 두 개의 Menu객체는 동일한 로우를 조회하는 경우 동일성을 보장하게 된다.
     * (단순 ==비교가 가능해진다.)
     * */

    @Test
    public void JDBC_API_동등성_확인_테스트() throws SQLException {

        //given
        int menuCode = 12;
        //when
        String query = "SELECT MENU_CODE, MENU_NAME FROM TBL_MENU WHERE MENU_CODE = ?";

        PreparedStatement pstmt1 = con.prepareStatement(query);
        pstmt1.setInt(1, menuCode);

        ResultSet rset1 = pstmt1.executeQuery();

        Menu menu1 = null;
        while(rset1.next()) {
            menu1 = new Menu();
            menu1.setMenuCode(rset1.getInt("MENU_CODE"));
            menu1.setMenuName(rset1.getString("MENU_NAME"));
        }

        PreparedStatement pstmt2 = con.prepareStatement(query);
        pstmt2.setInt(1, menuCode);

        ResultSet rset2 = pstmt2.executeQuery();

        Menu menu2 = null;
        while(rset2.next()) {
            menu2 = new Menu();
            menu2.setMenuCode(rset2.getInt("MENU_CODE"));
            menu2.setMenuName(rset2.getString("MENU_NAME"));
        }

        rset1.close();
        rset2.close();
        pstmt1.close();
        pstmt2.close();

        //then
        assertFalse(menu1 == menu2);
        assertEquals(menu1.getMenuName(), menu2.getMenuName());
        System.out.println("menu1 = " + menu1);
        System.out.println("menu2 = " + menu2);

        /*
         * JPA로 구현하게 되면 동일 비교가 가능해진다.
         * Menu menu1 = entityManager.find(Menu.class, 12);
         * Menu menu2 = entityManager.find(Menu.class, 12);
         *
         * menu1 == menu2;       //true
         * */
    }
}
