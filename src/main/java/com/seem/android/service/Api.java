package com.seem.android.service;

import com.seem.android.MyApplication;
import com.seem.android.exceptions.EmailAlreadyExistsException;
import com.seem.android.exceptions.UsernameAlreadyExistsException;
import com.seem.android.model.Item;
import com.seem.android.model.Media;
import com.seem.android.model.Seem;
import com.seem.android.model.UserProfile;
import com.seem.android.util.Iso8601;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igbopie on 17/03/14.
 */
public class Api {

    public static final String S3_ENPOINT="http://seem-dev-test.s3-website-us-west-2.amazonaws.com/";
    public static final String ENDPOINT = "https://seem-test.herokuapp.com/";
    public static final String ENDPOINT_GET_SEEMS = "api/seem";
    public static final String ENDPOINT_GET_MEDIA_LARGE = "api/media/get/large/";
    public static final String ENDPOINT_GET_MEDIA_THUMB = "api/media/get/thumb/";
    public static final String ENDPOINT_CREATE_MEDIA = "api/media/create";
    public static final String ENDPOINT_CREATE_SEEM = "api/seem/create";
    public static final String ENDPOINT_LOGIN = "api/user/login";
    public static final String ENDPOINT_CREATE = "api/user/create";
    public static final String ENDPOINT_USER_PROFILE = "api/user/profile";
    public static final String ENDPOINT_USER_UPDATE = "api/user/update";
    public static final String ENDPOINT_FOLLOW = "api/follow";
    public static final String ENDPOINT_UNFOLLOW = "api/unfollow";
    public static final String ENDPOINT_FEED = "api/feed";
    public static final String ENDPOINT_ADD_GCM_TOKEN = "api/user/addgcmtoken";

    public static final String ENDPOINT_SEEM_ADD = "api/seem/add";
    public static final String ENDPOINT_SEEM_GET_ITEMS = "api/seem/items";
    public static final String ENDPOINT_SEEM_BY_UPDATED = "api/seem/by/updated";
    public static final String ENDPOINT_SEEM_BY_ABOUT_TO_START = "api/seem/by/abouttostart";
    public static final String ENDPOINT_SEEM_BY_ABOUT_TO_END = "api/seem/by/abouttoend";
    public static final String ENDPOINT_SEEM_BY_ENDED = "api/seem/by/ended";




    public static final int RESPONSE_CODE_OK = 200;
    public static final int RESPONSE_CODE_OK_CREATED = 201;
    public static final int RESPONSE_CODE_CLIENT_LOGIN_TIMEOUT = 440;
    public static final int RESPONSE_CODE_CLIENT_USERNAME_ALREADY_EXISTS = 466;
    public static final int RESPONSE_CODE_CLIENT_EMAIL_ALREADY_EXISTS = 467;

    public static final String JSON_TAG_CODE = "code";
    public static final String JSON_TAG_MESSAGE = "message";
    public static final String JSON_TAG_RESPONSE = "response";

    //SEEM Model
    public static final String JSON_TAG_SEEM_TITLE = "title";
    public static final String JSON_TAG_SEEM_ID = "_id";
    public static final String JSON_TAG_SEEM_ITEM_COUNT = "itemCount";
    public static final String JSON_TAG_SEEM_CREATED = "created";
    public static final String JSON_TAG_SEEM_UPDATED = "updated";
    public static final String JSON_TAG_SEEM_LASTEST_ITEMS = "latestItems";
    public static final String JSON_TAG_SEEM_END_DATE = "endDate";
    public static final String JSON_TAG_SEEM_START_DATE = "startDate";
    public static final String JSON_TAG_SEEM_COVER_PHOTO_MEDIA_ID = "coverPhotoMediaId";
    public static final String JSON_TAG_SEEM_PUBLISH_PERMISSIONS= "publishPermissions";

    //ITEM Model

