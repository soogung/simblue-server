package com.soogung.simblue.domain.application.domain.repository;

import com.soogung.simblue.domain.application.domain.Application;
import com.soogung.simblue.domain.application.domain.Owner;
import com.soogung.simblue.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    @Query("SELECT o FROM Owner o " +
            "JOIN FETCH o.application " +
            "WHERE o.teacher = :teacher AND o.application.state <> 'DELETED' " +
            "ORDER BY o.id DESC")
    List<Owner> findAllByTeacher(User teacher);

    @Query("SELECT o FROM Owner o " +
            "JOIN FETCH o.teacher " +
            "WHERE o.application = :application AND o.teacher <> :teacher " +
            "ORDER BY o.id DESC")
    List<Owner> findOwnerByApplicationWithoutTeacher(Application application, User teacher);

    boolean existsByApplicationIdAndTeacherId(Long application, Long teacher);

    void deleteByApplication(Application application);
}
