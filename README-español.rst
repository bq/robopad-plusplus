============================
RoboPad-plusplus / RoboPad++
============================

RoboPad++ es una aplicación Android para controlar robots a través del Bluetooth del dispositivo móvil. Puedes elegir distintos tipos de robots con sus mandos de control específicos de cada uno. Todos los robots deben utilizar una placa Arduino y un módulo Bluetooth.

Los tipos de robots que se pueden seleccionar hoy en día son aquellos diseñados por la empresa Bq, además de cualquier otro que sea controlado por una placa Arduino.

Puedes usar una impresora 3D para crear tu printbot (Renacuajo, Escarabajo y Rhino). Tanto los archivos con las partes imprimibles como el código Arduino de cada uno de los printbots de Bq se puede descargar desde la web http://diy.bq.com/printbots/ (el código no está disponible aún en la web).

Hay un mando de control para robots genéricos que te permite controlar tu propio robot con hasta 6 funcionalidades más la cruceta de movimientos o bien aumentar el número de funcionalidades de algún printbot ya existente con los 6 botones de comandos.

Estos 6 botones mandan el siguiente carácter a la placa Arduino:

| Botón 1 - '1'
| Botón 2 - '2'
| Botón 3 - '3'
| Botón 4 - '4'
| Botón 5 - '5'
| Botón 6 - '6'

La diferencia con la aplicación RoboPad es que ¡ahora tienes la nueva funcionalidad de programar de los movimientos del robot! Puedes añadir los movimientos que quieres que el robot realice, reorganizarlos y eliminarlos uno a uno o todas a la vez. Cuando estés listo, puedes enviar los movimientos al robot y ver cómo los realiza.

Si tienes alguna duda puedes consultarnos a través del `foro de DIY <http://diy.bq.com/forums/forum/forum/>`_ o mandando un correo a diy@bq.com.


Características
===============

#. Controla robots que utilicen una placa Arduino a través del Bluetooth de tu dispositivo móvil

#. 6 botones en el tipo de robot genérico para usar en tus propios robots
   
#. Programa los movimientos de tu robot y mira cómo los realiza

#. Mandos de control específicos para los printbots Renacuajo, Escarabajo y Rhino de Bq

#. Administración de la conexión Bluetooth para ahorrar batería


Instalación
===========

#. RoboPad++ depende de las bibliotecas droid2ino y drag-drop-grid. Clona los repositorios de ambas bibliotecas::

    git clone https://github.com/bq/droid2ino.git
    git clone https://github.com/bq/drap-drop-grid.git

#. Instala la biblioteca droid2ino en tu repositorio local::
  
    cd droid2ino/droid2ino
    gradle install

#. Instala la biblioteca drag-drop-grid en tu repositorio local::
   
    * Versiones de Gradle igual o menores de 1.10 y versiones de drag-drop-grid igual o menores de 1.6::
  
        cd drag-drop-grid/drag-drop-grid
        gradle publishToMavenLocal

    * Versiones de Gradle mayores de 1.10 y versiones de drag-drop-grid mayores de 1.6::
        
        cd drag-drop-grid/drag-drop-grid
        gradle install

#. Instala `Android Studio <https://developer.android.com/sdk/installing/studio.html>`_ y `Gradle <http://www.gradle.org/downloads>`_.

#. Si usas Linux de 64 bits, necesitarás instalar ia32-libs-multiarch::

	sudo apt-get update
	sudo apt-get upgrade
	sudo apt-get install ia32-libs-multiarch 

#. Clona el repositorio RoboPad-plusplus::
	
	git clone https://github.com/bq/robopad-plusplus.git

#. En Android Studio, ve a ``File`` > ``Open`` y selecciona el proyecto RoboPad clonado previamente.

#. Mete el código Arduino adecuado a tu robot. Puedes encontrarlo en la carpeta Arduino de este proyecto o en la `web de DIY de Bq  <http://diy.bq.com/printbots/>`_ (el código no está disponible aún en la web).
   
#. Para instalar el firmware del printbot Cangrejo que se encuentra en la carpeta de Arduino, tienes que copiar la carpeta ``Oscillator`` (que está en la carpeta Oscillator_Lib) en la carpeta ``libraries``  en la carpeta donde has instalado el programa de Arduino. Puedes encontrar información más detallada para hacer esto en la  `documentación de la web de Arduino <http://arduino.cc/en/Guide/Libraries>`_. 
   


Requisitos
==========

- `Java JDK <http://www.oracle.com/technetwork/es/java/javase/downloads/jdk7-downloads-1880260.html>`_ 

- `Android Studio <https://developer.android.com/sdk/installing/studio.html>`_ 

- `Maven <http://maven.apache.org/download.cgi>`_. Si estás en Ubuntu::
    
    sudo apt-get update
    sudo apt-get install maven

- `Gradle <http://www.gradle.org/downloads>`_ recommended version 1.10
  
- `Arduino IDE <http://arduino.cc/en/Main/Software#.UzBT5HX5Pj4>`_ 

- La placa Arduino con un módulo Bluetooth


Limitaciones
============

- Para evitar el problema de mostrar mensajes recibidos por parte de la placa Arduino vacíos o partidos, la librería droid2ino utiliza una serie de carácteres de escape. 
 
  - Carácter de escape de inicio del mensaje: ``&&`` 

  - Carácter de escape de fin del mensaje : ``%%``

  Por lo tanto, un ejemplo de cómo el programa Arduino tiene que mandar un mesaje sería::

	  &&Hola mundo desde Arduino%%

- El mando de control de robot genérico tiene 6 botones que pueden ser usados para dotar a tu propio robot de más funcionalidad. Estos botones mandan los mensajes '1', '2', '3', '4', '5' y '6' respectivamnete a la placa Arduino.


Licencia
========

RoboPad-plusplus es distribuido en términos de la licencia GPL. Consulte la web http://www.gnu.org/licenses/ para más detalles.
