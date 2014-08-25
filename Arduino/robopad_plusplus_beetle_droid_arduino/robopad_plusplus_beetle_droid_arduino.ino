/*  
 *
 *     ********************************************************
 *     ********************************************************
 *     ***                                                  ***
 *     ***                   Beetle Droid                   ***
 *     ***                                                  ***
 *     ******************************************************** 
 *     ********************************************************
 *
 *    Arduino code for the Beetle robot for using with the
 *    Android RoboPad++ app. 
 *     
 *   ****************************************************
 *   * Fecha: 18/03/2014                                *
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

#define pinLeftWheel   6   
#define pinRightWheel  9   
#define pinClaw       11
#define pinSensorIRLeft         2   /*   Left infrared sensor     */ 
#define pinSensorIRRight        3   /*   Right infrared sensor    */
#define pinSensorLDRLeft       A2   /*   Left light sensor        */ 
#define pinSensorLDRRight      A3   /*   Right light sensor       */

/* Definition of the values ​​that can take continuous rotation servo,
 that is, the wheels */
#define wheelStopValue 90
#define leftWheelFordwardValue 0
#define leftWheelBackwardsValue 180
#define rightWheelFordwardValue 180
#define rightWheelBackwardsValue 0

/* Bauderate of the Bluetooth*/
#define MI_PRIMER_KIT_DE_ROBOTICA_BLUETOOTH    38400
#define BQ_ZUM_BLUETOOTH                       19200

/* Define the posible states of the state machine of the program */
#define MANUAL_CONTROL_STATE    0
#define LINE_FOLLOWER           1
#define LIGHT_FOLLOWER          2

/* Max and min positions of the claw */
#define maxClawPosition 10
#define minClawPosition 55

/* Size of the received data buffer */
#define bufferSize 5

/* Default delay */
#define defaultDelay        10

/* Variable that controls the current state of the program */
int currentState;

/* A object from the Servo class is created for each servo */
Servo leftWheel;                       /*  Values from 0 to 180  */
Servo rightWheel;                      /*  Values from 0 to 180  */
Servo claw;                            /*  Values from 10 to 50  */

/* Variables of the line follower mode */
int rightIR;
int leftIR;
int BLACK = 0;
int WHITE = 1;

/* Variables of the light follower mode */
int rightLDR;
int leftLDR;
int lightLimitValue;

/*  A char buffer to storage the received data from the Bluetooth
    Serial */
char dataBuffer[bufferSize]; 

/* Buffer iterator */
int i = 0;

/* Number of characters availables in the Serial */
int numChar = 0;    

/* Received postion for the claw */
int posClaw = 0; 


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


void moveClaw() {

  // Check limits of the claw position
  if(posClaw < maxClawPosition) {
    posClaw = maxClawPosition;
  
  } else if (posClaw > minClawPosition) {
    posClaw = minClawPosition;
  }

  claw.write(posClaw);
  delay(defaultDelay);
}


/* Manage the buffer of data */
void checkData(char* data){  
  
   switch(data[0]) {

    /* Line follower mode button pressed */
    case 'I':
      currentState = LINE_FOLLOWER;
      break;

    /* Light follower mode button pressed */
    case 'G':
      currentState = LIGHT_FOLLOWER;
      break;

    /* Manual control mode button pressed */
    case 'M':
      currentState = MANUAL_CONTROL_STATE;
      stopWheels();
      break;

    case'S':
      /* Stop button pressed */
      stopWheels();
      break;
    
    case 'U':
      /* Up button pressed */
      goForwards();
      break;
    
    case 'D':
      /* Down button pressed */
      goBackwards();
      break;
      
    case 'L':
      /* Left button pressed */ 
      goLeft();
      break;
      
    case 'R':
      /* Right button pressed */ 
      goRight();
      break;

    /* Claw button pressed */
    case 'C':
      posClaw = strtol(data+1, NULL, 10);
      moveClaw();
      break;

    /* open claw button pressed in scheduler screen */
    case 'O': 
      posClaw -= 5;
      moveClaw();
      break;
    
    /* close claw button pressed in scheduler screen */ 
    case 'T': 
      posClaw += 5;
      moveClaw();
      break;
    
    /* full open claw button pressed in scheduler screen */ 
    case 'F': 
      posClaw = maxClawPosition;
      moveClaw();
      break;
    
  } 

  /* Empty the Serial */   
  Serial.flush();

}




/* The robot is in the line follower mode */
void followTheLine() {
  /* Read the state of the IR sensors */
  rightIR = digitalRead(pinSensorIRLeft);
  leftIR = digitalRead(pinSensorIRRight);

  if (rightIR == WHITE && leftIR == BLACK) {
    goRight();

  } else if (rightIR == BLACK && leftIR == WHITE) {
    goLeft();

  } else {
    goForwards();

  }
}


/* The robot is in the light follower mode */
void followTheLight() {
  /* Read the state of the LDR sensors */
  rightLDR = analogRead(pinSensorLDRRight);
  leftLDR = analogRead(pinSensorLDRLeft);
  
  /* If the user covers the right LDR, we stop the right wheel,
     and go forward if it is receiving light */
  if (rightLDR < lightLimitValue) {
    rightWheel.write(wheelStopValue);
    delay(defaultDelay);

  } else {
    rightWheel.write(rightWheelFordwardValue);
    delay(defaultDelay);
  }
  
    /* If the user covers the left LDR, we stop the left wheel,
     and go forward if it is receiving light */
  if (leftLDR < lightLimitValue) {
    leftWheel.write(wheelStopValue);
    delay(defaultDelay);

  } else {
    leftWheel.write(leftWheelFordwardValue);
    delay(defaultDelay);

  }
}
    

/******************************************************************
 *                             Setup                              *
 ******************************************************************/

void setup() {
  
  /* Open the Bluetooth Serial and empty it */
  //Serial.begin(BQ_ZUM_BLUETOOTH);  
  Serial.begin(MI_PRIMER_KIT_DE_ROBOTICA_BLUETOOTH); 
  Serial.flush();     
  
  /* Define the appropiate pin to each object */
  leftWheel.attach(pinLeftWheel);
  rightWheel.attach(pinRightWheel);
  claw.attach(pinClaw);

  /* Put the IR sensors as input */
  pinMode(pinSensorIRLeft, INPUT);
  pinMode(pinSensorIRRight, INPUT);

  /* The robot is stopped at the beginning */
  stopWheels();
  
  /* Put the claw in a intermediate position at the beginning */
  posClaw = 30;
  moveClaw();

  currentState = MANUAL_CONTROL_STATE;

  lightLimitValue = 200;

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

    /* Read the Bluetooth Serial and store it in the buffer */
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

  if(currentState == LINE_FOLLOWER) {
    followTheLine();
  
  } else if(currentState == LIGHT_FOLLOWER) {
    followTheLight();
  }
  
}  
  
