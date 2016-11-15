
package com.getwala;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.amazonaws.regions.Regions;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;

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