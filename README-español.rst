=======
RoboPad
=======

RoboPad es una aplicación Android para controlar robots a través del Bluetooth del dispositivo móvil. Puedes elegir distintos tipos de robots con sus mandos de control específicos de cada uno. Todos los robots deben utilizar una placa Arduino y un módulo Bluetooth.

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


Características
===============

#. Controla robots que utilicen una placa Arduino a través del Bluetooth de tu dispositivo móvil

#. 6 botones en el tipo de robot genérico para usar en tus propios robots

#. Mandos de control específicos para los printbots Renacuajo, Escarabajo y Rhino de Bq.


Instalación
===========

#. RoboPad_plusplus depende de las bibliotecas droid2ino y drag_drop_grid. Clona los repositorios de ambas bibliotecas::

    git clone https://github.com/bq/droid2ino.git
    git clone https://github.com/bq/drap_drop_grid.git

#. Instala la biblioteca droid2ino en tu repositorio local::
  
    cd droid2ino/droid2ino
    gradle install

#. Instala la biblioteca drag_drop_grid en tu repositorio local::
   
   cd drag_drop_grid/drag_drop_grid
   gradle publishToMavenLocal

#. Instala `Android Studio <https://developer.android.com/sdk/installing/studio.html>`_ y `Gradle <http://www.gradle.org/downloads>`_.

#. Si usas Linux de 64 bits, necesitarás instalar ia32-libs-multiarch::

	sudo apt-get update
	sudo apt-get upgrade
	sudo apt-get install ia32-libs-multiarch 

#. Clona el repositorio RoboPad::
	
	git clone https://github.com/bq/robopad.git

#. En Android Studio, ve a ``File`` > ``Open`` y selecciona el proyecto RoboPad clonado previamente.

#. Sube el código Arduino adecuado a tu robot. Puedes encontrarlo en la carpeta Arduino de este proyecto o en la `web de DIY de Bq  <http://diy.bq.com/printbots/>`_ (el código no está disponible aún en la web).


Requisitos
==========

- `Java JDK <http://www.oracle.com/technetwork/es/java/javase/downloads/jdk7-downloads-1880260.html>`_ 

- `Android Studio <https://developer.android.com/sdk/installing/studio.html>`_ 

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

RoboPad es distribuido en términos de la licencia GPL. Consulte la web http://www.gnu.org/licenses/ para más detalles.
