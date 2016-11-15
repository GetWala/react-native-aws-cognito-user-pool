
# react-native-react-native-aws-cognito-user-pool

## Getting started

`$ npm install react-native-aws-cognito-user-pool --save`

### Mostly automatic installation

`$ react-native link react-native-aws-cognito-user-pool`

### Manual installation


#### iOS

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
[Read it! :D](https://github.com/ReactWindows/react-native)

1. In Visual Studio add the `ReactNativeAwsCognitoUserPool.sln` in `node_modules/react-native-react-native-aws-cognito-user-pool/windows/ReactNativeAwsCognitoUserPool.sln` folder to their solution, reference from their app.
2. Open up your `MainPage.cs` app
  - Add `using Cl.Json.ReactNativeAwsCognitoUserPool;` to the usings at the top of the file
  - Add `new ReactNativeAwsCognitoUserPoolPackage()` to the `List<IReactPackage>` returned by the `Packages` method


## Usage
```javascript
import ReactNativeAwsCognitoUserPool from 'react-native-react-native-aws-cognito-user-pool';

// TODO: What do with the module?
ReactNativeAwsCognitoUserPool;
```
  