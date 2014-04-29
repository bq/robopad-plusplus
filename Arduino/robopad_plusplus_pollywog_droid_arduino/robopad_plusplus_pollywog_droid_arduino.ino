/*  
 *
 *     ********************************************************
 *     ********************************************************
 *     ***                                                  ***
 *     ***                Pollywog Droid                   ***
 *     ***                                                  ***
 *     ******************************************************** 
 *     ********************************************************
 *
 *    Arduino code for the Pollywog robot for using with the
 *    Android RoboPad app. 
 *     
 *   ****************************************************
 *   * Fecha: 05/03/2014                                *
 *   * Autor:Estefana Sarasola Elvira                   *
 *   * Mail: diy@bq.com                                 *
 *   * Licencia: GNU General Public License v3 or later *
 *   ****************************************************
 */

/******************************************************************/
/******************************************************************/



/******************************************************************
 *                           Libraries                            *
 ******************************************************************/ 

#include <Servo.h>


/******************************************************************
 *                    Definition of variables                     *
 ******************************************************************/

/* Pin definition of the board to be used */
#define pinLeftWheel 6
#define pinRightWheel 9

/* Definition of the values ​​that can take continuous rotation servo,
 that is, the wheels */
#define wheelStopValue 90
#define leftWheelFordwardValue 0
#define leftWheelBackwardsValue 180
#define rightWheelFordwardValue 180
#define rightWheelBackwardsValue 0

/* Size of the received data buffer */
#define bufferSize 1

/* A object from the Servo class is created for each servo */
Servo leftWheel;                       /*  Values from 0 to 180  */
Servo rightWheel;                      /*  Values from 0 to 180  */

/*  A char buffer to storage the received data from the Bluetooth
    Serial */
char dataBuffer[bufferSize]; 

/* Buffer iterator */
int i = 0;

/* Number of characters availables in the Serial */
int numChar = 0;


/******************************************************************
 *                     Definition of functions                    *
 ******************************************************************/

void stopWheels() {
  leftWheel.write(wheelStopValue);
  delay(3);

  rightWheel.write(wheelStopValue);
  delay(3);
}

void goForwards() {
  leftWheel.write(leftWheelFordwardValue);
  delay(3);

  rightWheel.write(rightWheelFordwardValue);
  delay(3);
}

void goBackwards() {
  leftWheel.write(leftWheelBackwardsValue);
  delay(3);

  rightWheel.write(rightWheelBackwardsValue);
  delay(3);
}

void goLeft() {
  leftWheel.write(wheelStopValue);
  delay(3);

  rightWheel.write(rightWheelFordwardValue);
  delay(3);
}

void goRight() {
  leftWheel.write(leftWheelFordwardValue);
  delay(3);

  rightWheel.write(wheelStopValue);
  delay(3);
}

/*
  Perform the action required by the user of the Android app
*/
void setAction(char* data) {
  
  switch(data[0]) {
   
    /* Stop button pressed */
    case 'S':
      stopWheels();
      break;

    /* Up button pressed  */
    case 'U':
      goForwards();
      break;

    /* Down button pressed  */ 
    case 'D':
      goBackwards();
      break;

    /* Left button pressed  */
    case 'L':
      goLeft();
      break;

    /* Right button pressed  */
    case 'R':
      goRight();
      break;
   
  }
    
  /* Empty the Serial */      
  Serial.flush();
    
}


/******************************************************************
 *                             Setup                              *
 ******************************************************************/

void setup(){
  
  /* Open the Bluetooth Serial and empty it */
  Serial.begin(38400); 
  Serial.flush();     
  
  /* Define the appropiate pin to each object */
  leftWheel.attach(pinLeftWheel);
  rightWheel.attach(pinRightWheel);

  /* The robot is stopped at the beginning */
  stopWheels();
}


/******************************************************************
 *                       Main program loop                        *
 ******************************************************************/

void loop() {
 
   /* If there is something in the Bluetooth serial port */
  if (Serial.available() > 0) { 
   
    /* Reset the iterator and clear the buffer */
    i = 0;
    memset(dataBuffer, 0, sizeof(dataBuffer));  
    
    /* Wait for let the buffer fills up. Depends on the length of 
       the data, 1 ms for each character more or less */
    delay(bufferSize); 

    /* Number of characters availables in the Bluetooth Serial */
    numChar = Serial.available();   
    
    /* Limit the number of characters that will be read from the
       Serial to avoid reading more than the size of the buffer */
    if (numChar > bufferSize) {
          numChar = bufferSize;
    }

    /* Read the Bluetooth Serial and store it in the buffer*/
    while (numChar--) {
        dataBuffer[i++] = Serial.read();

        /* As data trickles in from your serial port you are 
         grabbing as much as you can, but then when it runs out 
         (as it will after a few bytes because the processor is 
         much faster than a 9600 baud device) you exit loop, which
         then restarts, and resets i to zero, and someChar to an 
         empty array.So please be sure to keep this delay */
        delay(3);
    } 

    /* Manage the data */
    setAction(dataBuffer);
    
  }
}  
 
