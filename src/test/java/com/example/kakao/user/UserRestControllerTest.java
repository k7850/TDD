package com.example.kakao.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.kakao._core.advice.ValidAdvice;
import com.fasterxml.jackson.databind.ObjectMapper;

@Import({ValidAdvice.class}) // WebMvc가 ioc에 올려주지 않는 건 직접 로딩해야 함
@EnableAspectJAutoProxy // AOP 활성화
@WebMvcTest(UserRestController.class) // filter - d.s. - usercontroller 메모리에 띄워준다
public class UserRestControllerTest {

    @Autowired
    private MockMvc mvc; // 컨트롤러 요청 객체

    @MockBean // 유저서비스 가짜 객체 만듬
    private UserService userService;

    @Autowired
    private ObjectMapper om;

    @Test
    public void join_test() throws Exception{
        // given (데이터 준비)
        UserRequest.JoinDTO joinDTO = new UserRequest.JoinDTO();
        joinDTO.setEmail("cos@nate.com");
        joinDTO.setPassword("A#12345678");
        joinDTO.setUsername("cos");

        // json 직접 적기 복잡하니까 만들어서 ObjectMapper로 json으로 변환
        String requestBody = om.writeValueAsString(joinDTO);
        System.out.println("테스트 : "+requestBody);

        // when (실행)
        // ResultActions actions = mvc.perform(MockMvcRequestBuilders.post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON)); // 자주 쓰는건 static import
        ResultActions actions = mvc.perform(post("/join").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);
        
        // then (상태 검증)
        // actions.andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true)); // 자주 쓰는건 static import
        actions.andExpect(jsonPath("$.success").value(true));
        actions.andExpect(jsonPath("$.response").value(nullValue()));
        actions.andExpect(jsonPath("$.error").value(nullValue()));
    }





    @Test
    public void login_test() throws Exception{
        // given (데이터 준비)
        UserRequest.LoginDTO loginDTO = new UserRequest.LoginDTO();
        loginDTO.setEmail("cos@nate.com");
        loginDTO.setPassword("A#12345678");
        
        String requestBody = om.writeValueAsString(loginDTO);
        System.out.println("테스트 : "+requestBody);

        // stub (가정) // 덜 만든 기능의 리턴값을 가정할 수 있음
        when(userService.login(any())).thenReturn("abcd");

        // when (실행)
        ResultActions actions = mvc.perform(post("/login").content(requestBody).contentType(MediaType.APPLICATION_JSON));
        String authorization = actions.andReturn().getResponse().getHeader("Authorization");
        System.out.println("테스트 : "+authorization);
        String responseBody = actions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : "+responseBody);

        // then (상태 검증)
        actions.andExpect(MockMvcResultMatchers.header().string("Authorization", "Bearer abcd"));
        actions.andExpect(jsonPath("$.success").value(true));
        actions.andExpect(jsonPath("$.response").value(nullValue()));
        actions.andExpect(jsonPath("$.error").value(nullValue()));
    }
}