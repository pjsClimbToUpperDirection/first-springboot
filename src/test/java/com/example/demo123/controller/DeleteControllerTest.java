package com.example.demo123.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeleteController.class)
public class DeleteControllerTest {

    // 현재 컨트롤러 클래스에 dao 기능을 하는 코드가 같이 들어있어 PostRepository 빈 주입이 요구되는 상황 -> todo 추후 dao 로직을 분리하는 걸 고려하기
    @Autowired
    private MockMvc mockMvc; // Spring Mvc controller 테스트를 위한 가상의 웹 환경 제공

    @Test
    public void DeleteControllerTestMethod() throws Exception {
        //String expectedJson = "{\"Deleted\":\"{\"writer\":\"title\"}\"}";

        mockMvc.perform(delete("/api/v1/delete-api/delete")
                .param("writer", "park").param("title", "c1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Deleted").exists())
                .andExpect(jsonPath("$.Deleted.writer").exists())
                .andDo(print());
    }
}
