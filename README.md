
# react-native-text-direction
A react native module that uses the Android 8 builtin TextClassificationManager to identify which lanaguages are represented in a given text string and determine if the String is an RTL (Right To Left) String. The logic applyed here is that if there is a RTL language identified in the string - the String is a RTL String (=Should be treated as a RTL string when displaying it).
There are two functions in this library :
IsAvailabe() - As the TextClassificationManager functnionality is available only in Android 8 - this functions will only return 'true' on Android 8 devices. 
IsRTL(input) - On devices where the functionality is available - the lib will determine if the String is a RTL one.

## Getting started

`$ npm install react-native-text-direction --save`

### Mostly automatic installation

`$ react-native link react-native-text-direction`

### Manual installation


#### iOS

Currently not supported, Hopefully will be in the future.

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNTextDirectionPackage;` to the imports at the top of the file
  - Add `new RNTextDirectionPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-text-direction'
  	project(':react-native-text-direction').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-text-direction/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-text-direction')
  	```

#### Windows

Currently not supported, ptobably never will be.

## Usage
```javascript
import RNTextDirection from 'react-native-text-direction';

RNTextDirection.isAvailable()
    .then(value => { 
      global.isTextDirectionAvailable = value;
    })
    .catch((error) => {
      global.isTextDirectionAvailable = false;
  });
console.log("RNTextDirection.isAvailable()=" + global.isTextDirectionAvailable) 

//Additional code runs here...

if (global.isTextDirectionAvailable){
    RNTextDirection.isRTL(typedText)
      .then(value => { 
        console.log("isRTL returned " + value);
      })
      .catch((error) => {
        console.log("Error running isRTL: " + error);
    });
}else{
  console.log("Sorry, the text direction native module is not available on this device");
}
```
## To be added in the near future 
Tests!
