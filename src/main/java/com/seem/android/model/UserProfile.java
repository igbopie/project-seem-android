package com.seem.android.model;

/**
 * Created by igbopie on 09/04/14.
 */
public class UserProfile {
    private String id;
    private String username;
    private String mediaId;
    private String name;
    private String bio;
    private String email;

    private int followers;
    private int following;
    private int published;
    private int favourites;

    private Boolean isFollowingMe;
    private Boolean isFollowedByMe;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public Boolean getIsFollowingMe() {
        return isFollowingMe;
    }

    public void setIsFollowingMe(Boolean isFollowingMe) {
        this.isFollowingMe = isFollowingMe;
    }

    public Boolean getIsFollowedByMe() {
        return isFollowedByMe;
    }

    public void setIsFollowedByMe(Boolean isFollowedByMe) {
        this.isFollowedByMe = isFollowedByMe;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPublished() {
        return published;
    }

    public void setPublished(int published) {
        this.published = published;
    }

    public int getFavourites() {
        return favourites;
    }

    public void setFavourites(int favourites) {
        this.favourites = favourites;
    }


    @Override
    public String toString() {
        return "UserProfile{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", mediaId='" + mediaId + '\'' +
                ", name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", email='" + email + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", published=" + published +
                ", favourites=" + favourites +
                ", isFollowingMe=" + isFollowingMe +
                ", isFollowedByMe=" + isFollowedByMe +
                '}';
    }
}
