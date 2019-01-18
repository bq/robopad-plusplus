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

#define MI_PRIMER_KIT_DE_ROBOTICA_BLUETOOTH      1
#define BQ_ZUM_BLUETOOTH                         2
#define BQ_ZUM_CORE_2                            3
/* Select in this variable which board are you going to use */
int boardSelected = BQ_ZUM_CORE_2;

/* Pin definition of the board to be used */
#define pinLeftWheel            6
#define pinRightWheel           9
#define pinSensorIRLeft         2   /*   Left infrared sensor     */ 
#define pinSensorIRRight        3   /*   Right infrared sensor    */

/* Define the posible states of the state machine of the program */
#define MANUAL_CONTROL_STATE    0
#define LINE_FOLLOWER           1

/* Bauderate of the Bluetooth*/
#define BAUDS_MI_PRIMER_KIT_DE_ROBOTICA_BLUETOOTH       38400
#define BAUDS_BQ_ZUM_BLUETOOTH                          19200
#define BAUDS_BQ_ZUM_CORE_2                            115200

/* Definition of the values ​​that can take continuous rotation servo,
 that is, the wheels */
#define wheelStopValue            90
#define leftWheelFordwardValue    0
#define leftWheelBackwardsValue   180
#define rightWheelFordwardValue   180
#define rightWheelBackwardsValue  0

/* Size of the received data buffer */
#define bufferSize 1

/* Default delay */
#define defaultDelay        10

/* Variable that controls the current state of the program */
int currentState;

/* A object from the Servo class is created for each servo */
Servo leftWheel;                       /*  Values from 0 to 180  */
Servo rightWheel;                      /*  Values from 0 to 180  */

/* Variables of the line follower mode */
int rightIR;
int leftIR;
int BLACK = 0;
int WHITE = 1;

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
  delay(defaultDelay);

  rightWheel.write(wheelStopValue);
  delay(defaultDelay);
}

void goForwards() {
  leftWheel.write(leftWheelFordwardValue);
  delay(defaultDelay);

  rightWheel.write(rightWheelFordwardValue);
  delay(defaultDelay);
}

void goBackwards() {
  leftWheel.write(leftWheelBackwardsValue);
  delay(defaultDelay);

  rightWheel.write(rightWheelBackwardsValue);
  delay(defaultDelay);
}

void goLeft() {
  leftWheel.write(wheelStopValue);
  delay(defaultDelay);

  rightWheel.write(rightWheelFordwardValue);
  delay(defaultDelay);
}

void goRight() {
  leftWheel.write(leftWheelFordwardValue);
  delay(defaultDelay);

  rightWheel.write(wheelStopValue);
  delay(defaultDelay);
}

/*
  Perform the action required by the user of the Android app
*/
void setAction(char* data) {
  
  switch(data[0]) {

    /* Line follower mode button pressed */
    case 'I':
      currentState = LINE_FOLLOWER;
      break;

    /* Manual control mode button pressed */
    case 'M':
      currentState = MANUAL_CONTROL_STATE;
      stopWheels();
      break;
   
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


void followTheLine() {
  /* Read the state of the sensors */
  rightIR = digitalRead(pinSensorIRLeft);
  leftIR = digitalRead(pinSensorIRRight);

  /* If the right sensor reads black, we go straight forward, else
     if it reads white, we turn to the left */
  if (rightIR == BLACK) {
    leftWheel.write(leftWheelFordwardValue);
    delay(defaultDelay);
  
  } else {
    leftWheel.write(wheelStopValue);
    delay(defaultDelay);
  }
  
  /* If the left sensor reads black, we go straight forward, else
    if it reads white, we turn to the right */
  if (leftIR == BLACK) {
    rightWheel.write(rightWheelFordwardValue);
    delay(defaultDelay);
  
  } else {
    rightWheel.write(wheelStopValue);
    delay((defaultDelay));
  }
}


/******************************************************************
 *                             Setup                              *
 ******************************************************************/

void setup(){
  
  /* Open the Bluetooth Serial and empty it */
  long bauds = BAUDS_MI_PRIMER_KIT_DE_ROBOTICA_BLUETOOTH;
  if (boardSelected == BQ_ZUM_BLUETOOTH) {
    bauds = BAUDS_BQ_ZUM_BLUETOOTH;
  } else if (boardSelected == BQ_ZUM_CORE_2) {
    bauds = BAUDS_BQ_ZUM_CORE_2;
  }
  Serial.begin(bauds);
  Serial.flush();     
  
  /* Define the appropiate pin to each object */
  leftWheel.attach(pinLeftWheel);
  rightWheel.attach(pinRightWheel);

  /* The robot is stopped at the beginning */
  stopWheels();

  /* Put the IR sensors as input */
  pinMode(pinSensorIRLeft, INPUT);
  pinMode(pinSensorIRRight, INPUT);

  /* Default state is manual control */
  currentState = MANUAL_CONTROL_STATE;
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

  if(currentState == LINE_FOLLOWER) {
    followTheLine();
  }

}  
 
