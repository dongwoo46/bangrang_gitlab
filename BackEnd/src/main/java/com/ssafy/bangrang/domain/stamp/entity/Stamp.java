package com.ssafy.bangrang.domain.stamp.entity;

import com.ssafy.bangrang.domain.event.entity.Event;
import com.ssafy.bangrang.global.common.entity.CommonEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "stamp")
public class Stamp{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "stamp_idx")
    private Long idx;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_idx")
    private Event event;

    @JoinColumn(name = "stamp_name")
    private String name;

    @Builder
    public Stamp(Event event, String name){
        this.event = event;
        this.name = name;
    }
    public void EventDeleteStamp() {
        this.event=null;
    }
}
