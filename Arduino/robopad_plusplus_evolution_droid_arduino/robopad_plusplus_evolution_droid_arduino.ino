/*  
 *
 *     ********************************************************
 *     ********************************************************
 *     ***                                                  ***
 *     ***                 Evolution Droid                  ***
 *     ***                                                  ***
 *     ******************************************************** 
 *     ********************************************************
 *
 *    Arduino code for the Evolution printbot for using with the
 *    Android RoboPad++ app. 
 *     
 *   ****************************************************
 *   * Fecha: 27/03/2015                                *
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

#define pinLeftWheel            8   
#define pinRightWheel           9   
#define pinSensorIRLeft         2   /*   Left infrared sensor     */ 
#define pinSensorIRRight        3   /*   Right infrared sensor    */
#define pinSensorLDRLeft       A2   /*   Left light sensor        */ 
#define pinSensorLDRRight      A3   /*   Right light sensor       */
#define pinUSTri                4   /*   Ultrasound trigger       */
#define pinUSEch                5   /*   Ultrasound echo          */
#define pinHead                11   /*   Ultrasound echo          */
#define pinBuzzer              12   /*   Boozer                   */

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
#define OBSTACLES_AVOIDER       3

/* Size of the received data buffer */
#define bufferSize 5

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

/* Variables of the light avoider mode */
int rightLDR;
int leftLDR;
int lightLimitValue;

/* Variables of the obstacles avoider mode */
#define US_CENTER_ANGLE                    80  
#define US_LEFT_ANGLE                     110
#define US_RIGHT_ANGLE                     50  
#define US_WAITING_FOR_RESPONSE_DELAY     500     
#define OBSTACLE_DETECTED                   0
#define OBSTACLE_NOT_DETECTED               1
#define SEARCHING_OBSTACLES_HEAD_DELAY    500
int centerObstacle = OBSTACLE_NOT_DETECTED;
int leftObstacle = OBSTACLE_NOT_DETECTED;
int rightObstacle = OBSTACLE_NOT_DETECTED;
Servo head;                      /*  Values from 0 to 180  */
boolean wasGoingFordward = true;
int lastHeadAngle; 

/* The obstacles avoider mode consumes a lot of time while it is 
  not listening to the bluetooth serial, so we have to chop all 
  the algorithm in several parts. This variables are used for 
  managing the state of this algorithm in order to listen to 
  the bluetooth serial between the different parts */
#define US_STATE_CHECK_NOT_STARTED          0   
#define US_STATE_CHECK_CENTER               1
#define US_STATE_CHECK_RIGHT                2
#define US_STATE_CHECK_LEFT                 3

int lastUsState = US_STATE_CHECK_NOT_STARTED;


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

 //bqBAT (bq US)
long TP_init_4_5() {
  digitalWrite(pinUSTri, LOW);
  delayMicroseconds(2);
  digitalWrite(pinUSTri, HIGH);
  delayMicroseconds(10);
  digitalWrite(pinUSTri, LOW);
  long microseconds = pulseIn(pinUSEch ,HIGH);
  return microseconds;
}


long distance_4_5() {
  long microseconds = TP_init_4_5();
  long distance;
  distance = microseconds/29/2;
  return distance;
}


void turnHead(int angle, int delayDuration) {
  head.write(angle);
  delay(delayDuration);
  lastHeadAngle = angle;
}


int searchObstacles(int angle) {
  int distance = 0;

  turnHead(angle, SEARCHING_OBSTACLES_HEAD_DELAY);

  distance = distance_4_5();

  if ((distance != 0) && (distance < 25)) {
    tone(pinBuzzer, 261, 300);
    delay(300);
    return OBSTACLE_DETECTED;
    
  } else {
    tone(pinBuzzer,329, 150);
    delay(150);
    
  }

  return OBSTACLE_NOT_DETECTED; 
}


int checkCenterObstacle() {

  if(!wasGoingFordward) {
    turnHead(US_CENTER_ANGLE, SEARCHING_OBSTACLES_HEAD_DELAY);
  }

  int distance = distance_4_5();

  if ((distance != 0) && (distance < 25)) {
    stopWheels();
    tone(pinBuzzer, 261, 300);
    delay(300);
    return OBSTACLE_DETECTED;
    
  } 

  return OBSTACLE_NOT_DETECTED;
}


void stopWheels() {
  leftWheel.write(wheelStopValue);
  delay(defaultDelay);

  rightWheel.write(wheelStopValue);
  delay(defaultDelay);
}


void goForwards() {

  if(lastHeadAngle != US_CENTER_ANGLE) {
    turnHead(US_CENTER_ANGLE, defaultDelay);
  }

  leftWheel.write(leftWheelFordwardValue);
  delay(defaultDelay);

  rightWheel.write(rightWheelFordwardValue);
  delay(defaultDelay);
}


