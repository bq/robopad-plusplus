/**  
 *
 *     ********************************************************
 *     ********************************************************
 *     ***                                                  ***
 *     ***         CRAB PRINTBOT  -   With Oscilators       ***
 *     ***                                                  ***
 *     ******************************************************** 
 *     ********************************************************
 *
 *     This program uses the library "Oscilator", created by Juan 
 *     Gonzalez-Gomez (Obijuan). 
 *
 *     Its way of moving can be modified through adjusting the 
 *     period, the amplitude and the offset values, using the 
 *     RoboPad and RoboPad++ Android apps via a Bluetooth connection.  
 *
 *     If you do not have a Bluetooth module connected, the initial 
 *     parameters for the locomotion are executed. With these values,
 *     the Crab Printbot moves slowly.
 *     
 *     
 *   ****************************************************
 *   * Fecha:31/03/2014                                 *
 *   * Autor: Ana de Prado                              *
 *   * Mail : diy@bq.com   			                        *
 *   * Licencia: GNU General Public License v3 or later *
 *   ****************************************************
 */
/******************************************************************/
/******************************************************************/


/******************************************************************
 *                           Libraries                            *
 ******************************************************************/ 

#include <Servo.h>
#include <Oscillator.h>


/******************************************************************
 *                    Definition of variables                     *
 ******************************************************************/

/* Pin definition of the board to be used                         */
#define pin_miniServoD       10  /*   Mini servo 1 - Right     -  */    
#define pin_miniServoI       11  /*   Mini servo 2 - Left      -  */ 
#define pin_miniServoC       6   /*   Mini servo 3 - Central   -  */ 


/*  Definition of the three oscillators                            */
Oscillator osc[3];

/*  Definition of the initial values of the oscillators            */  
int T=1500;      // PERIOD
int Ad=40;       // RIGHT AMPLITUDE 
int Ai=40;       // LEFT AMPLITUDE
int Offset=0;    // OFFSET

/* Phase difference between the central legs and the external ones*/ 
double dif_fase = DEG2RAD(-90); 


/* Variables of the Bluetooth communication                       */
#define bufferSize 20  // Size of the received data (buffer)
int i = 0;             // Buffer iterator
int numChar = 0;       // NNumber of characters availables in the Serial

/*  A char vector to storage the received data from the Bluetooth
    Serial           */
char dataBuffer[bufferSize]; 




/******************************************************************
 *                     Definition of functions                    *
 ******************************************************************/

/** Function that depends on the received data from the Android app. 
    This data consists on in capital letter followed by an integer 
    number. The letter denotes which  parameter of the oscillator is 
    being modified, and the integer number the value that the 
    parameter will take.
*/
void setAction(char* data) {
  
  switch(data[0]) {
    
    //-- AMPLITUDE OF THE ARCS DESCRIBED BY THE LEGS
     case 'A':    
        if (data[1]=='R'){        // RIGHT LEGS AMPLITUDE  (e.g.: AR30)
            Ad = strtol(data+2, NULL, 10);
            osc[0].SetA(Ad);
          }
        else if(data[1]=='L'){   // LEFT LEGS AMPLITUDE    (e.g.: AL30)
            Ai = strtol(data+2, NULL, 10);
            osc[1].SetA(Ai);
          }        
        break; 
 
    //-- PERIOD  (ej: T3000)
     case 'T':    
        T = strtol(data+1, NULL, 10);
        osc[0].SetT(T);
        osc[1].SetT(T);
        osc[2].SetT(T);
        break; 
    
    //-- DIFERENCE OF PHASE  (e.g.: F-90)
     case 'F':    
        dif_fase = DEG2RAD(strtol(data+1, NULL, 10));
        osc[2].SetPh(dif_fase);
        break; 


   /* //////////////////////////////////////////////////////////
   *  //       BLUETOOTH SCHEDULER APP (ROBOPAD++) CASES      //              
   *  //////////////////////////////////////////////////////////   */ 

    //-- STOP
     case 'S':    
        osc[0].SetA(0);          // Minimum amplitudes      A = 0;
        osc[1].SetA(0);
        setNormalPeriod();       // Default period       T = 1500;     
        break; 
      
    //-- RESET TO DEFAULT VALUES 
     case 'I':    
        setMaxAmplitude();       // Maximum amplitudes     A = 40;      
        setNormalPeriod();       // Default period       T = 1500;     
        dif_fase = DEG2RAD(-90); // Forward phase difference
        osc[2].SetPh(dif_fase);
        break;
    
    
    //-- Up - GO FORWARD   
     case 'U':     
        setMaxAmplitude();       // Maximum amplitudes     A = 40; 
        dif_fase = DEG2RAD(-90); // Forward phase difference
        osc[2].SetPh(dif_fase);
        break;

    //-- Down - GO BACKWARD
     case 'D':    
        setMaxAmplitude();       // Maximum amplitudes     A = 40; 
        dif_fase = DEG2RAD(90);  // Backward phase difference
        osc[2].SetPh(dif_fase);
        break;
        
    //-- Right - RIGHT   
     case 'R':     
        Ad=0;   Ai=40;           // Left maximum amplitude and right minimum amplitude  
        osc[0].SetA(Ad);
        osc[1].SetA(Ai);  
        dif_fase = DEG2RAD(-90); // Forward phase difference
        osc[2].SetPh(dif_fase);
        break;   
    
    //-- Left - LEFT   
     case 'L':     
        Ad=40;   Ai=0;           // Right maximum amplitude and right left amplitude 
        osc[0].SetA(Ad);
        osc[1].SetA(Ai);  
        dif_fase = DEG2RAD(-90); // Forward phase difference
        osc[2].SetPh(dif_fase);
        break;      
  }
}

