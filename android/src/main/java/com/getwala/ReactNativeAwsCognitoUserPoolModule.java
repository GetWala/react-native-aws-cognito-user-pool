
package com.getwala;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidentityprovider.model.RespondToAuthChallengeRequest;
import com.amazonaws.services.cognitoidentityprovider.model.RespondToAuthChallengeResult;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.Iterator;

public class ReactNativeAwsCognitoUserPoolModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private CognitoUserPool cognitoUserPool;
    private CognitoUser lastSignUp;

    public ReactNativeAwsCognitoUserPoolModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "ReactNativeAwsCognitoUserPool";
    }

    @ReactMethod
    public void initializeUserPool(String userPoolId, String clientId, String clientSecret, String region) {
        cognitoUserPool = new CognitoUserPool(reactContext.getApplicationContext(), userPoolId, clientId, clientSecret, getRegion(region));
    }

    @ReactMethod
    public void SignUp(String userId, String password, ReadableMap attributes, final Promise promise){
        lastSignUp = null;
        SignUpHandler handler = new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser user, boolean signUpConfirmationState, CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                lastSignUp = user;
                WritableMap map = Arguments.createMap();
                map.putString("userId", user.getUserId());
                map.putString("userPoolId", user.getUserPoolId());
                promise.resolve(map);
            }

            @Override
            public void onFailure(Exception exception) {
                lastSignUp = null;
                promise.reject(exception);
            }
        };
        cognitoUserPool.signUpInBackground(userId, password, toCognitoUserAttributes(attributes), null, handler);
    }

    @ReactMethod
    public void confirmSignUp(String confirmationCode, final Promise promise){
        GenericHandler handler = new GenericHandler() {
            @Override
            public void onSuccess() {
                promise.resolve(new Object());
            }

            @Override
            public void onFailure(Exception exception) {
                promise.reject(exception);
            }
        };
        if(lastSignUp != null){
            lastSignUp.confirmSignUp(confirmationCode, true, handler);
        }else{
            promise.reject(new Exception("There is no pending sign-up to confirm"));
        }
    }

    @ReactMethod
    public void isAuthenticated(ReadableMap authenticationData, final Promise promise){
        CognitoUser user = getOrCreateUser(authenticationData);
        AuthenticationHandler handler = createAuthenticationHandler(promise);
        user.getSessionInBackground(handler);
    }

    @ReactMethod
    public void authenticate(ReadableMap authenticationData, final Promise promise){
        CognitoUser user = getOrCreateUser(authenticationData);
        AuthenticationHandler handler = createAuthenticationHandler(promise);
        AuthenticationDetails authDetails = new AuthenticationDetails(authenticationData.getString("userId"), authenticationData.getString("password"), null);
        user.initiateUserAuthentication(authDetails, handler, true);
    }

    @ReactMethod
    public void completeAuthenticationChallenge(ReadableMap authenticationData, final Promise promise){
        if(challengeContinuation == null){
            promise.reject(new Exception("There is no pending authentication challenge"));
        }else{
            challengeContinuation.setChallengeResponse(authenticationData.getString("challengeKey"), authenticationData.getString("challengeAnswer"));
            challengeContinuation.continueTask();
            promise.resolve(true);
        }
//        CognitoUser user = getOrCreateUser(authenticationData);
//        RespondToAuthChallengeRequest request = new RespondToAuthChallengeRequest();
//        request.setChallengeName(authenticationData.getString("challengeName"));
//        request.setChallengeResponses(fromReadableMap(authenticationData.getMap("responses")));
//        request.setClientId(cognitoUserPool.getClientId());
//        user.respondToChallenge(request, createAuthenticationHandler(promise), true);
    }

    @ReactMethod
    public void completeAuthenticationDetails(ReadableMap authenticationData, final Promise promise){
        if(authenticationContinuation == null){
            promise.reject(new Exception("There is no pending authentication details activity"));
        }else {
            AuthenticationDetails authDetails = new AuthenticationDetails(authenticationData.getString("userId"), authenticationData.getString("password"), null);
            authenticationContinuation.setAuthenticationDetails(authDetails);
            authenticationContinuation.continueTask();
        }
//        CognitoUser user = getOrCreateUser(authenticationData);
//        AuthenticationHandler handler = createAuthenticationHandler(promise);
//        AuthenticationDetails authDetails = new AuthenticationDetails(authenticationData.getString("userId"), authenticationData.getString("password"), null);
//        user.initiateUserAuthentication(authDetails, handler, true);
    }

    @ReactMethod
    public void completeMfaCode(String mfaCode, final Promise promise){
        if(multiFactorAuthenticationContinuation == null){
            promise.reject(new Exception("There is no pending multi-factor authentication challenge"));
        }else {
            multiFactorAuthenticationContinuation.setMfaCode(mfaCode);
            multiFactorAuthenticationContinuation.continueTask();
            promise.resolve(true);
        }
//        CognitoUser user = getOrCreateUser(authenticationData);
//        AuthenticationHandler handler = createAuthenticationHandler(promise);
//        RespondToAuthChallengeResult result = new RespondToAuthChallengeResult();
//        user.respondToMfaChallenge(authenticationData.getString("mfaCode"), result, handler, true);
    }

    @ReactMethod
    public void signOut(final Promise promise){
        CognitoUser user = cognitoUserPool.getCurrentUser();
        if(user == null){
            promise.reject(new Exception("There is no current user to sign out"));
        }else{
            user.signOut();
            promise.resolve(true);
        }
    }

    private ForgotPasswordContinuation lastForgotPasswordContinuation;

    @ReactMethod
    public void forgotPassword(ReadableMap authenticationData, final Promise promise){
        CognitoUser user = getOrCreateUser(authenticationData);
        ForgotPasswordHandler handler = new ForgotPasswordHandler() {
            @Override
            public void onSuccess() {
                WritableMap map = Arguments.createMap();
                map.putString("activity", "ForgotPasswordComplete");
                promise.resolve(map);
            }

            @Override
            public void getResetCode(ForgotPasswordContinuation continuation) {
                WritableMap map = Arguments.createMap();
                CognitoUserCodeDeliveryDetails details =  continuation.getParameters();
                map.putString("activity", "ForgotPasswordResetCodeRequired");
                map.putString("attributeName", details.getAttributeName());
                map.putString("deliveryMedium", details.getDeliveryMedium());
                map.putString("destination", details.getDestination());
                promise.resolve(map);
            }

            @Override
            public void onFailure(Exception exception) {
                promise.reject(exception);
            }
        };
        lastForgotPasswordContinuation = null;
        user.forgotPasswordInBackground(handler);
    }

    @ReactMethod
    public void completeForgotPasswordResetCode(ReadableMap authenticationData, final Promise promise){
        if(lastForgotPasswordContinuation == null){
            promise.reject(new Exception("No pending forgot password continuation"));
        }
        lastForgotPasswordContinuation.setPassword(authenticationData.getString("password"));
        lastForgotPasswordContinuation.setVerificationCode(authenticationData.getString("verificationCode"));
        lastForgotPasswordContinuation.continueTask();
        promise.resolve(true);
    }

    @ReactMethod
    public void resendConfirmationCode(ReadableMap authenticationData, final Promise promise){
        CognitoUser user = getOrCreateUser(authenticationData);
        VerificationHandler handler = new VerificationHandler() {
            @Override
            public void onSuccess(CognitoUserCodeDeliveryDetails verificationCodeDeliveryMedium) {
                WritableMap map = Arguments.createMap();
                map.putString("attributeName", verificationCodeDeliveryMedium.getAttributeName());
                map.putString("deliveryMedium", verificationCodeDeliveryMedium.getDeliveryMedium());
                map.putString("destination", verificationCodeDeliveryMedium.getDestination());
                promise.resolve(map);
            }

            @Override
            public void onFailure(Exception exception) {
                promise.reject(exception);
            }
        };
        user.resendConfirmationCodeInBackground(handler);
    }

    private CognitoUser getOrCreateUser(ReadableMap authenticationData){
        CognitoUser user = cognitoUserPool.getCurrentUser();
        if(user == null) user = cognitoUserPool.getUser(authenticationData.getString("userId"));
        return user;
    }

    private AuthenticationContinuation authenticationContinuation;
    private MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation;
    private ChallengeContinuation challengeContinuation;

    private AuthenticationHandler createAuthenticationHandler(final Promise promise){
        final ReactNativeAwsCognitoUserPoolModule module = this;
        AuthenticationHandler handler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
                WritableMap map = Arguments.createMap();
                map.putString("activity", "AuthenticationComplete");
                map.putBoolean("authenticated", userSession.isValid());
                map.putString("idToken", userSession.getIdToken().getJWTToken());
                promise.resolve(map);
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String UserId) {
                module.authenticationContinuation = authenticationContinuation;
                WritableMap map = Arguments.createMap();
                map.putString("activity", "AuthenticationDetailsRequired");
                map.putString("userId", UserId);
                promise.resolve(map);
            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
                module.multiFactorAuthenticationContinuation = continuation;
                WritableMap map = Arguments.createMap();
                map.putString("activity", "MfaCodeRequired");
                promise.resolve(map);
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                module.challengeContinuation = challengeContinuation;
                WritableMap map = Arguments.createMap();
                map.putString("activity", "AuthenticationChallengeRequired");
                map.putString("challengeName", continuation.getChallengeName());
                map.putMap("parameters", fromMap(continuation.getParameters()));
                promise.resolve(map);
            }

            @Override
            public void onFailure(Exception exception) {
                promise.reject(exception);
            }
        };
        return handler;
    }

    private java.util.Map<String, String> fromReadableMap(ReadableMap map){
        java.util.Map<String, String> result = new HashMap<String, String>();
        ReadableMapKeySetIterator i = map.keySetIterator();
        while(i.hasNextKey()){
            String key = i.nextKey();
            result.put(key, map.getString(key));
        }
        return result;
    }

    private WritableMap fromMap(java.util.Map<String,String> values){
        WritableMap result = Arguments.createMap();
        Iterator<String> i = values.keySet().iterator();
        while(i.hasNext()){
            String key = i.next();
            result.putString(key, values.get(key));
        }
        return result;
    }

    private CognitoUserAttributes toCognitoUserAttributes(ReadableMap values){
        CognitoUserAttributes result = new CognitoUserAttributes();
        ReadableMapKeySetIterator iterator = values.keySetIterator();
        while(iterator.hasNextKey()){
            String key = iterator.nextKey();
            result.addAttribute(key, values.getString(key));
        }
        return result;
    }

    private Regions getRegion(String region) {
        Regions _region = Regions.DEFAULT_REGION;
        if (region != null && region != "") {
            _region = Regions.fromName(region);
        }
        return _region;
    }
}