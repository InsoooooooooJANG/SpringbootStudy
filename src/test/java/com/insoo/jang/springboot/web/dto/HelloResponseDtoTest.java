package com.insoo.jang.springboot.web.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class HelloResponseDtoTest {

    @Test
    public void 롬복_기능_테스트(){
        //given
        String name="test";
        int amount=1000;

        // 롬복으로 자동으로 생성자 만들어준 걸로 객체 생성
        HelloResponseDto dto = new HelloResponseDto(name,amount);

        //저 객체 만들때 set한 값이 지금 name, amount 하고 같은지 체크 (lombok이 getter, setter 잘 만드는지 확인함)
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getAmount()).isEqualTo(amount);
    }
}
