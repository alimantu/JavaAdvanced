set sourceDir=f:\Backup\Documents\Java\Advanced2015\Implementor\src
set classPath1=ru\ifmo\ctddev\salinskii\implementor\
set classPath2=info\kgeorgiy\java\advanced\implementor\
set className=ImplementorRunner
set packageName=ru.ifmo.ctddev.salinskii.implementor
set outputDir=.\out
mkdir %outputDir%
javac -cp %sourceDir%\ -source 8 -d %outputDir% %sourceDir%\%classPath1%*java %sourceDir%\%classPath2%*Impler*.java
jar cfe %className%.jar %packageName%.%className% -C %outputDir% .
pause