    public static final String JSON_TAG_ITEM_ID = "_id";
    public static final String JSON_TAG_ITEM_CAPTION = "caption";
    public static final String JSON_TAG_ITEM_MEDIA_ID = "mediaId";
    public static final String JSON_TAG_ITEM_CREATED = "created";
    public static final String JSON_TAG_ITEM_SEEM_ID = "seemId";
    public static final String JSON_TAG_ITEM_USER_ID = "userId";
    public static final String JSON_TAG_ITEM_USER = "user";


    //USERPROFILE

    public static final String JSON_TAG_USER_PROFILE_ID = "_id";
    public static final String JSON_TAG_USER_PROFILE_USERNAME = "username";
    public static final String JSON_TAG_USER_PROFILE_PUBLISHED = "published";
    public static final String JSON_TAG_USER_PROFILE_FAVOURITES = "favourites";
    public static final String JSON_TAG_USER_PROFILE_FOLLOWERS = "followers";
    public static final String JSON_TAG_USER_PROFILE_FOLLOWING= "following";
    public static final String JSON_TAG_USER_PROFILE_MEDIA_ID= "mediaId";
    public static final String JSON_TAG_USER_PROFILE_NAME= "name";
    public static final String JSON_TAG_USER_PROFILE_BIO= "bio";
    public static final String JSON_TAG_USER_PROFILE_EMAIL= "email";
    public static final String JSON_TAG_USER_PROFILE_IS_FOLLOWED_BY_ME= "isFollowedByMe";
    public static final String JSON_TAG_USER_PROFILE_IS_FOLLOWING_ME= "isFollowingMe";


    public enum ImageFormat{THUMB,LARGE};


    public static List<Seem> getSeemsByUpdated(){
        return getSeemBySomething(ENDPOINT+ ENDPOINT_SEEM_BY_UPDATED);
    }
    public static List<Seem> getSeemsByAboutToStart(){
        return getSeemBySomething(ENDPOINT+ ENDPOINT_SEEM_BY_ABOUT_TO_START);
    }

    public static List<Seem> getSeemsByAboutToEnd(){
        return getSeemBySomething(ENDPOINT+ ENDPOINT_SEEM_BY_ABOUT_TO_END);
    }
    public static List<Seem> getSeemsByEnded(){
        return getSeemBySomething(ENDPOINT+ ENDPOINT_SEEM_BY_ENDED);
    }


    private static List<Seem> getSeemBySomething(String endpoint){
        try {
            HttpResponse httpResponse = makeRequest(endpoint,new HashMap<String, String>());
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if(responseCode == RESPONSE_CODE_OK){
                //Utils.debug(Api.class,"Va bien! Status Line:" + httpResponse.getStatusLine().getStatusCode());

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(os);
                String output = os.toString( "UTF-8" );
                //Utils.debug(Api.class,"Output:"+output);

                JSONObject jsonObj = new JSONObject(output);

                return fillSeems(jsonObj.getJSONArray(JSON_TAG_RESPONSE));

            } else {
                //Utils.debug(Api.class,"API response code is: "+responseCode);
                return null;
            }
        } catch (Exception e) {
            //Utils.debug(Api.class,"API error:",e);
            return null;
        }
    }
    public static List<Seem> getSeems(){
        try {
            HttpResponse httpResponse = makeRequest(ENDPOINT+ENDPOINT_GET_SEEMS,new HashMap<String, String>());
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if(responseCode == RESPONSE_CODE_OK){
                //Utils.debug(Api.class,"Va bien! Status Line:" + httpResponse.getStatusLine().getStatusCode());

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(os);
                String output = os.toString( "UTF-8" );
                //Utils.debug(Api.class,"Output:"+output);

                JSONObject jsonObj = new JSONObject(output);

                return fillSeems(jsonObj.getJSONArray(JSON_TAG_RESPONSE));

            } else {
                //Utils.debug(Api.class,"API response code is: "+responseCode);
                return null;
            }
        } catch (Exception e) {
            //Utils.debug(Api.class,"API error:",e);
            return null;
        }
    }



