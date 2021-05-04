a social media app to help in times of pandemic.
# Sannad App Installation

(*) This installation guide provides a step by step guide to building and installing the app with gradle from the command line. If the project is opened in Android Studio you will need to install the Lombok Plugin and then let gradle sync.

##Legal notes:

(*) Since the App is provided with all of its resources and code, the only kind of apk that you are allowed to generate using gradle is the debug version. The apk provided within this folder is the regular one, you however cannot assemble one like it yourself, as the application key and password will be kept private. Thank you for understanding.

(*) This software is provided on a read-only basis. Recreating or reuploading the software as your own will be seen as intellectual property theft and will face legal consequences. Furthermore will the stolen software be reported to Google if uploaded to the Play Store.

## Installation:
* Fire up a command console in the folder Sannad_App (project root directory)
* Now you can choose between using either a gradle version preinstalled on your system or using the provided gradlew file to build the project.
* To assemble the debug apk simply type ''gradle assembleDebug'' or ''.\gradlew assembleDebug'' 
* If you wish to immediately install the apk use the command ''gradle installDebug'' or ''.\gradlew installDebug'' instead
* You can find the generated apk file within the folder app > build > outputs > apk > debug

### Video
Music is non copyright, link: https://www.youtube.com/watch?v=SVLXGMfgU5s
