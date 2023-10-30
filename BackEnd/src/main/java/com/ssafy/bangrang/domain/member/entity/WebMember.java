package com.ssafy.bangrang.domain.member.entity;

import com.ssafy.bangrang.domain.event.entity.Event;
import com.ssafy.bangrang.domain.inquiry.entity.Comment;
import com.ssafy.bangrang.domain.inquiry.entity.Inquiry;
import com.ssafy.bangrang.domain.member.model.vo.SocialProvider;
import com.ssafy.bangrang.domain.member.model.vo.WebMemberStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("web")
public class WebMember extends Member{

    @Column(name = "web_member_organization_name")
    private String organizationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "web_member_status")
    private WebMemberStatus webMemberStatus;

    @Column(name = "web_member_auth_file")
    private String authFile;

    @OneToMany(mappedBy = "webMember")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "webMember")
    private List<Event> events = new ArrayList<>();

    @Builder
    public WebMember(Long idx, String id, String password, String organizationName, String authFile, WebMemberStatus webMemberStatus){
        this.idx = idx;
        this.id = id;
        this.password = password;
        this.organizationName = organizationName;
        this.webMemberStatus = webMemberStatus;
        this.authFile = authFile;
    }

    public void changeWebMemberStatus(WebMemberStatus webMemberStatus){
        this.webMemberStatus = webMemberStatus;
    }

    @Builder
    public WebMember(Long idx, String id, String password, String organizationName, WebMemberStatus webMemberStatus, String authFile) {
        this.idx = idx;
        this.id = id;
        this.password = password;
        this.organizationName = organizationName;
        this.webMemberStatus = webMemberStatus;
        this.authFile = authFile;
    }

    /**
     * 비밀번호 암호화
     */
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