    public static boolean addGcmToken(String gcmToken,String token){
        try {
            HashMap<String,String>params = new HashMap<String, String>();
            params.put("gcmtoken",gcmToken);
            params.put("token",token);
            HttpResponse httpResponse = makeRequest(ENDPOINT+ENDPOINT_ADD_GCM_TOKEN,params);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if(responseCode == RESPONSE_CODE_OK){
                //Utils.debug(Api.class,"Va bien! Status Line:" + httpResponse.getStatusLine().getStatusCode());
                return true;

            } else {
                //Utils.debug(Api.class,"API response code is: "+responseCode);
                return false;
            }
        } catch (Exception e) {
            //Utils.debug(Api.class,"API error:",e);
            return false;
        }
    }
    public static boolean follow(String username,String token){
        try {
            HashMap<String,String>params = new HashMap<String, String>();
            params.put("token",token);
            params.put("username",username);
            //Utils.debug(Api.class,"Username:"+username);
            HttpResponse httpResponse = makeRequest(ENDPOINT+ENDPOINT_FOLLOW,params);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if(responseCode == RESPONSE_CODE_OK){
                //Utils.debug(Api.class,"Va bien! Status Line:" + httpResponse.getStatusLine().getStatusCode());
                return true;

            } else {
                //Utils.debug(Api.class,"API response code is: "+responseCode);
                return false;
            }
        } catch (Exception e) {
            //Utils.debug(Api.class,"API error:",e);
            return false;
        }
    }

    public static boolean unfollow(String username,String token){
        try {
            HashMap<String,String>params = new HashMap<String, String>();
            params.put("token",token);
            params.put("username",username);
            //Utils.debug(Api.class,"Username:"+username);
            HttpResponse httpResponse = makeRequest(ENDPOINT+ENDPOINT_UNFOLLOW,params);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if(responseCode == RESPONSE_CODE_OK){
                //Utils.debug(Api.class,"Va bien! Status Line:" + httpResponse.getStatusLine().getStatusCode());
                return true;

            } else {
                //Utils.debug(Api.class,"API response code is: "+responseCode);
                return false;
            }
        } catch (Exception e) {
            //Utils.debug(Api.class,"API error:",e);
            return false ;
        }
    }



