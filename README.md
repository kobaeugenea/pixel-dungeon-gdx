pixel-dungeon-gdx
=================

GDX port of the awesome [Pixel Dungeon](https://github.com/watabou/pixel-dungeon)

Import project to IDEA
----------
Before import execute `gradlew idea` in project directory.
IDEA Run Configurations:
* 'Android' - run and debug for android version
* 'Desktop' - run and debug for desktop version
* 'Update dependencies' - update gradle dependencies for IDEA

If you lost Run Configuration you can restore it by deleting 'pixel-dungeon-gdx.iws' file and run `gradlew idea` in 
the project directory.

Quickstart
----------

Do `./gradlew <task>` to compile and run the project, where `task` is:

* Desktop: `desktop:run`
* Android: `android:installDebug android:run`
* iOS: `launchIosDevice` or `launchIphoneSimulator` or `launchIpadSimulator`
* HTML: `html:superDev` (this doesn't work yet, some classes need to be changed)
* Generate IDEA project: `idea`

For more info about those and other tasks: https://github.com/libgdx/libgdx/wiki/Gradle-on-the-Commandline#running-the-html-project
