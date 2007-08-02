#/usr/bin/bash

cd ../Tetrlais/bin/
jar -cvf Tetrlais.jar ./Tetrlais/*.class
mv Tetrlais.jar ../../envJars/
cd ../../envJars

cd ../MountainCar/bin/
jar -cvf MountainCar.jar ./MountainCar/*.class
mv MountainCar.jar ../../envJars/
cd ../../envJars
