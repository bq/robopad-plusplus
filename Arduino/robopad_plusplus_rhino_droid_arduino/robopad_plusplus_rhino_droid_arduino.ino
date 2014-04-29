/*  
 *
 *     ********************************************************
 *     ********************************************************
 *     ***                                                  ***
 *     ***                   Rhino Droid                    ***
 *     ***                                                  ***
 *     ******************************************************** 
 *     ********************************************************
 *
 *    Arduino code for the Rhino robot for using with the
 *    Android RoboPad++ app. 
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
#define bufferSize 2

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

/* Received postion for the slider */
int sliderPos = 0; 


/******************************************************************
 *                     Definition of functions                    *
 ******************************************************************/

void stopWheels() {
  leftWheel.write(wheelStopValue);
  delay(3);

  rightWheel.write(wheelStopValue);
  delay(3);
}


void moveLeftWheelUp() {
  leftWheel.write(leftWheelFordwardValue);
  delay(3);
}


void moveRightWheelUp() {
  rightWheel.write(rightWheelFordwardValue);
  delay(3);
}

void moveLeftWheelDown() {
  leftWheel.write(leftWheelBackwardsValue);
  delay(3);
}


void moveRightWheelDown() {
  rightWheel.write(rightWheelBackwardsValue);
  delay(3);
}

void moveLeftWheelStop() {
  leftWheel.write(wheelStopValue);
  delay(3);
}


void moveRightWheelStop() {
  rightWheel.write(wheelStopValue);
  delay(3);
}



/* Manage the buffer of data */
void checkData(char* data){  
  
  if (data[0] == 'S') {
    /* Stop button pressed */
    leftWheel.write(wheelStopValue);
    rightWheel.write(wheelStopValue);
    
  } else if (data[0] == 'C') {
    /* Charge button pressed */
    moveLeftWheelDown();
    moveRightWheelDown();
    delay(800);
    moveLeftWheelUp();
    moveRightWheelUp();
    delay(1400);
    stopWheels();
    
  } else if (data[0] == 'U') {
    /* Up button pressed */
    moveLeftWheelUp();
    moveRightWheelUp();
  
  } else if (data[0] == 'D') {
    /* Down button pressed */
    moveLeftWheelDown();
    moveRightWheelDown();

  } else if (data[0] == 'L') {
    if(data[1] == 0) {
      moveRightWheelUp();
      moveLeftWheelStop();
    } else if(data[1] == 'U') {
      moveLeftWheelUp();
    } else if (data[1] == 'D') {
      moveLeftWheelDown();
    } else if (data[1] == 'S') {
      moveLeftWheelStop();
    } 

  }  else if (data[0] == 'R') {
    if(data[1] == 0) {
      moveLeftWheelUp();
      moveRightWheelStop();
    } else if(data[1] == 'U') {
      moveRightWheelUp();
    } else if (data[1] == 'D') {
      moveRightWheelDown();
    } else if (data[1] == 'S') {
      moveRightWheelStop();
    } 

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
    checkData(dataBuffer);
    
  }
}
 