    public static boolean updateUser(String username,String email,String name,String bio,String mediaId,String token){
        try {
            HashMap<String,String>params = new HashMap<String, String>();
            if(username != null) {
                params.put("username", username);
            }
            if(email != null) {
                params.put("email", email);
            }
            if(name != null) {
                params.put("name", name);
            }
            if(bio != null) {
                params.put("bio", bio);
            }
            if(mediaId != null) {
                params.put("mediaId", mediaId);
            }
            params.put("token", token);
            HttpResponse httpResponse = makeRequest(ENDPOINT+ENDPOINT_USER_UPDATE,params);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if(responseCode == RESPONSE_CODE_OK) {
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            //Utils.debug(Api.class,"API error:",e);
            return false;
        }
    }
    public static UserProfile getUserProfile(String username,String token){
        try {
            HashMap<String,String>params = new HashMap<String, String>();
            params.put("username",username);
            if(token != null){
                params.put("token",token);
            }
            //Utils.debug(Api.class,"Username:"+username);
            HttpResponse httpResponse = makeRequest(ENDPOINT+ENDPOINT_USER_PROFILE,params);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if(responseCode == RESPONSE_CODE_OK){
                //Utils.debug(Api.class,"Va bien! Status Line:" + httpResponse.getStatusLine().getStatusCode());

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(os);
                String output = os.toString( "UTF-8" );
                //Utils.debug(Api.class,"Output:"+output);


                JSONObject jsonObj = new JSONObject(output);
                JSONObject itemJson = jsonObj.getJSONObject(JSON_TAG_RESPONSE);
                UserProfile userProfile = fillUserProfile(itemJson);
                //Utils.debug(Api.class,"Userprofile fetched: "+userProfile);
                return userProfile;

            } else {
                //Utils.debug(Api.class,"API response code is: "+responseCode);
                return null;
            }
        } catch (Exception e) {
            //Utils.debug(Api.class,"API error:",e);
            return null;
        }
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException
    {
        byte[] buffer = new byte[1024]; // Adjust if you want
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1)
        {
            output.write(buffer, 0, bytesRead);
        }
    }

    public static String createMedia(InputStream streamPhoto){
        try {
            //Utils.shrinkBitmapFromStream(stream);
            //ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            //copyStream(streamPhoto,outStream);
            //image.compress(Bitmap.CompressFormat.JPEG, 50, streamPhoto);
            String fileName = String.format("file_%d.png", new Date().getTime());

            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("file", new InputStreamBody(streamPhoto,"image/jpeg", fileName));

            HttpPost httpPost = new HttpPost(ENDPOINT+ENDPOINT_CREATE_MEDIA);
            httpPost.setEntity(reqEntity);

            int timeoutConnection = 60000;
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            int timeoutSocket = 60000;
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpConnectionParams.setTcpNoDelay(httpParameters, true);


            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(httpPost);

            if(response.getStatusLine().getStatusCode() != RESPONSE_CODE_OK){
                return null;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();
            while ((sResponse = reader.readLine()) != null) {
                s = s.append(sResponse);
            }
            System.out.println("Response: " + s);

            JSONObject json = new JSONObject(s.toString());

            return json.getString(JSON_TAG_RESPONSE);
        } catch (Exception e){
            //Utils.debug(Api.class,"API error:",e);
            return null;
        }
    }



    public static Seem createSeem(String title,Date expire){
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("title", title);
            params.put("expire", Iso8601.fromDate(expire));
            params.put("token", MyApplication.getToken());

            HttpResponse httpResponse = makeRequest(ENDPOINT+ENDPOINT_CREATE_SEEM,params);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if(responseCode == RESPONSE_CODE_OK){
                //Utils.debug(Api.class,"Va bien! Status Line:" + httpResponse.getStatusLine().getStatusCode());

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(os);
                String output = os.toString( "UTF-8" );
                //Utils.debug(Api.class,"Output:"+output);

                Seem seem = fillSeem(new JSONObject(output).getJSONObject(JSON_TAG_RESPONSE));

                return seem;
            }else if(responseCode == RESPONSE_CODE_CLIENT_LOGIN_TIMEOUT ) {
                String token = login(MyApplication.getUsername(),MyApplication.getPassword());
                if(token != null){
                    MyApplication.login(MyApplication.getUsername(),MyApplication.getPassword(),token);
                    return createSeem(title,expire);
                }
            }
            return null;
        } catch (Exception e) {
            //Utils.debug(Api.class,"API error:",e);
            return null;
        }
    }
    public static Item addToSeem(String caption,String mediaId,String seemId){
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("seemId", seemId);
            params.put("mediaId", mediaId);
            params.put("caption", caption);
            params.put("token", MyApplication.getToken());

            HttpResponse httpResponse = makeRequest(ENDPOINT+ ENDPOINT_SEEM_ADD,params);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if(responseCode == RESPONSE_CODE_OK){
                //Utils.debug(Api.class,"Va bien! Status Line:" + httpResponse.getStatusLine().getStatusCode());

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(os);
                String output = os.toString( "UTF-8" );
                //Utils.debug(Api.class,"Output:"+output);

                Item item = fillItem(new JSONObject(output).getJSONObject(JSON_TAG_RESPONSE));

                return item;
            } else if(responseCode == RESPONSE_CODE_CLIENT_LOGIN_TIMEOUT ) {
                String token = login(MyApplication.getUsername(),MyApplication.getPassword());
                if(token != null){
                    MyApplication.login(MyApplication.getUsername(),MyApplication.getPassword(),token);
                    return addToSeem(caption, mediaId, seemId);
                }
            }
            return null;
        } catch (Exception e) {
            //Utils.debug(Api.class,"API error:",e);
            return null;
        }
    }
    public static List<Item> getSeemItems(String seemId,int page,String token){
        try {
            HashMap<String,String>params = new HashMap<String, String>();
            params.put("seemId",seemId);
            params.put("page",page+"");
            if(token != null){
                params.put("token",token+"");
            }

            HttpResponse httpResponse = makeRequest(ENDPOINT+ ENDPOINT_SEEM_GET_ITEMS,params);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if(responseCode == RESPONSE_CODE_OK){
                //Utils.debug(Api.class,"Va bien! Status Line:" + httpResponse.getStatusLine().getStatusCode());

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(os);
                String output = os.toString( "UTF-8" );
                //Utils.debug(Api.class,"Output:"+output);


                JSONObject jsonObj = new JSONObject(output);
                JSONArray itemJson = jsonObj.getJSONArray(JSON_TAG_RESPONSE);
                List<Item> items = fillItems(itemJson);
                //Utils.debug(Api.class,"Items fetched: "+items);

                return items;

            } else {
                //Utils.debug(Api.class,"API response code is: "+responseCode);
                return null;
            }
        } catch (Exception e) {
            //Utils.debug(Api.class,"API error:",e);
            return null;
        }
    }



