package com.ssafy.bangrang.global.fcm.repository;

import com.ssafy.bangrang.domain.member.entity.AppMember;
import com.ssafy.bangrang.global.fcm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface AlarmRepository  extends JpaRepository<Alarm, Long> {
    @Query("SELECT u FROM Alarm u WHERE u.appMember = :appMemberidx")
    List<Alarm> findByAppMember(Long appMemberidx);

    @Query("SELECT u FROM Alarm u WHERE u.idx = :idx and u.appMember = :appMemberidx")
    Optional<Alarm> findByIdxAndAppMember(Long idx, Long appMemberidx);

    Optional<Alarm> findByIdx(Long idx);
}