void goBackwards() {

  if(lastHeadAngle != US_CENTER_ANGLE) {
    turnHead(US_CENTER_ANGLE, defaultDelay);
  }

  if(currentState == OBSTACLES_AVOIDER) {
    tone(pinBuzzer, 261, 100);
    delay(300);
    tone(pinBuzzer, 293, 100);
    delay(300);
    tone(pinBuzzer, 261, 300);
    delay(500);
  }

  leftWheel.write(leftWheelBackwardsValue);
  delay(defaultDelay);

  rightWheel.write(rightWheelBackwardsValue);
  delay(defaultDelay);
}


void goLeft() {

  if(lastHeadAngle != US_LEFT_ANGLE) {
    turnHead(US_LEFT_ANGLE, defaultDelay);
  }

  leftWheel.write(wheelStopValue);
  delay(defaultDelay);

  rightWheel.write(rightWheelFordwardValue);
  delay(defaultDelay);
}


void goRight() {

  if(lastHeadAngle != US_RIGHT_ANGLE) {
    turnHead(US_RIGHT_ANGLE, defaultDelay);
  }

  leftWheel.write(leftWheelFordwardValue);
  delay(defaultDelay);

  rightWheel.write(wheelStopValue);
  delay(defaultDelay);
}


void turnLeft() {
  goLeft();
  delay(900);
  stopWheels();
}


void turnRight() {
  goRight();
  delay(900);
  stopWheels();
}


/* Manage the buffer of data */
void checkData(char* data){  
  
   switch(data[0]) {

    /* Line follower mode button pressed */
    case 'I':
      currentState = LINE_FOLLOWER;
      break;

    /* Light avoider mode button pressed */
    case 'G':
      currentState = LIGHT_FOLLOWER;
      break;

    /* Obstacles avoider mode button pressed */
    case 'B':
      currentState = OBSTACLES_AVOIDER;
      lastUsState = US_STATE_CHECK_NOT_STARTED; 
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


/* The robot is in the light avoider mode */
void avoidTheLight() {
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


void searchCenterObstacles() {
  centerObstacle = checkCenterObstacle();
  //delay(US_WAITING_FOR_RESPONSE_DELAY);

  if (centerObstacle == OBSTACLE_NOT_DETECTED) {

    lastUsState = US_STATE_CHECK_NOT_STARTED; 

    if(!wasGoingFordward) {

      wasGoingFordward = true;

      tone(pinBuzzer, 329,100);
      delay(100);
      tone(pinBuzzer, 392,100);
      delay(100);
      tone(pinBuzzer, 494,300);
      delay(300);
    }   

    goForwards();
      
  } else {
    lastUsState = US_STATE_CHECK_CENTER;
    wasGoingFordward = false;
  }
}


void searchRightObstacles() {
    rightObstacle = searchObstacles(US_RIGHT_ANGLE);
    delay(US_WAITING_FOR_RESPONSE_DELAY);

    if (rightObstacle == OBSTACLE_NOT_DETECTED) {
      lastUsState = US_STATE_CHECK_NOT_STARTED; 
      turnRight();
    
    } else {
      lastUsState = US_STATE_CHECK_RIGHT;
    }
}


void searchLeftObstacles() {
    leftObstacle = searchObstacles(US_LEFT_ANGLE);
    delay(US_WAITING_FOR_RESPONSE_DELAY);

    if (leftObstacle == OBSTACLE_NOT_DETECTED) {
      lastUsState = US_STATE_CHECK_NOT_STARTED; 
      turnLeft();
    
    } else {
      lastUsState = US_STATE_CHECK_LEFT;
    }
}


/* The robot is in the obstacles avoider mode */
void avoidTheObstacles() {

  switch (lastUsState) {
      case US_STATE_CHECK_NOT_STARTED:
        searchCenterObstacles();
        break;

      case US_STATE_CHECK_CENTER:
        searchRightObstacles();
        break;

      case US_STATE_CHECK_RIGHT:
        searchLeftObstacles();
        break;

      case US_STATE_CHECK_LEFT:
        /* If the printbot goes backwards it should not check to go 
           fordward again, check left and right sides directly */
        lastUsState = US_STATE_CHECK_CENTER; 
        goBackwards();
        break;
  }
  
  //delay(800);
}
    

/******************************************************************
 *                             Setup                              *
 ******************************************************************/

void setup() {
  
  /* Open the Bluetooth Serial and empty it */
  Serial.begin(BQ_ZUM_BLUETOOTH);  
  //Serial.begin(MI_PRIMER_KIT_DE_ROBOTICA_BLUETOOTH); 
  Serial.flush();     
  
  /* Define the appropiate pin to each object */
  leftWheel.attach(pinLeftWheel);
  rightWheel.attach(pinRightWheel);
  head.attach(pinHead);

  /* Put the IR sensors as input */
  pinMode(pinSensorIRLeft, INPUT);
  pinMode(pinSensorIRRight, INPUT);

  /* US sensors */
  pinMode(pinUSTri, OUTPUT);
  pinMode(pinUSEch, INPUT);

  /* The robot is stopped at the beginning */
  stopWheels();

  /* Point the head fordwards */ 
  turnHead(US_CENTER_ANGLE, defaultDelay);

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
    avoidTheLight();
  
  } else if(currentState == OBSTACLES_AVOIDER) {
    avoidTheObstacles();
  } 
  
}  
  
