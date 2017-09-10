package com.example.android.lagdev;

import java.util.ArrayList;

/**
 * Created by sodiqOladeni on 27/08/2017.
 */
public class Profile extends ArrayList {
    private String mProfileResourceId;
    private String mUserName;
    private String mGitUrl;

    public Profile(String profileResourceId, String userName, String gitUrl) {
        mProfileResourceId = profileResourceId;
        mUserName = userName;
        mGitUrl = gitUrl;
    }

    public String getMProfileResourceId() {
        return mProfileResourceId;
    }

    public String getMUserName() {
        return mUserName;
    }

    public String getMGitUrl() {
        return mGitUrl;
    }
}
