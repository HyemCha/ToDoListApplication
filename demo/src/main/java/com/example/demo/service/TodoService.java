package com.example.demo.service;

import com.example.demo.model.TodoEntity;
import com.example.demo.persistence.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TodoService {

    @Autowired
    private TodoRepository repository;

    public String testService() {
        //TodoEntity 생성
        TodoEntity entity = TodoEntity.builder().title("My first todo item").build();
        //TodoEntity 저장
        repository.save(entity);
        //TodoEntity 검색
        TodoEntity savedEntity = repository.findById(entity.getId()).get();
        return savedEntity.getTitle();
    }

    public List<TodoEntity> create(final TodoEntity entity){
        //Validations
//        if (entity == null) {
//            log.warn("Unknown user.");
//            throw new RuntimeException("Unknown user.");
//        }
        validate(entity);

        repository.save(entity);

        log.info("Entity Id :  {} is saved.", entity.getId());

        return repository.findByUserId(entity.getUserId());
    }

    //리팩토링 메서드
    private void validate(final TodoEntity entity) {
        if (entity == null) {
            log.warn("Entity cannot be null");
            throw new RuntimeException("Entity cannot be null.");
        }
        if (entity.getUserId() == null) {
            log.warn("Unknown user.");
            throw new RuntimeException("Unknown user.");
        }
    }

    //검색
    public List<TodoEntity> retrieve(final String userId) {
        // userId로 Todo리스트 반환
        return repository.findByUserId(userId);
    }

    //수정
    public List<TodoEntity> update(final TodoEntity entity) {
        // 저장할 엔티티가 유효한지 확인.
        validate(entity);
        // 넘겨받은 엔티티 id를 이용해 TodoEntity를 가져옴. 존재하지 않는 엔티티는 업데이트를 할 수 없기 때문
        final Optional<TodoEntity> original = repository.findById(entity.getId());

        original.ifPresent(todo -> {
            // 반환된 TodoEntity가 존재하면 값을 새 entity 갑으로 덮어씌움
            todo.setTitle(entity.getTitle());
            todo.setDone(entity.isDone());
            // 데이터베이스에 새 값을 저장한다.
            repository.save(todo);
        });
        // 검색 메서드를 이용해 사용자의 모든 To do 리스트를 리턴
        return retrieve(entity.getUserId());
    }

    // 삭제
    public List<TodoEntity> delete(final TodoEntity entity) {
        // 저장할 엔티티가 유효한지 확인.
        validate(entity);

        try {
            // 엔티티를 삭제
            repository.delete(entity);
        } catch (Exception e) {
            // exception 발생 시 id와 exception을 로깅
            log.error("error deleting entity ", entity.getId(), e);
            // 컨트롤러로 exception을 보냄. 데이터베이스 내부 로직을 캡슐화하려면 e를 리턴하지 않고 새 exception오브젝트를 리턴
            throw new RuntimeException("error deleting entity " + entity.getId());
        }

        // 새 Todo 리스트를 가져와 리턴
        return retrieve(entity.getUserId());
    }
}