package com.openisle.controller;

import com.openisle.model.Tag;
import com.openisle.service.TagService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
@AutoConfigureMockMvc(addFilters = false)
class TagControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @Test
    void createAndGetTag() throws Exception {
        Tag t = new Tag();
        t.setId(1L);
        t.setName("java");
        t.setDescribe("d");
        t.setIcon("i");
        Mockito.when(tagService.createTag(eq("java"), eq("d"), eq("i"))).thenReturn(t);
        Mockito.when(tagService.getTag(1L)).thenReturn(t);

        mockMvc.perform(post("/api/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"java\",\"describe\":\"d\",\"icon\":\"i\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("java"))
                .andExpect(jsonPath("$.describe").value("d"))
                .andExpect(jsonPath("$.icon").value("i"));

        mockMvc.perform(get("/api/tags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void listTags() throws Exception {
        Tag t = new Tag();
        t.setId(2L);
        t.setName("spring");
        t.setDescribe("d2");
        t.setIcon("i2");
        Mockito.when(tagService.listTags()).thenReturn(List.of(t));

        mockMvc.perform(get("/api/tags"))
                .andExpect(status().isOk())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("spring"))
                .andExpect(jsonPath("$[0].describe").value("d2"))
                .andExpect(jsonPath("$[0].icon").value("i2"));
    }

    @Test
    void updateTag() throws Exception {
        Tag t = new Tag();
        t.setId(3L);
        t.setName("java");
        t.setDescribe("d3");
        t.setIcon("i3");
        Mockito.when(tagService.updateTag(eq(3L), eq("java"), eq("d3"), eq("i3"))).thenReturn(t);

        mockMvc.perform(put("/api/tags/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"java\",\"describe\":\"d3\",\"icon\":\"i3\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("java"))
                .andExpect(jsonPath("$.describe").value("d3"))
                .andExpect(jsonPath("$.icon").value("i3"));
    }
}