/* Function that modifies the period in order to get a stable speed */
void setNormalPeriod() {
  
      T = 1500;        // Default period  
      osc[0].SetT(T);
      osc[1].SetT(T);
      osc[2].SetT(T);
}

/* Function that modifies the amplitude to its maximum value       */
void setMaxAmplitude() {
 
      Ad=40;   Ai=40;  // Maximum amplitude of the legs 
      osc[0].SetA(Ad);
      osc[1].SetA(Ai); 
}



/******************************************************************
 *                             Setup                              *
 ******************************************************************/

void setup() {
 
  /*  Attach each oscillator to its pin                            */
  osc[0].attach(pin_miniServoD);
  osc[1].attach(pin_miniServoI);
  osc[2].attach(pin_miniServoC);

  /* Configure the parameters of the oscillators                   */
  osc[0].SetO(0);
  osc[0].SetA(Ad);
  osc[0].SetT(T);
  osc[0].SetPh(0);
  
  osc[1].SetO(0);
  osc[1].SetA(Ai);
  osc[1].SetT(T);
  osc[1].SetPh(0);
  
  osc[2].SetO(0); //-5, -10...
  osc[2].SetA(15);
  osc[2].SetT(T);
  osc[2].SetPh(dif_fase);
  
  /* Open and empty the port where the Bluetooth is connected       */
  Serial.begin(38400); 
  Serial.flush();  
  
  /* Wait three seconds                                             */
  delay(3000);
 
}

/******************************************************************
 *                       Main program loop                        *
 ******************************************************************/

void loop() {

  /* ////////////////////////////////////////////////////////////
   * // This code is for being able to use the printbot with   //
   * // the Bluetooth module                                   // 
   * ////////////////////////////////////////////////////////////   */
  
  /* If there is something in the Bluetooth serial port             */
  if (Serial.available() > 0) { 
   
        /* Reset the iterator and clear the bufferSize              */
        i = 0;
        memset(dataBuffer, 0, sizeof(dataBuffer));  
        
        /* Wait for let the buffer fills up. Depends on the length of 
           the data, 1 ms for each character more or less.          */
        delay(bufferSize); 

        /* Number of characters availables in the Bluetooth Serial  */
        numChar = Serial.available();   
        
        /* Limit the number of characters that will be read from the
           Serial to avoid reading more than the size of the buffer */
        if (numChar > bufferSize) {
              numChar = bufferSize;
        }

        /* Read the Bluetooth Serial and store it in the buffer    */
        while (numChar--) {
            dataBuffer[i++] = Serial.read();
            delay(3);    // Wait for this process required
        } 
        setAction(dataBuffer);
    }


   /* //////////////////////////////////////////////////////////
   *  //  Update the oscillators                              //              
   *  //////////////////////////////////////////////////////////    */   
  for (int i=0; i<3; i++){
      osc[i].refresh();
  }    
}


