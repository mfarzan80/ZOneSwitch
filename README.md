# Zero One Switch

A switch that uses 0 for the inactive state and 1 for the active state, and it has an animation for changing its value. this library written with Jetpack Compose.


<table style="width:100%">
  <tr>
    <th><img src="./normal.gif" alt="normal"  >

</th>
    <th>
<img src="./bouncy.gif" alt="bouncy" >
    </th>
    
  </tr>
  <tr>
    <th>Normal</td>
    <th>Bouncy</td>
    
  </tr>

</table>


# Installing
Add JitPack Maven to `setting.gradle` file like this:

```gradle
dependencyResolutionManagement {
    ...
    repositories {
        ...
        maven { url "https://jitpack.io" }  
       
    }
}

```
If you are using a lower version `7.x.x` of Gradle build tools, you must add JitPack Maven to your root build.gradle file like this:
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

And then add dependency to the module's build.gradle

```gradle
dependencies {
...
  implementation "com.github.mfarzan80:ZOneSwitch:1.0.2"
}
```

# How to use
See this example code to understand how to use and customize the switch

```Kotlin
val value = remember { mutableStateOf(false) }
ZeroOneSwitch(
    checked = value.value,
    animationDuration = 1000,  
    thumbBounce = true,        //thumb's bouncing animation
    colors = SwitchDefaults.colors(
    checkedTrackColor = Color(0xFF47e789),
    uncheckedTrackColor = Color(0xFFFB4550),
    checkedTrackAlpha = 1f,
    uncheckedTrackAlpha = 1f,
    checkedThumbColor = Color.White,
    uncheckedThumbColor = Color.White,
    ),
    onCheckedChange = { value.value = it }
)
```
