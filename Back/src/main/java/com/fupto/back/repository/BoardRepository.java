package com.fupto.back.repository;


import com.fupto.back.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
<<<<<<< HEAD

    List<Board> findAllByOrderByModifiedAtDesc();

    void deleteById(Long id);
=======
    @Query("select b from Board b where b.regMember.id = :id")
    List<Board> findByRegMemberId(Long id);
>>>>>>> 63ddb87f6ed60decd1963344edbc09225c17c3b1
}

