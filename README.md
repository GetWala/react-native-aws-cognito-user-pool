
# react-native-aws-cognito-user-pool

## Getting started

`$ npm install react-native-aws-cognito-user-pool --save`

### Mostly automatic installation

`$ react-native link react-native-aws-cognito-user-pool`

### Manual installation


#### iOS
### This is not implemented yet
1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-aws-cognito-user-pool` and add `ReactNativeAwsCognitoUserPool.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libReactNativeAwsCognitoUserPool.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.getwala.ReactNativeAwsCognitoUserPoolPackage;` to the imports at the top of the file
  - Add `new ReactNativeAwsCognitoUserPoolPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-react-native-aws-cognito-user-pool'
  	project(':react-native-react-native-aws-cognito-user-pool').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-react-native-aws-cognito-user-pool/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-react-native-aws-cognito-user-pool')
  	```

#### Windows
### This is not implemented yet
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `ReactNativeAwsCognitoUserPool.sln` in `node_modules/react-native-react-native-aws-cognito-user-pool/windows/ReactNativeAwsCognitoUserPool.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Cl.Json.ReactNativeAwsCognitoUserPool;` to the usings at the top of the file
  - Add `new ReactNativeAwsCognitoUserPoolPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import ReactNativeAwsCognitoUserPool from 'react-native-aws-cognito-user-pool';

//first initialize the user pool
ReactNativeAwsCognitoUserPool.initializeUserPool(poolId, clientId, clientSecret, region);

ReactNativeAwsCognitoUserPool.signUp(userId, password, attributes).then(result=>{
	//do something with the result
	//this will have the userId and userPoolId
}).catch(error=>{
	//any errors that occurred
});

ReactNativeAwsCognitoUserPool.confirmSignUp(confirmationCode).then(result=>{
	//this will be true if successful
}).catch(error=>{
	//any errors that occurred
});

ReactNativeAwsCognitoUserPool.isAuthenticated(authenticationData).then(result=>{
	//the result will be true if authenticated, or else false 
}).catch(error=>{
	//any errors that occurred
});

ReactNativeAwsCognitoUserPool.authenticate(authenticationData).then(result=>{
	//the result will indicate the activity that needs to take place next
	//either:
	//AuthenticationComplete 
	//OR 
	//AuthenticationDetailsRequired
	//OR
	//MfaCodeRequired
	//OR
	//AuthenticationChallengeRequired 
}).catch(error=>{
	//any errors that occurred
});

//complete a pending authentication challenge received during authenticate
ReactNativeAwsCognitoUserPool.completeAuthenticationChallenge(authenticationData).then(result=>{
	
}).catch(error=>{
	//any errors that occurred
});

//complete a pending authentication details request received during authenticate
ReactNativeAwsCognitoUserPool.completeAuthenticationDetails(authenticationData).then(result=>{

}).catch(error=>{
	//any errors that occurred
});

//complete a pending mfa code request received during authenticate
ReactNativeAwsCognitoUserPool.completeMfaCode(mfaCode).then(result=>{

}).catch(error=>{
	//any errors that occurred
});

ReactNativeAwsCognitoUserPool.signOut().then(result=>{
	//will be true if successful
}).catch(error=>{
	//any errors that occurred
});

ReactNativeAwsCognitoUserPool.forgotPassword(authenticationData).then(result=>{
	//the result will indicate the activity that needs to take place next
	//either:
	//ForgotPasswordComplete
	//OR 
	//ForgotPasswordResetCodeRequired
}).catch(error=>{
	//any errors that occurred
});

ReactNativeAwsCognitoUserPool.completeForgotPasswordResetCode(authenticationData).then(result=>{

}).catch(error=>{
	//any errors that occurred
});

//resend the last confirmation code
ReactNativeAwsCognitoUserPool.resendConfirmationCode(authenticationData).then(result=>{
	//the result will include the deliveryMedium and destination
}).catch(error=>{
	//any errors that occurred
});
```