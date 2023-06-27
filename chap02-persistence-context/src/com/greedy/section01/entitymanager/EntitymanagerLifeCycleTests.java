package com.greedy.section01.entitymanager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntitymanagerLifeCycleTests {

    /* 엔티티 매니저(EntityManager)
        엔티티 매니저는 엔티티를 저장하는 메모리상의 데이터베이스라고 생각하면 된다.
        엔티티를 저장하고 수정하고 삭제하고 조회하는 등의 엔티티와 관련된 모든 일을 한다.
        엔티티 매니저는 스레드세이프 하지 않기 때문에 동시성 문제가 발생할 수 있다.
        따라서 스레드간 공유를 하지 않도록 web의 경우 일반적으로 request scope와 일치시킨다.

        엔티티 매니저 팩토리(EntityManagerFactory)
        엔티티 매니저를 생성할 수 있는 기능을 제공하는 팩토리 클래스이다.
        스레드세이프이기 때문에 여러 스레드가 동시에 접근해도 안전하기 때문에 서로 다른 스레드간 공유해서 재사용한다.
        하지만 스레드 세이프한 기능을 요청 스코프마다 생성하기에는 비용(시간, 메모리)부담이 크기 때문에
        application 스코프와 동일한 싱글톤으로 생성해서 관리하게 된다.
        따라서 데이터베이스를 사용하는 애플리케이션 당 한개의 EntityManagerfactory를 생성한다.

        영속성 컨텍스트(PersistenceContext)
        영속성 컨텍스트는 엔티티를 영구 저장하는 환경을 말한다.
        엔티티 매니저에 엔티티를 저장하거나 조회하면 엔티티 매니저는 영속성 컨텍스트에 엔티티를 보관하고 관리한다.
        영속성 엔티티를 key value 방식으로 저장하는 저상소 역할을 한다.
        영속성 컨텍스트는 엔티티 매니저를 생성할 때 하나 만들어진다.
        그리고 엔티티 매니저를 통해서 영속성 컨텍스트에 접근할 수 있고 영속성 컨텍스트를 관리할 수 있다.
    * */

    private static EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;

    @BeforeAll
    public static void  initFactory() {
        /* createEntityManagerFactory는 환경설정정보를 담고있는 xml 설정내용을 기반으로
        *  factory를 만든다.
        *  jpatest라는 파일을 작성하고, 그 파일의 설정내용을 기반으로 factory를 만들어 반환한다.
        *  프로젝트의 기본경로 밑에 META-INF에 설정정보를 위치시켜야된다.
        *  파일 이름도 persistence.xml로 작성해야 된다.*/
        entityManagerFactory = Persistence.createEntityManagerFactory("jpatest");
    }

    @BeforeEach
    public void initManager() {
        entityManager = entityManagerFactory.createEntityManager();
    }

    @Test
    public void 엔티티_매니저_팩토리와_엔티티_매니저_생명주기_확인용_메소드1() {
        System.out.println("entityManagerFactory.hashcode : " + entityManagerFactory.hashCode());
        System.out.println("entityManager.hashcode : " + entityManager.hashCode());
    }
    @Test
    public void 엔티티_매니저_팩토리와_엔티티_매니저_생명주기_확인용_메소드2() {
        System.out.println("entityManagerFactory.hashcode : " + entityManagerFactory.hashCode());
        System.out.println("entityManager.hashcode : " + entityManager.hashCode());
    }
}