    public static String login(String username, String password) throws Exception {
        HashMap<String,String>params = new HashMap<String, String>();
        params.put("username",username);
        params.put("password",password);

        HttpResponse httpResponse = makeRequest(ENDPOINT+ENDPOINT_LOGIN,params);
        int responseCode = httpResponse.getStatusLine().getStatusCode();
        if(responseCode == RESPONSE_CODE_OK){
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            httpResponse.getEntity().writeTo(os);
            String output = os.toString( "UTF-8" );
            //Utils.debug(Api.class,"Output:"+output);
            JSONObject jsonObj = new JSONObject(output);
            String token = jsonObj.getString(JSON_TAG_RESPONSE);
            return token;

        }
        return null;
    }

    public static String signUp(String username, String password,String email) throws Exception {
        HashMap<String,String>params = new HashMap<String, String>();
        params.put("username",username);
        params.put("password",password);
        params.put("email",email);

        HttpResponse httpResponse = makeRequest(ENDPOINT+ENDPOINT_CREATE,params);
        int responseCode = httpResponse.getStatusLine().getStatusCode();
        if(responseCode == RESPONSE_CODE_OK_CREATED){
            return login(username,password);
        } else if(responseCode == RESPONSE_CODE_CLIENT_USERNAME_ALREADY_EXISTS) {
            throw new UsernameAlreadyExistsException();
        } else if(responseCode == RESPONSE_CODE_CLIENT_EMAIL_ALREADY_EXISTS) {
            throw new EmailAlreadyExistsException();

        }else {
            //Utils.debug(Api.class,"Api response code:"+responseCode);
        }
        return null;
    }


    private static List<Seem> fillSeems(JSONArray seemsArray) throws JSONException, ParseException {
        List<Seem> seemList = new ArrayList<Seem>();
        for (int i = 0; i < seemsArray.length(); i++) {
            JSONObject seemJson = seemsArray.getJSONObject(i);
            seemList.add(fillSeem(seemJson));
        }
        return seemList;
    }

