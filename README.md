# Youtube2ResourcePackSongs

Youtube2ResourcePackSongs is a Java program which converts Youtube videos to resource packs.

## Installation

* Install Java 8 or higher
* TODO

## Usage

### Input CSV

* Prepare a text file called ``input.csv`` where you add all youtube songs you want to add to the resource pack.
* The format is ``<youtubeUrl>,<pathInResourcePack>``
* A sample of such a file is shown below where the same youtube song gets added to the resource pack  three time as 3 different names. 

**Sample:**
```xml
https://www.youtube.com/watch?v=dQw4w9WgXcQ,custom/mysong
https://www.youtube.com/watch?v=dQw4w9WgXcQ,custom/thesamesong
https://www.youtube.com/watch?v=dQw4w9WgXcQ,custom/againthesamesong
```

### GUI

* The program can be started by running ``java -jar Youtube2ResourcePackSongs.jar`` or double clicking the  ``Youtube2ResourcePackSongs.jar`` file.
* Select the input.csv file containing all the youtube links and target sound names.
* Select the path where the resourcepack.zip should be saved.
* Click on a generate and wait until the success message ``Finished generating resource pack.`` gets displayed. If the program fails, take a look at the latest.log file.

### CLI

* The program can be started headless by running ``java -jar Youtube2ResourcePackSongs.jar -headless -immediately -ifile 'input.csv' -ofile 'resourcepack.zip'``.
* For all parameters see ``java -jar Youtube2ResourcePackSongs.jar --help``

### Minecraft

* Put the generated ``resourcepack.zip`` file into the ``%appdata%/Roaming/.minecraft/resourcepacks`` folder. 
* Select the resource pack in Minecraft.
* **1.13 and above:** Execute the command ``/playsound minecraft:<sound> master <player>``
* **1.8 to 1.12.2:** Execute the command ``/playsound <sound> <player>``

**Sample:**
```
/playsound minecraft:custom.mysong master Shynixn
```

## Contributing

* Clone the repository to your local environment
* Execute ``gradle build`` to run the tests

## Licence

Copyright 2020-2020 Shynixn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
