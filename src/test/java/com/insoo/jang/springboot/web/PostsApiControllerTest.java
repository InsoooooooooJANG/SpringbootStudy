package com.insoo.jang.springboot.web;

import com.insoo.jang.springboot.domain.posts.Posts;
import com.insoo.jang.springboot.domain.posts.PostsRepository;
import com.insoo.jang.springboot.web.dto.PostsSaveRequestDto;
import com.insoo.jang.springboot.web.dto.PostsUpdateRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @AfterEach
    public void tearDown() throws Exception{
        postsRepository.deleteAll();
    }

    @Test
    public void Posts_등록된다() throws Exception{
        //given
        String title = "title";
        String content = "content";

        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                                                            .title(title)
                                                            .content(content)
                                                            .author("oasisholics@gmail.com")
                                                            .build();

        String url  = "http://localhost:" + port + "api/v1/posts";

        //when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }

    @Test
    public void Posts_수정된다() throws  Exception{
        //given
        Posts savedPosts = postsRepository.save(Posts.builder()
                                            .title("title")
                                            .content("content")
                                            .author("author")
                                            .build()); // db에 posts 저장
        Long updateId = savedPosts.getId(); // 저장 한 뒤 해당 row의 id 가져오기
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                                                                .title(expectedTitle)
                                                                .content(expectedContent)
                                                                .build(); // 업데이트 requestDto 생성

        String url = "http://localhost:" + port + "/api/v1/posts/" + updateId; // 요청 url

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Long.class); // 요청

        //then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK); //상태 체크
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll(); // db 에 저장된 거 다 가져옴
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle); // 1번째 열의 제목, 콘텐츠가 요청한 내용대로 변경되었는지 (update 잘 되었는지) 확인
        assertThat(all.get(0).getContent()).isEqualTo(expectedContent);
    }
}