    private static Seem fillSeem(JSONObject seemJson) throws JSONException, ParseException {
        Seem seem = new Seem();
        seem.setId(seemJson.getString(JSON_TAG_SEEM_ID));
        seem.setTitle(seemJson.getString(JSON_TAG_SEEM_TITLE));
        seem.setCreated(Iso8601.toCalendar(seemJson.getString(JSON_TAG_SEEM_CREATED)).getTime());
        seem.setItemCount(seemJson.getInt(JSON_TAG_SEEM_ITEM_COUNT));
        if(seemJson.has(JSON_TAG_SEEM_END_DATE)) {
            seem.setEndDate(Iso8601.toCalendar(seemJson.getString(JSON_TAG_SEEM_END_DATE)).getTime());
        }
        if(seemJson.has(JSON_TAG_SEEM_START_DATE)) {
            seem.setStartDate(Iso8601.toCalendar(seemJson.getString(JSON_TAG_SEEM_START_DATE)).getTime());
        }
        if(seemJson.has(JSON_TAG_SEEM_COVER_PHOTO_MEDIA_ID)){
           seem.setCoverPhotoMediaId(seemJson.getString(JSON_TAG_SEEM_COVER_PHOTO_MEDIA_ID));
        }
        if(seemJson.has(JSON_TAG_SEEM_PUBLISH_PERMISSIONS)){
            seem.setPublishPermissions(seemJson.getString(JSON_TAG_SEEM_PUBLISH_PERMISSIONS));
        }

        seem.setUpdated(Iso8601.toCalendar(seemJson.getString(JSON_TAG_SEEM_UPDATED)).getTime());

        if(seemJson.has(JSON_TAG_SEEM_LASTEST_ITEMS)){
            JSONArray array = seemJson.getJSONArray(JSON_TAG_SEEM_LASTEST_ITEMS);
            seem.setLastestItems(fillItems(array));
        }

        return seem;
    }


    private static String getStringJsonField(JSONObject jsonObject, String property) throws JSONException {
        if(jsonObject.has(property)){
            return jsonObject.getString(property);
        }
        return null;

    }


    private static List<Item> fillItems(JSONArray itemArray) throws JSONException, ParseException {
        List<Item> itemList = new ArrayList<Item>();
        for (int i = 0; i < itemArray.length(); i++) {
            JSONObject itemJSON = itemArray.getJSONObject(i);
            itemList.add(fillItem(itemJSON));
        }
        return itemList;
    }
    private static UserProfile fillUserProfile(JSONObject itemJson) throws JSONException, ParseException {
        UserProfile user = new UserProfile();
        user.setId(itemJson.getString(JSON_TAG_USER_PROFILE_ID));
        user.setUsername(itemJson.getString(JSON_TAG_USER_PROFILE_USERNAME));
        if(itemJson.has(JSON_TAG_USER_PROFILE_FOLLOWERS)){
            user.setFollowers(itemJson.getInt(JSON_TAG_USER_PROFILE_FOLLOWERS));
        }
        if(itemJson.has(JSON_TAG_USER_PROFILE_FOLLOWING)) {
            user.setFollowing(itemJson.getInt(JSON_TAG_USER_PROFILE_FOLLOWING));
        }
        if(itemJson.has(JSON_TAG_USER_PROFILE_MEDIA_ID)) {
            user.setId(itemJson.getString(JSON_TAG_USER_PROFILE_MEDIA_ID));
        }
        if(itemJson.has(JSON_TAG_USER_PROFILE_IS_FOLLOWED_BY_ME)){
            user.setIsFollowedByMe(itemJson.getBoolean(JSON_TAG_USER_PROFILE_IS_FOLLOWED_BY_ME));
        }
        if(itemJson.has(JSON_TAG_USER_PROFILE_IS_FOLLOWING_ME)){
            user.setIsFollowingMe(itemJson.getBoolean(JSON_TAG_USER_PROFILE_IS_FOLLOWING_ME));
        }
        if(itemJson.has(JSON_TAG_USER_PROFILE_FAVOURITES)){
            user.setFavourites(itemJson.getInt(JSON_TAG_USER_PROFILE_FAVOURITES));
        }
        if(itemJson.has(JSON_TAG_USER_PROFILE_PUBLISHED)){
            user.setPublished(itemJson.getInt(JSON_TAG_USER_PROFILE_PUBLISHED));
        }

        user.setEmail(getStringJsonField(itemJson,JSON_TAG_USER_PROFILE_EMAIL));
        user.setName(getStringJsonField(itemJson, JSON_TAG_USER_PROFILE_NAME));
        user.setBio(getStringJsonField(itemJson, JSON_TAG_USER_PROFILE_BIO));
        user.setMediaId(getStringJsonField(itemJson, JSON_TAG_USER_PROFILE_MEDIA_ID));

        return user;
    }

