package com.ssafy.bangrang.global.security.oauth.userinfo;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProfileImg() {
        return (String) attributes.get("picture");
    }
}
