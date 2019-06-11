
# react-native-text-direction

## Getting started

`$ npm install react-native-text-direction --save`

### Mostly automatic installation

`$ react-native link react-native-text-direction`

### Manual installation


#### iOS

Currently not supported

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

Currently not supported

## Usage
```javascript
import RNTextDirection from 'react-native-text-direction';

// TODO: What to do with the module?
RNTextDirection;
```
  