    private static Item fillItem(JSONObject itemJson) throws JSONException, ParseException {
        Item item = new Item();
        item.setId(itemJson.getString(JSON_TAG_ITEM_ID));
        item.setCaption(itemJson.getString(JSON_TAG_ITEM_CAPTION));
        item.setMediaId(itemJson.getString(JSON_TAG_ITEM_MEDIA_ID));
        item.setCreated(Iso8601.toCalendar(itemJson.getString(JSON_TAG_ITEM_CREATED)).getTime());
        if(itemJson.has(JSON_TAG_ITEM_SEEM_ID)) {
            item.setSeemId(itemJson.getString(JSON_TAG_ITEM_SEEM_ID));
        }
        if(itemJson.has(JSON_TAG_ITEM_USER_ID)) {
            item.setUserId(itemJson.getString(JSON_TAG_ITEM_USER_ID));
        }

        if(itemJson.has(JSON_TAG_ITEM_USER) && !(itemJson.getString(JSON_TAG_ITEM_USER).equals("null"))){
            item.setUserProfile(fillUserProfile(itemJson.getJSONObject(JSON_TAG_ITEM_USER)));
        }

        return item;
    }



    public static InputStream downloadLargeImage(Media media) throws IOException {
        return  (InputStream) new URL(getImageEndpoint(media.getId(),ImageFormat.LARGE)).getContent();
       //return (InputStream) new URL(ENDPOINT+ENDPOINT_GET_MEDIA_LARGE+media.getId()).getContent();
        //media.setImageLarge(Drawable.createFromStream(is, media.getId()));
    }

    public static InputStream downloadThumbImage(Media media) throws IOException {
        return  (InputStream) new URL(getImageEndpoint(media.getId(),ImageFormat.THUMB)).getContent();
        //return (InputStream) new URL(ENDPOINT+ENDPOINT_GET_MEDIA_THUMB+media.getId()).getContent();
    }

    public static String getImageEndpoint(String mediaId,ImageFormat format){
        switch (format){
            case LARGE:
                return S3_ENPOINT+mediaId+"_"+"large";
            case THUMB:
            default:
                return S3_ENPOINT+mediaId+"_"+"thumb";
        }

    }


    public static HttpResponse makeRequest(String path, Map<String,String> params) throws Exception
    {

        //url with the post data
        HttpPost httpPost = new HttpPost(path);

        //convert parameters into JSON object

        StringBuffer content = new StringBuffer();
        for(Map.Entry<String,String> entry:params.entrySet()){
            content.append(entry.getKey());
            content.append("=");
            content.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            content.append("&");
        }
        //remove last &
        if(content.length() > 0 ) {
            content.setLength(content.length() - 1);
        }

        //passes the results to a string builder/entity
        StringEntity se = new StringEntity(content.toString());

        //sets the post request as the resulting string
        httpPost.setEntity(se);
        //sets a request header so the page receving the request
        //will know what to do with it
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");

        //Handles what is returned from the page
        //ResponseHandler responseHandler = new BasicResponseHandler();

        //instantiates httpclient to make request
        DefaultHttpClient httpClient = new DefaultHttpClient();
        return httpClient.execute(httpPost);
    }
}
