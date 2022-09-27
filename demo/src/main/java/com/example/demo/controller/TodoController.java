package com.example.demo.controller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.TodoDTO;
import com.example.demo.model.TodoEntity;
import com.example.demo.service.TodoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("todo")
public class TodoController {

    @Autowired
    private TodoService service;

    @GetMapping("/test")
    public ResponseEntity<?> testTodo() {
        String str = service.testService(); //테스트 서비스 사용
        List<String> list = new ArrayList<>();
        list.add(str);
        ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> createTodo(
            @AuthenticationPrincipal String userId,
            @RequestBody TodoDTO dto) {
        try{
            // String temporaryUserId = "temporary-user";//temporary user id.
            //(1) TodoEntity로 변환
            TodoEntity entity = TodoDTO.toEntity(dto);
            //(2) id를 null로 초기화. 생성 당시에는 id가 없어야 하기 때문
            entity.setId(null);
            //(3) 임시 사용자 아이디를 설정. 인증과 인가 기능 없이 한 사용자(temporary-user)만 로그인 없이 사용할 수 있게
            entity.setUserId(userId);
            //(4) 서비스를 이용해 Todo 엔티티를 생성
            List<TodoEntity> entities = service.create(entity);
            //(5) 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
            //(6) 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
            //(7) ResponseDTO를 리턴
            // return ResponseEntity.ok().body(response);
            return ResponseEntity.ok(response);
        }catch (Exception e){
            //(8) 혹시 예외가 있는 경우 dto 대신 error에 메시지를 넣어 리턴
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }


    }

    //검색
    @GetMapping
    public ResponseEntity<?> retrieveTodoList(
            @AuthenticationPrincipal String userId
    ) {
        // String temporaryUserId = "temporary-user";
        // service의 retrieve메서드로 Todo리스트 가져오기
        List<TodoEntity> entities = service.retrieve(userId);
        // 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO리스트로 변환
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
        // 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
        // ResponseDTO를 리턴
        // return ResponseEntity.ok().body(response);
        return ResponseEntity.ok(response);
    }

    // 수정
    @PutMapping
    public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId,
            @RequestBody TodoDTO dto) {
        // String temporaryUserId = "temporary-user";
        // dto를 entity로 변환
        TodoEntity entity = TodoDTO.toEntity(dto);
        // id를 temporaryUserId로 초기화.
        entity.setUserId(userId);
        // 서비스를 이용해 entity를 업데이트
        List<TodoEntity> entities = service.update(entity);
        // 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
        // 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
        // RessponseDTO를 리턴
        // return ResponseEntity.ok().body(response);
        return ResponseEntity.ok(response);
    }

    // 삭제
    @DeleteMapping
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId,
            @RequestBody TodoDTO dto) {
        try {
            // String temporaryUserId = "temporary-user";
            // TodoEntity로 변환
            TodoEntity entity = TodoDTO.toEntity(dto);
            // 임시 사용자 아이디를 설정해 준다.
            entity.setUserId(userId);
            // 서비스를 이용해 entity를 삭제
            List<TodoEntity> entities = service.delete(entity);
            // 자바 스트림을 이용해 리턴된 엔티티 리스트를 TodoDTO리스트로 변환
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
            // 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();
            // ResponseDTO를 리턴
            // return ResponseEntity.ok().body(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 혹시 예외가 있는 경우 dto 대신 error에 메시지를 넣어 리턴
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
